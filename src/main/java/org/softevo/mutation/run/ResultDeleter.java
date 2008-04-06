package org.softevo.mutation.run;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

/**
 *
 * Deletes the results from the database.
 *
 * @author David Schuler
 *
 */
public class ResultDeleter {

	private static Logger logger = Logger.getLogger(ResultDeleter.class);

	public static void deleteAllWithPrefix() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		String query = "FROM Mutation WHERE mutationResult IS NOT NULL className LIKE '"
				+ prefix + "%'";
		deleteMutationResultsFromQuery(query);
	}

	@SuppressWarnings("unchecked")
	public static void deleteAllMutationResult() {
		String query = "from Mutation where mutationResult IS NOT NULL";
		deleteMutationResultsFromQuery(query);
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String query) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String.format(query);
		Query q = session.createQuery(queryString);
		List<Mutation> mutations = q.list();
		for (Mutation m : mutations) {
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				m.setMutationResult(null);
				session.delete(singleTestResult);
			}
		}
		logger.info(String.format("Deleting %d mutation results", mutations
				.size()));
		tx.commit();
		session.close();
	}

	public static void main(String[] args) {

		if (args.length >= 1) {
			if (args[0].toLowerCase().equals("all")) {
				if (MutationProperties.PROJECT_PREFIX != null) {

				} else {
					deleteAllMutationResult();
				}
			} else if (args[0].toLowerCase().equals("files")) {
				deleteFromFiles();
			} else {
				deleteMutationsResults(args[0]);
			}
		} else {
			System.out.print("Specify an option: a file or all ");
		}
	}

	private static void deleteMutationsResults(String filename) {
		logger.info("Trying to delete ids from file: " + filename);
		List<Long> ids = Io.getIDsFromFile(new File(filename));
		List<Mutation> mutationsFromDbByID = QueryManager
				.getMutationsFromDbByID(ids.toArray(new Long[0]));
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		for (Mutation m : mutationsFromDbByID) {
			deleteMutationResult(session, m);
		}
		logger.info(String.format("Deleting %d mutation results",
				mutationsFromDbByID.size()));
		tx.commit();
		session.close();
	}

	private static void deleteMutationResult(Session session, Mutation m) {
		deleteMutationResult(session, m, true);
	}

	private static void deleteMutationResult(Session session, Mutation m,
			boolean deleteUnmutated) {
		session.load(m, m.getId());
		logger.info("Loading mutation with  following id: " + m.getId());

		SingleTestResult singleTestResult = m.getMutationResult();
		if (singleTestResult != null) {
			m.setMutationResult(null);
			session.delete(singleTestResult);
		}
		if (deleteUnmutated) {
			if (QueryManager.hasUnmutated(m)) {
				Mutation unMutated = QueryManager.generateUnmutated(m);
				unMutated = (Mutation) session.get(Mutation.class, unMutated
						.getId());
				if (unMutated != null)
					logger
							.info("Loading unmutated mutation with following id: "
									+ unMutated.getId());
				// session.load(unMutated, unMutated.getId());
				SingleTestResult unMutatedSingleTestResult = unMutated
						.getMutationResult();
				if (unMutatedSingleTestResult != null) {
					unMutated.setMutationResult(null);
					session.delete(unMutatedSingleTestResult);
				}
			}
		}
	}

	private static void deleteFromFiles() {
		File dir = new File(MutationProperties.RESULT_DIR);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith("mutation-task")) {
					return true;
				}
				return false;
			}
		});
		for (File file : files) {
			deleteResults(file);
		}
	}

	private static void deleteResults(File file) {
		System.out.println("\nDeleting results from file: " + file.toString());
		List<Long> idList = Io.getIDsFromFile(file);
		List<Mutation> mutations = QueryManager.getMutationsFromDbByID(idList
				.toArray(new Long[0]));
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		for (Mutation m : mutations) {
			deleteMutationResult(session, m);
		}
		logger.info(String.format("Deleting %d mutation results", mutations
				.size()));
		tx.commit();
		session.close();
	}

}
