package org.softevo.mutation.analyze;

import static org.softevo.mutation.properties.MutationProperties.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class AnalyzeMain {

	public static void main(String[] args) {
		// analyzeMutations(new MutationResultAnalyzer());
		boolean didOutput = false;
		if (args.length >= 1) {
			String arg = args[0].toLowerCase();
			if (arg.equals("exp")) {
				didOutput = true;
				analyzeMutations(new MutationAnalyzer[] { new ExperimentAnalyzer() });
			}
		}
		if (!didOutput) {
			analyzeMutations(new MutationAnalyzer[] { new InvariantAnalyzer(),
					new MutationResultAnalyzer(), new KilledAnalyzer(),
					new AssertAnalyzer() ,/*new AspectJAnalyzer()*/});

		}
	}

	private static void analyzeMutations(
			MutationAnalyzer[] mutationResultAnalyzers) {
		String prefix = PROJECT_PREFIX;
		if (prefix == null) {
			throw new RuntimeException("no prefix set");
		}

		analyzeMutations(mutationResultAnalyzers, prefix);
	}

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
