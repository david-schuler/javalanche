package org.softevo.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;

/**
 *
 * Deletes the results from the database.
 *
 * @author David Schuler
 *
 */
public class ResultDeleter {

	private static Logger logger = Logger.getLogger(ResultDeleter.class);

	@SuppressWarnings("unchecked")
	public static void deleteAllMutationResult() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("from Mutation where mutationResult IS NOT NULL");
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
		deleteAllMutationResult();
	}

}
