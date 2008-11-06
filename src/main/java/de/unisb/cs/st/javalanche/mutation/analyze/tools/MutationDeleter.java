package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

/**
 *
 * Class with a main method that deletes all mutation with a specified prefix
 * from the database.
 *
 * @author David Schuler
 *
 */
public class MutationDeleter {

	private static Logger logger = Logger.getLogger(MutationDeleter.class);

	/**
	 * Deletes all mutation test results for classes with the specified
	 * {@link MutationProperties.PROJECT_PREFIX}.
	 */
	public static void deleteAllWithPrefix() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		String query = "DELETE FROM Mutation WHERE className LIKE '" + prefix
				+ "%'";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes all mutations that match the given query.
	 *
	 * @param queryString
	 *            query that is used to delete the mutations.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String queryString) {
		logger.info("Deleting with this query: " + queryString);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(queryString);
		int deleted = q.executeUpdate();
		logger.info(String.format("Deleted %d mutations", deleted));
		tx.commit();
		session.close();
	}

	/**
	 * Deletes all mutation with a specified prefix from the database.
	 *
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		MutationProperties.checkProperty(MutationProperties.PROJECT_PREFIX_KEY);
		deleteAllWithPrefix();
	}
}
