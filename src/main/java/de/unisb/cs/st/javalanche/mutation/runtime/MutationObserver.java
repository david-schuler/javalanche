package de.unisb.cs.st.javalanche.mutation.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * Class used by the mutations at runtime to report the testcases that cover a
 * mutation.
 *
 * @author David Schuler
 *
 */
public class MutationObserver implements MutationTestListener {

	private static Logger logger = Logger.getLogger(MutationObserver.class);

	/**
	 * All mutations that were activated.
	 */
	private static List<Mutation> reportedMutations = new ArrayList<Mutation>();

	/**
	 * All tests that wher touched by tests.
	 */
	private static Collection<String> touchingTestCases = new HashSet<String>();

	/**
	 * Id of the currently active mutation.
	 */
	private static long expectedID;

	/**
	 * Name of the currently active test.
	 */
	private static String actualTestCase;

	/**
	 * Set of all mutations that where touched during this run.
	 */
	private static Set<Mutation> touchedMutations = new HashSet<Mutation>();

	/**
	 * Currently active mutation.
	 */
	private static Mutation actualMutation;

	/**
	 * Indicates wheter the currently active Mutation was touched or not.
	 */
	private static boolean touched = false;

	/**
	 * This method is called by statements that are added to the mutated code.
	 * It is called every time the mutated statements get executed.
	 *
	 * @param mutationID
	 *            the id of the mutation that is executed
	 */
	public static synchronized void touch(long mutationID) {
		// (actualMutation == null ? "null " : actualMutation.getId() + ""));
		if (mutationID != expectedID) {
			String message = "Expected ID did not match reported ID "
					+ expectedID + "  - " + mutationID;
			logger.warn(message);
			throw new RuntimeException(message);
		} else {
			touchingTestCases.add(actualTestCase);
			logger.info(MutationObserver.class.getClassLoader());
			if (!touched) {
				touchedMutations.add(actualMutation);
				logger.info("Touch called by mutated code in test: "
						+ actualTestCase + " for mutation: " + mutationID
						+ " Thread " + Thread.currentThread()
				// + "Trace " + Util.getStackTraceString()
						);
				touched = true;
			}
		}
	}

	/**
	 * Returns a summary for all collected test outcomes and writes these to a
	 * file if a property for the file name was set.
	 *
	 * @return The String containing the summary.
	 */
	public static String summary(boolean finishedNormal) {
		RunResult runResult = new RunResult(reportedMutations,
				touchedMutations, MutationForRun.getAppliedMutations(),
				finishedNormal);
		String resultFile = MutationProperties.RESULT_FILE;
		if (resultFile != null) {
			XmlIo.toXML(runResult, new File(resultFile));
		} else {
			logger.warn("Not writing property file. Didn't found Property "
					+ MutationProperties.RESULT_FILE_KEY);
		}
		return runResult.toString();
	}

	/**
	 * Return all test cases that touched the currently active mutation up to
	 * this point.
	 *
	 * @return the test cases that touched the currently active mutation
	 */
	public static Collection<String> getTouchingTestCases() {
		return touchingTestCases;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#end()
	 */
	public void end() {
	}

	/**
	 * Called when a mutation starts. Resets the proper variables.
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#mutationStart(de.unisb.cs.st.javalanche.mutation.results.Mutation)
	 */
	public void mutationStart(Mutation mutation) {
		logger.info("Mutation start");
		reportedMutations.add(mutation);
		actualMutation = mutation;
		touchingTestCases.clear();
		expectedID = mutation.getId();
		touched = false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#mutationEnd(de.unisb.cs.st.javalanche.mutation.results.Mutation)
	 */
	public void mutationEnd(Mutation mutation) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#start()
	 */
	public void start() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#testEnd(java.lang.String)
	 */
	public void testEnd(String testName) {
		actualTestCase = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener#testStart(java.lang.String)
	 */
	public void testStart(String testName) {
		actualTestCase = testName;
	}

}
