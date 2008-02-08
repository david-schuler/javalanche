package org.softevo.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.run.analyze.KilledAnalyzer;
import org.softevo.mutation.run.analyze.KilledForClassAnalyzer;
import org.softevo.mutation.run.analyze.MutatedUnmutatedAnalyzer;
import org.softevo.mutation.run.analyze.ShowCheckErrorsAnalyzer;
import org.softevo.mutation.run.analyze.TestsAnalyzer;
import org.softevo.mutation.run.analyze.UnMutatedTestAnalyzer;

public class CompareWithUnmutated {

	private static Logger logger = Logger.getLogger(CompareWithUnmutated.class);



	public static void main(String[] args) {

		MutatedUnmutatedAnalyzer[] allAnalyzers = new MutatedUnmutatedAnalyzer[] {
				new TestsAnalyzer(), new KilledAnalyzer(), new KilledForClassAnalyzer(),
				new UnMutatedTestAnalyzer() };

//		checkResults(allAnalyzers);
		checkResults(new MutatedUnmutatedAnalyzer[]{new ShowCheckErrorsAnalyzer()});
	}

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

	private static List doQuery(Session session, String prefix) {
		String queryString = "SELECT {m1.*}, {m2.*} FROM Mutation m1 INNER JOIN Mutation m2 ON m1.lineNumber = m2.lineNumber AND m1.className = m2.className WHERE m1.className LIKE '" +prefix +"%' AND m1.mutationType != 0 AND m2.mutationType = 0 AND m1.mutationResult_id IS NOT NULL AND m2.mutationResult_id IS NOT NULL";
		SQLQuery query = session.createSQLQuery(queryString);
		query.addEntity("m1", Mutation.class);
		query.addEntity("m2", Mutation.class);
		List l = query.list();
		logger.info("Got " + l.size() + " Mutations");
		return l;
	}

}
