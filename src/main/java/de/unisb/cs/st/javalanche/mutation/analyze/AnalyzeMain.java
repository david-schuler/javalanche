package de.unisb.cs.st.javalanche.mutation.analyze;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Analyzes the mutation results for a project.
 *
 * @author David Schuler
 *
 */
public class AnalyzeMain {

	public static void main(String[] args) {
		// analyzeMutations(new MutationResultAnalyzer());
		boolean didOutput = false;
		if (args.length >= 1) {
			String arg = args[0].toLowerCase();
			if (arg.equals("exp")) {
				didOutput = true;
				analyzeMutations(new MutationAnalyzer[] { new ExperimentAnalyzer2() });
			}
		}

		if (!didOutput) {
			analyzeMutations(new MutationAnalyzer[] { new Invariant2Analyzer() });
			// analyzeMutations(new MutationAnalyzer[] { new
			// InvariantAnalyzer(),
			// new MutationResultAnalyzer(), new KilledAnalyzer(),
			// new AssertAnalyzer(),/* new AspectJAnalyzer() */});

		}
	}

	/**
	 *
	 * Analyzes the mutation results for a project
	 *
	 * @param mutationResultAnalyzers
	 *            the mutationAnalyzers to use
	 */
	private static void analyzeMutations(
			MutationAnalyzer[] mutationResultAnalyzers) {
		String prefix = PROJECT_PREFIX;
		if (prefix == null) {
			throw new RuntimeException("no prefix set");
		}

		analyzeMutations(mutationResultAnalyzers, prefix);
	}

	/**
	 *
	 * Analyzes the mutation results for a project.
	 *
	 * @param mutationResultAnalyzers
	 *            the mutationAnalyzers to use
	 *
	 * @param mutationAnalyzers
	 * @param prefix
	 *            the prefix for the mutations to analyze
	 */
	@SuppressWarnings("unchecked")
	private static void analyzeMutations(MutationAnalyzer[] mutationAnalyzers,
			String prefix) {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE className LIKE '" + prefix
						+ "%'");
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		StringBuilder sb = new StringBuilder();
		for (MutationAnalyzer mutationAnalyzer : mutationAnalyzers) {
			String analyzeResult = mutationAnalyzer.analyze(mutations);
			sb.append("Results from " + mutationAnalyzer.getClass() + "\n");
			sb.append(analyzeResult);
		}
		long l = getNumberOfMutationsWithoutResult(session, prefix);
		System.out.println("Analyzed Results for mutations with prefix: "
				+ PROJECT_PREFIX);
		System.out.println("No results for " + l + " mutations");
		System.out.println(sb.toString());
		tx.commit();
		session.close();
	}

	/**
	 * Returns the number of mutations without a result.
	 *
	 * @param session
	 *            the hibernate session to use
	 * @param prefix
	 *            the prefix for the classes
	 * @return the number of mutations without a result
	 */
	@SuppressWarnings("unchecked")
	private static long getNumberOfMutationsWithoutResult(Session session,
			String prefix) {
		String countQueryString = "SELECT count(*) FROM Mutation WHERE mutationResult = null AND className LIKE '"
				+ prefix + "%'";
		Query countQuery = session.createQuery(countQueryString);
		List countList = countQuery.list();
		long l = QueryManager.getResultFromCountQuery(countList);
		return l;
	}
}
