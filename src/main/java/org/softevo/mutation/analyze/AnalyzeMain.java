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
//		analyzeMutations(new MutationResultAnalyzer());
		analyzeMutations(new InvariantAnalyzer());
	}

	private static void analyzeMutations(
			MutationAnalyzer mutationResultAnalyzer) {
		String prefix = PROJECT_PREFIX;
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
		System.out.println("Analyzed Results for mutations with prefix: " + PROJECT_PREFIX);
		System.out.println("No results for " + l +  " mutations");
		System.out.println(analyzeResult);
		tx.commit();
		session.close();
	}
}
