package org.softevo.mutation.runtime;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.QueryManager;

/**
 * @author David Schuler
 *
 */
public class ResultReporter {

	private static Logger logger = Logger.getLogger(ResultReporter.class);

	private static Mutation actualMutation;

	private static Set<String> touchingTestCases = new HashSet<String>();

	private static String actualTestCase;

	private static boolean firstTouch = true;

	private int reports = 0;

	private int touched;

	public synchronized void report(TestResult mutationTestResult,
			Mutation mutation, MutationTestListener mutationTestListener) {
		if (mutationTestResult == null || mutation == null || mutation == null) {
			throw new IllegalArgumentException("Argument was null: "
					+ mutationTestResult == null ? "mutationTestResult" : ""
					+ mutation == null ? ", mutation" : ""
					+ mutationTestListener == null ? ", mutationTestListener"
					: "");
		}
		SingleTestResult mutated = new SingleTestResult(mutationTestResult,
				mutationTestListener, touchingTestCases);
		QueryManager.updateMutation(mutation, mutated);
		if (touchingTestCases.size() > 0) {
			touched++;
		}
		touchingTestCases.clear();
		actualMutation = null;
		actualTestCase = null;
		firstTouch = true;
		reports++;
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

	public String summary() {
		RunResult runResult = new RunResult(reports, touched);
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
}