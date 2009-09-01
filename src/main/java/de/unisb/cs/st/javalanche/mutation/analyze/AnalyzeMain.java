package de.unisb.cs.st.javalanche.mutation.analyze;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.transform.impl.AddStaticInitTransformer;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

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
		List<MutationAnalyzer> analyzers = new ArrayList<MutationAnalyzer>();
		analyzers.add(new MutationResultAnalyzer());
		List<MutationAnalyzer> analyzersFromProperty = getAnalyzersFromProperty();
		if (analyzersFromProperty != null && analyzersFromProperty.size() > 0) {
			analyzers.addAll(analyzersFromProperty);
		}
		analyzeMutations(analyzers);
	}

	@SuppressWarnings("unchecked")
	private static List<MutationAnalyzer> getAnalyzersFromProperty() {
		String property = System.getProperty(ANALYZERS_KEY);
		List<MutationAnalyzer> analyzers = new ArrayList<MutationAnalyzer>();
		if (property != null) {
			String[] split = property.split(",");
			for (String analyzer : split) {
				try {
					analyzer = analyzer.trim();
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
		return analyzers;
	}

	/**
	 * 
	 * Analyzes the mutation results for a project
	 * 
	 * @param analyzers
	 *            the mutationAnalyzers to use
	 */
	public static void analyzeMutations(
			List<MutationAnalyzer> analyzers) {
		String prefix = PROJECT_PREFIX;
		if (prefix == null) {
			throw new RuntimeException("no prefix set");
		}
		analyzeMutations(analyzers, prefix);
	}

	/**
	 * 
	 * Analyzes the mutation results for a project.
	 * 
	 * @param mutationResultAnalyzers
	 *            the mutationAnalyzers to use
	 * 
	 * @param analyzers
	 * @param prefix
	 *            the prefix for the mutations to analyze
	 */
	@SuppressWarnings("unchecked")
	private static void analyzeMutations(List<MutationAnalyzer> analyzers,
			String prefix) {
		Session session = HibernateUtil.openSession();
		// Session session =
		// HibernateServerUtil.getSessionFactory(Server.KUBRICK).openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE className LIKE '" + prefix
						+ "%'");
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		HtmlReport report = new HtmlAnalyzer().analyze(mutations);
		StringBuilder sb = new StringBuilder();
		sb
				.append("--------------------------------------------------------------------------------\n");

		for (MutationAnalyzer mutationAnalyzer : analyzers) {

			String analyzeResult = mutationAnalyzer.analyze(mutations, report);

			String str = "Results from "
					+ mutationAnalyzer.getClass().getName() + "\n";
			report.addSummary(str, analyzeResult);
			sb.append(str);
			sb.append(analyzeResult);
			sb
					.append("\n--------------------------------------------------------------------------------\n");
		}
		long l = getNumberOfMutationsWithoutResult(session, prefix);
		System.out.println("Analyzed Results for mutations with prefix: "
				+ PROJECT_PREFIX);
		System.out.println("No results for " + l + " mutations");
		System.out.println(sb.toString());
		report.report();
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
