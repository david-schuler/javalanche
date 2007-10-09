package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.OldMutationResult;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.QueryManager;

public class ResultReporter {

	private static Logger logger = Logger.getLogger(ResultReporter.class);

	private static Mutation actualMutation;

	private static Set<String> touchingTestCases = new HashSet<String>();

	private static String actualTestCase;

	private List<OldMutationResult> mutationResults = new ArrayList<OldMutationResult>();

	public void report(TestResult mutationTestResult, Mutation mutation,
			MutationTestListener mutationTestListener) {
		SingleTestResult mutated = new SingleTestResult(mutationTestResult,
				mutationTestListener, touchingTestCases);
		QueryManager.updateMutation(mutation, mutated);
		touchingTestCases.clear();
		actualMutation = null;
		actualTestCase = null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (OldMutationResult cr : mutationResults) {
			sb.append(cr.toString());
			sb.append('\n');
		}
		touch(123);
		return sb.toString();
	}

	public static void touch(long mutationID) {
		logger.info("Touch called by mutated code");
		long expectedID = actualMutation.getId();
		if (mutationID != expectedID) {
			throw new RuntimeException("Expected ID did not match reported ID"
					+ actualMutation.getId() + " " + mutationID);
		} else {
			touchingTestCases.add(actualTestCase);
		}
	}

	/**
	 * @return the actualTestCase
	 */
	public static String getActualTestCase() {
		return actualTestCase;
	}

	/**
	 * @param actualTestCase
	 *            the actualTestCase to set
	 */
	public static void setActualTestCase(String actualTestCase) {
		ResultReporter.actualTestCase = actualTestCase;
	}

	/**
	 * @return the actualMutation
	 */
	public static Mutation getActualMutation() {
		return actualMutation;
	}

	/**
	 * @param actualMutation
	 *            the actualMutation to set
	 */
	public static void setActualMutation(Mutation actualMutation) {
		ResultReporter.actualMutation = actualMutation;
	}
}
