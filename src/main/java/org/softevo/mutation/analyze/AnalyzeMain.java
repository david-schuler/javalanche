package org.softevo.mutation.analyze;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class AnalyzeMain {
	public static void main(String[] args) {
		analyzeMutations(new MutationResultAnalyzer());
	}

	private static void analyzeMutations(
			MutationResultAnalyzer mutationResultAnalyzer) {
		String prefix = MutationProperties.PROJECT_PREFIX;
		if (prefix == null) {
			throw new RuntimeException("no prefix set");
		}
		analyzeMutations(mutationResultAnalyzer, prefix);
	}

	@SuppressWarnings("unchecked")
	private static void analyzeMutations(MutationAnalyzer mutationAnalyzer,
			String prefix) {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE mutationResult_id != null AND className LIKE '"
						+ prefix + "%'");
		// query.setString("prefix", prefix);
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		String analyzeResult = mutationAnalyzer.analyze(mutations);
		String countQueryString = "SELECT count(*) FROM Mutation WHERE mutationResult = null";
		Query countQuery = session.createQuery(countQueryString);
		List countList = countQuery.list();
		long l = QueryManager.getResultFromCountQuery(countList);
		System.out.println(l + " mutations where not covered by tests");
		System.out.println(analyzeResult);
		tx.commit();
		session.close();
	}
}
