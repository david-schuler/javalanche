package org.softevo.mutation.runtime;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

/**
 *
 * Class that stores the results of the TestCases.
 *
 * @author David Schuler
 *
 */
public class ResultReporter {

	private static Logger logger = Logger.getLogger(ResultReporter.class);

	private static Mutation actualMutation;

	private Map<Mutation, SingleTestResult> results = new HashMap<Mutation, SingleTestResult>();

	private static Set<String> touchingTestCases = new HashSet<String>();

	private static Set<Mutation> reportedMutations = new HashSet<Mutation>();

	private static Set<Mutation> touchedMutations = new HashSet<Mutation>();

	private static Set<Mutation> unMutatedMutations = new HashSet<Mutation>();

	private static String actualTestCase;

	private static boolean firstTouch = true;

	public synchronized void report(TestResult mutationTestResult,
			Mutation mutation, MutationTestListener mutationTestListener) {
		if (mutationTestResult == null || mutation == null || mutation == null) {
			throw new IllegalArgumentException("Argument was null: "
					+ mutationTestResult == null ? "mutationTestResult" : ""
					+ mutation == null ? ", mutation" : ""
					+ mutationTestListener == null ? ", mutationTestListener"
					: "");
		}
		SingleTestResult mutationSingleTestResult = new SingleTestResult(
				mutationTestResult, mutationTestListener, touchingTestCases);
		// QueryManager.updateMutation(mutation, mutated);
		results.put(mutation, mutationSingleTestResult);
		if (!reportedMutations.contains(mutation)) {
			reportedMutations.add(mutation);
		}
		if (touchingTestCases.size() > 0) {
			touchedMutations.add(mutation);
		}
		touchingTestCases.clear();
		actualMutation = null;
		actualTestCase = null;
		firstTouch = true;
	}

	public void persist(){
		logger.info("Start storing " + results.size()  + " mutaion test results in db" );
		QueryManager.updateMutations(results);
	}

	public static void touch(long mutationID) {
		if (firstTouch) {
			logger.info("Touch called by mutated code in test "
					+ actualTestCase);
			firstTouch = false;
		}
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

	/**
	 * Returns a summary for all collected test outcomes and writes these to a
	 * file if a property for the file name was set.
	 *
	 * @return The String containing the summary.
	 */
	public String summary(boolean finishedNormal) {
		RunResult runResult = new RunResult(reportedMutations,
				touchedMutations, MutationForRun.getAppliedMutations(),
				unMutatedMutations, finishedNormal);

		String resultFile = System
				.getProperty(MutationProperties.RESULT_FILE_KEY);
		if (resultFile != null) {
			XmlIo.toXML(runResult, new File(resultFile));
		} else {
			logger.warn("Not writing property file. Didn't found Property "
					+ MutationProperties.RESULT_FILE_KEY);
		}

		return runResult.toString();
	}

	public void addUnmutated(Mutation m) {
		assert m.getMutationType().equals(MutationType.NO_MUTATION);
		unMutatedMutations.add(m);
	}
}
