package de.st.cs.unisb.javalanche.analyze;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;
import de.st.cs.unisb.javalanche.results.persistence.QueryManager;

/**
 * Fetches one or more mutations from the database an prints it to the console.
 */
public class ShowMutation {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <mutationID> [<mutationID>]*");
			System.out.println("Showing one mutation");
			showMutation(12556);
		}
		for (int i = 0; i < args.length; i++) {
			long mutationID = Long.parseLong(args[i]);
			showMutation(mutationID);
		}
	}

	/**
	 * Fetches one a mutation from the database an prints it to the console.
	 *
	 * @param id
	 *            the id of the mutation to print
	 */
	@SuppressWarnings("unchecked")
	private static void showMutation(long id) {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("FROM Mutation WHERE id = :id");
		query.setLong("id", id);
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		for (Mutation mutation : mutations) {
			System.out.println(mutation);
			System.out.println("TestCases for mutation: " + QueryManager.getTestsCollectedData(mutation));
		}
		tx.commit();
		session.close();
	}
}
