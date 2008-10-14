package de.unisb.cs.st.javalanche.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

/**
 * Delete all Mutations from DB that have the word test in their classname.
 *
 * @author David Schuler
 *
 */
public class DeleteTestCaseMutations {

	private static Logger logger = Logger
			.getLogger(DeleteTestCaseMutations.class);

	public static void main(String[] args) {
		deleteMutations();
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT * from Mutation WHERE className LIKE '%test%'";
		Query query = session.createSQLQuery(queryString).addEntity(
				Mutation.class);
		List<Mutation> testCaseMutations = query.list();

		for (Mutation m : testCaseMutations) {
			if (m.getMutationResult() != null) {
				session.delete(m.getMutationResult());
			}
			session.delete(m);
		}
		logger.info("Deleted " + testCaseMutations.size() + " Mutations");
		tx.commit();
		session.close();
	}
}
