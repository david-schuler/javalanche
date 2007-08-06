package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationResult;
import org.softevo.mutation.results.SingleTestResult;

import junit.framework.TestResult;

public class ResultReporter {

	private List<MutationResult> list = new ArrayList<MutationResult>();

	public void report(TestResult normalTestResult,
			TestResult mutationTestResult,
			Mutation mutation) {
		SingleTestResult normal = new SingleTestResult(normalTestResult);
		SingleTestResult mutated = new SingleTestResult(mutationTestResult);
		MutationResult combinedResult = new MutationResult(normal, mutated,
				mutation);
		list.add(combinedResult);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MutationResult cr : list) {
			sb.append(cr.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
