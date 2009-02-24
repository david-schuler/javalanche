package de.unisb.cs.st.javalanche.mutation.analyze;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.analyze.invariant.TempInvariantAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.tracer.NewTracerAnalyzer;

/**
 * Analyzes the mutation results for a project. Either a list (comma separated)
 * of {@link MutationAnalyzer} is given via the property
 * "javalanche.mutation.analyzers" or a set of default analyzers is used.
 *
 * @author David Schuler
 *
 */
public class AnalyzeMain {

	public static final String ANALYZERS_KEY = "javalanche.mutation.analyzers";

	public static void main(String[] args) {
		MutationAnalyzer[] analyzers = getAnalyzersFromProperty();
		if (analyzers != null && analyzers.length > 0) {
			analyzeMutations(analyzers);
		} else {
			analyzeMutations(new MutationAnalyzer[] {
//					new TempInvariantAnalyzer(),
//					new MutationResultAnalyzer(),
//					new CheckAnalyzer()
					new NewTracerAnalyzer()
					//new InvariantSplitAnalyzer()
					/*
		 new KilledAnalyzer(),
		 new InvariantAnalyzer(),
		 new ManualAnalyzer()
		*/
		 /* , new AssertAnalyzer() */
			/* , new AspectJAnalyzer() */
			/* , new IdAnalyzer() */
			// new CoverageAnalyzer()
//			new DebugAnalyzer()
		 });
		}
	}

	@SuppressWarnings("unchecked")
	private static MutationAnalyzer[] getAnalyzersFromProperty() {
		String property = System.getProperty(ANALYZERS_KEY);
		List<MutationAnalyzer> analyzers = new ArrayList<MutationAnalyzer>();
		if (property != null) {
			String[] split = property.split(",");
			for (String analyzer : split) {
				try {
					Class<? extends MutationAnalyzer> analyzerClass = (Class<? extends MutationAnalyzer>) Class
							.forName(analyzer);
					MutationAnalyzer analyzerInstance = analyzerClass
							.newInstance();
					analyzers.add(analyzerInstance);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return analyzers.toArray(new MutationAnalyzer[0]);
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
//		Session session = HibernateServerUtil.getSessionFactory(Server.KUBRICK).openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE className LIKE '" + prefix
						+ "%'");
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		StringBuilder sb = new StringBuilder();
		sb
				.append("--------------------------------------------------------------------------------\n");
		for (MutationAnalyzer mutationAnalyzer : mutationAnalyzers) {

			String analyzeResult = mutationAnalyzer.analyze(mutations);

			sb.append("Results from " + mutationAnalyzer.getClass() + "\n");
			sb.append(analyzeResult);
			sb
					.append("\n--------------------------------------------------------------------------------\n");
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
