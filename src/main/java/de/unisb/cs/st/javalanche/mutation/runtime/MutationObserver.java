package de.unisb.cs.st.javalanche.mutation.runtime;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * TODO MAke this class a singleton.
 *
 * @author David Schuler
 *
 */
public class MutationObserver implements MutationTestListener {

	private static Logger logger = Logger.getLogger(MutationObserver.class);

	private static List<Mutation> reportedMutations;

	private static Collection<String> touchingTestCases;

	private static long expectedID;

	private static String actualTestCase;

	private static Set<Mutation> touchedMutations;
	private static Mutation actualMutation;

	private static boolean touched;

	public static synchronized void touch(long mutationID) {
		// (actualMutation == null ? "null " : actualMutation.getId() + ""));
		if (mutationID != expectedID) {
			String message = "Expected ID did not match reported ID "
					+ expectedID + "  - " + mutationID;
			logger.warn(message);
			throw new RuntimeException(message);
		} else {
			touchingTestCases.add(actualTestCase);
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
	 * @return the touchingTestCases
	 */
	public static Collection<String> getTouchingTestCases() {
		return touchingTestCases;
	}

	public void end() {
	}

	public void mutationStart(Mutation mutation) {
		actualMutation = mutation;
		touchingTestCases.clear();
		expectedID = mutation.getId();
	}

	public void mutationEnd(Mutation mutation) {

	}

	public void start() {

	}

	public void testEnd(String testName) {
		actualTestCase = null;
	}

	public void testStart(String testName) {
		actualTestCase = testName;
	}

}
