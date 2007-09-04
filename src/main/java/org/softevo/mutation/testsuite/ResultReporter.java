package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestResult;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.OldMutationResult;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.QueryManager;

public class ResultReporter {

	private List<OldMutationResult> mutationResults = new ArrayList<OldMutationResult>();

	public void report(
			TestResult mutationTestResult, Mutation mutation) {
		SingleTestResult mutated = new SingleTestResult(mutationTestResult);
		QueryManager.updateMutation(mutation, mutated);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (OldMutationResult cr : mutationResults) {
			sb.append(cr.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Save collected results to database
	 */
//	public void toDb() {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
//		for (OldMutationResult mr : mutationResults) {
//			session.save(mr);
//		}
//		tx.commit();
//		session.close();
//
//	}
}
