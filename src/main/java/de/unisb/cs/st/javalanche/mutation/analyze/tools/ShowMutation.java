package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil;

/**
 * Fetches one or more mutations from the database an prints it to the console.
 */
public class ShowMutation {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <mutationID> [<mutationID>]*");
			System.out.println("Showing one default mutation".length());
			// showMutation(1424030);
			// showMutation(1454789);
			showMutation(1449387);
		}

		int length = "".split("\n").length;
		PrintStream outBack = System.out;
		PrintStream errBack = System.err;
		ByteArrayOutputStream outCapt = new ByteArrayOutputStream();
		ByteArrayOutputStream errCapt = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outCapt));
		System.setErr(new PrintStream(errCapt));
		System.setOut(outBack);
		System.setErr(errBack);
		System.out.println("TEST OUT" + outCapt.toString());
		System.out.println("TEST ERR" + errCapt.toString());
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

		SessionFactory sessionFactory = HibernateServerUtil
				.getSessionFactory(HibernateServerUtil.Server.HOBEL);
		QueryManager.setSessionFactory(sessionFactory);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("FROM Mutation WHERE id = :id");
		query.setLong("id", id);
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		int count = 0;
		for (Mutation mutation : mutations) {
			System.out.println((count++) + "  " + mutation);
			System.out.println("TestCases for mutation: "
					+ MutationCoverageFile.getCoverageDataId(mutation.getId())
							.size());
			boolean coveredMutation = QueryManager.isCoveredMutation(mutation);
			System.out.println("Is covered: " + coveredMutation);
		}
		tx.commit();
		session.close();
	}
}
