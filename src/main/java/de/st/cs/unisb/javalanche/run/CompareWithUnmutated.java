package de.st.cs.unisb.javalanche.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.st.cs.unisb.javalanche.properties.MutationProperties;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;
import de.st.cs.unisb.javalanche.run.analyze.KilledAnalyzer;
import de.st.cs.unisb.javalanche.run.analyze.KilledForClassAnalyzer;
import de.st.cs.unisb.javalanche.run.analyze.MutatedUnmutatedAnalyzer;
import de.st.cs.unisb.javalanche.run.analyze.ShowCheckErrorsAnalyzer;
import de.st.cs.unisb.javalanche.run.analyze.TestsAnalyzer;
import de.st.cs.unisb.javalanche.run.analyze.UnMutatedTestAnalyzer;

public class CompareWithUnmutated {

	private static Logger logger = Logger.getLogger(CompareWithUnmutated.class);

	public static void main(String[] args) {
		MutatedUnmutatedAnalyzer[] analyzers = new MutatedUnmutatedAnalyzer[] { new KilledAnalyzer() };
		if (args.length >= 1) {
			if (args[0].equals("all")) {
				MutatedUnmutatedAnalyzer[] allAnalyzers = new MutatedUnmutatedAnalyzer[] {
						new TestsAnalyzer(), new KilledAnalyzer(),
						new KilledForClassAnalyzer(),
						new UnMutatedTestAnalyzer() };
				analyzers = allAnalyzers;
			} else if (args[0].equals("check")) {
				analyzers = new MutatedUnmutatedAnalyzer[] { new ShowCheckErrorsAnalyzer() };
			}
		}
		checkResults(analyzers);
	}

	@SuppressWarnings("unchecked")
	public static void checkResults(MutatedUnmutatedAnalyzer[] analyzers) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List l = doQuery(session, MutationProperties.PROJECT_PREFIX);
		for (Object o : l) {
			Object[] array = (Object[]) o;
			Mutation mutated = (Mutation) array[0];
			Mutation unMutated = (Mutation) array[1];
			for (MutatedUnmutatedAnalyzer analyzer : analyzers) {
				analyzer.handleMutation(mutated, unMutated);
			}
		}
		tx.commit();
		session.close();
		for (MutatedUnmutatedAnalyzer analyzer : analyzers) {
			System.out.println(analyzer.getResults());
		}
	}

	@SuppressWarnings("unchecked")
	private static List doQuery(Session session, String prefix) {
		String queryString = "SELECT {m1.*}, {m2.*} FROM Mutation m1 "
				+ "INNER JOIN Mutation m2 ON m1.lineNumber = m2.lineNumber AND m1.className = m2.className  "
				+ "WHERE m1.className LIKE '" + prefix + "%' "
				+ "AND m1.mutationType != 0 " + "AND m2.mutationType = 0 "
				+ "AND m1.mutationResult_id IS NOT NULL "
				+ "AND m2.mutationResult_id IS NOT NULL";
		SQLQuery query = session.createSQLQuery(queryString);
		System.out.println(queryString);
		query.addEntity("m1", Mutation.class);
		query.addEntity("m2", Mutation.class);
		List l = query.list();
		logger.info("Got " + l.size() + " Mutations");
		return l;
	}

}
