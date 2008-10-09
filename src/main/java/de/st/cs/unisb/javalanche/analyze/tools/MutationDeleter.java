package de.st.cs.unisb.javalanche.analyze.tools;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.st.cs.unisb.javalanche.properties.MutationProperties;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;

/**
 *
 * Deletes the mutations from the database.
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
		String query = "DELETE FROM Mutation WHERE className LIKE '" + prefix + "%'";
		deleteMutationResultsFromQuery(query);
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String queryString) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(queryString);
		int deleted = q.executeUpdate();
		logger.info(String.format("Deleted %d mutations", deleted));
		tx.commit();
		session.close();
	}

	public static void main(String[] args) {
		MutationProperties.checkProperty(MutationProperties.PROJECT_PREFIX_KEY);
		deleteAllWithPrefix();
	}
}
