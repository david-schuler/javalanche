package de.st.cs.unisb.javalanche.analyze.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.st.cs.unisb.ds.util.Formater;
import de.st.cs.unisb.javalanche.properties.MutationProperties;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationTestResult;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;

/**
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

	/**
	 * Deletes all mutation test results from the database.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteAllMutationResult() {
		String query = "from Mutation where mutationResult IS NOT NULL";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes results for all mutations that are returned by the given qeury.
	 *
	 * @param query
	 *            Query to get the mutations that are
	 */
	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String query) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(query);
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

	/**
	 * Delet mutation result for mutation with given id.
	 *
	 * @param id
	 *            the id of the mutation to delete
	 */
	private static void deleteMutationsResultsForId(long id) {
		String query = "from mutation where id=" + id;
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes mutation resutlts from the database. If a project prefix is
	 * specified, all results of mutations for this project are deleted. If an
	 * id is given as argument the result of the mutation with the given id is
	 * deleted.
	 *
	 * @param args
	 */
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
				} else {
					deleteMutationsResultsForId(Long.parseLong(args[0]));
				}
			} else {
				System.out.print("Specify an option: a file or all ");
			}
		}
	}

}
