package de.st.cs.unisb.javalanche.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;

/**
 * Delete all Mutations of type unmutated=0 from DB.
 *
 * @author David Schuler
 *
 */
public class DeleteUnmutated {

	private static Logger logger = Logger
			.getLogger(DeleteUnmutated.class);

	public static void main(String[] args) {
		deleteMutations();
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT * from Mutation WHERE mutationType = 0";
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
