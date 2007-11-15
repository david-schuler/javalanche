package org.softevo.mutation.run;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
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
		if (args.length >= 1) {
			if (args[0].toLowerCase().equals("all")) {
				deleteAllMutationResult();
			} else {
				deleteMutationsResults(args[0]);
			}
		}else{
			System.out.print("Specify an option: a file or all ");
		}
	}

	private static void deleteMutationsResults(String filename) {
		logger.info("Trying to delete ids from file" + filename);
		List<Long> ids = Io.getIDsFromFile(new File(filename));
		List<Mutation> mutationsFromDbByID = QueryManager
				.getMutationsFromDbByID(ids.toArray(new Long[0]));
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		for (Mutation m : mutationsFromDbByID) {
			session.load(m, m.getId());
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				m.setMutationResult(null);
				session.delete(singleTestResult);
			}
		}
		logger.info(String.format("Deleting %d mutation results",
				mutationsFromDbByID.size()));
		tx.commit();
		session.close();
	}

}
