package org.softevo.mutation.analyze.tools;

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
import org.softevo.mutation.results.MutationTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.util.Formater;

/**
 *
 * Deletes the mutation test results from the database.
 *
 * @author David Schuler
 *
 */
public class ResultDeleter {

	private static Logger logger = Logger.getLogger(ResultDeleter.class);

	/**
	 * Deletes all mutation test results for classes with the specified
	 * {@link MutationProperties.PROJECT_PREFIX}.
	 */
	public static void deleteAllWithPrefix() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		String query = "FROM Mutation WHERE mutationResult IS NOT NULL AND className LIKE '"
				+ prefix + "%'";
		deleteMutationResultsFromQuery(query);
	}

	@SuppressWarnings("unchecked")
	public static void deleteAllMutationResult() {
		String query = "from Mutation where mutationResult IS NOT NULL";
		deleteMutationResultsFromQuery(query);
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String queryString) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(queryString);
		List<Mutation> mutations = q.list();
		int deletes = 0, flushs = 0;
		for (Mutation m : mutations) {
			MutationTestResult result = m.getMutationResult();
			if (result != null) {
				m.setMutationResult(null);
				session.delete(result);
				deletes++;
			}
			if (deletes > 20) {
				// 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				long startFlush = System.currentTimeMillis();
				flushs++;
				logger.info("Doing temporary flush " + flushs);
				session.flush();
//				session.clear();
				long timeFlush = System.currentTimeMillis() - startFlush;
				logger.info("Flush took: "
						+ Formater.formatMilliseconds(timeFlush));
				deletes = 0;
			}
		}
		logger.info(String.format("Deleting %d mutation results", mutations
				.size()));
		tx.commit();
		session.close();
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

	private static void deleteMutationsResultsForId(long id) {
		logger.info("Trying to delete results for id: " + id);
		List<Mutation> mutationsFromDbByID = QueryManager
				.getMutationsFromDbByID(new Long[] { id });
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

		MutationTestResult singleTestResult = m.getMutationResult();
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
				MutationTestResult unMutatedSingleTestResult = unMutated
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

	public static void main(String[] args) {
		if (MutationProperties.PROJECT_PREFIX != null) {
			deleteAllWithPrefix();
		} else {
			if (args.length >= 1) {
				if (args[0].toLowerCase().equals("all")) {
					if (MutationProperties.PROJECT_PREFIX != null) {
						deleteAllWithPrefix();
					} else {
						deleteAllMutationResult();
					}
				} else if (args[0].toLowerCase().equals("files")) {
					deleteFromFiles();
				} else {
					deleteMutationsResultsForId(Long.parseLong(args[0]));
				}
			} else {
				System.out.print("Specify an option: a file or all ");
			}
		}
	}

}
