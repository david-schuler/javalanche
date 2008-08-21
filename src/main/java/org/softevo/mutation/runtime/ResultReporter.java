package org.softevo.mutation.runtime;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.util.Util;

import sun.security.jca.GetInstance;

import de.unisb.cs.st.invariants.runtime.InvariantObserver;

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

	private static Set<Mutation> reportedMutations = new HashSet<Mutation>();

	private static Set<Mutation> touchedMutations = new HashSet<Mutation>();

	private static Set<Mutation> unMutatedMutations = new HashSet<Mutation>();

	private static String actualTestCase;

	private static Map<Long, ResultReporter> instances = new HashMap<Long, ResultReporter>();

	private static long actualExpectedID;

	private Set<String> touchingTestCases = new HashSet<String>();

	private MutationTestResult singleTestResult;

	private Mutation mutation;

	private boolean touched;

	private ResultReporter(Mutation mutation) {
		this.mutation = mutation;

	}

	public static ResultReporter createInstance(Mutation mutation) {
		ResultReporter r;
		if (instances.containsKey(mutation.getId())) {
			throw new RuntimeException(
					"Already created ResultReporter for mutation" + mutation);
		} else {
			r = new ResultReporter(mutation);
			instances.put(mutation.getId(), r);
			setActualMutation(mutation);
		}
		return r;
	}

	public static ResultReporter getOrCreateInstance(Mutation mutation){
		if (instances.containsKey(mutation.getId())) {
			return instances.get(mutation.getId());
		} else {
			return createInstance(mutation);
		}
	}
	public synchronized void report(TestResult mutationTestResult,
			Mutation mutation, MutationTestListener mutationTestListener) {
		if (mutationTestResult == null || mutation == null || mutation == null) {
			throw new IllegalArgumentException("Argument was null: "
					+ mutationTestResult == null ? "mutationTestResult" : ""
					+ mutation == null ? ", mutation" : ""

			+ mutationTestListener == null ? ", mutationTestListener" : "");
		}
		MutationTestResult mutationSingleTestResult = new MutationTestResult(
				mutationTestResult, mutationTestListener, touchingTestCases);
		if (!reportedMutations.contains(mutation)) {
			reportedMutations.add(mutation);
		} else {
			String message = "Mutation " + mutation + " already reported ";
			logger.info(message);
			throw new RuntimeException(message);
		}
		singleTestResult = mutationSingleTestResult;
		if (touched) {
			touchedMutations.add(mutation);
		}
		InvariantObserver instance = InvariantObserver.getInstance();
		if (instance != null) {
			int totalViolatedInvariants = instance.getTotalViolatedInvariants();
			int[] violatedInvariants = instance.getViolatedInvariantsArray();
			singleTestResult.setTotalViolations(totalViolatedInvariants);
			singleTestResult.setViolatedInvariants(violatedInvariants);
			singleTestResult
					.setDifferentViolatedInvariants(violatedInvariants.length);
			InvariantObserver.reset();
		}
		touchingTestCases.clear();
		actualMutation = null;
		actualTestCase = null;
	}

	public synchronized static void persist() {
		logger.debug("Start storing " + instances.size()
				+ " mutation test results in db");
		Map<Mutation, MutationTestResult> map = new HashMap<Mutation, MutationTestResult>();
		Set<Entry<Long, ResultReporter>> entrySet = instances.entrySet();
		for (Entry<Long, ResultReporter> entry : entrySet) {
			ResultReporter rr = entry.getValue();
			map.put(rr.mutation, rr.singleTestResult);
		}
		QueryManager.updateMutations(map);
		logger.debug("Stored " + instances.size()
				+ " mutation test results in db");
		instances.clear();

	}

	public static synchronized void touch(long mutationID) {
		// logger.info("Touc called " + mutationID + " - expected " +
		// (actualMutation == null ? "null " : actualMutation.getId() + ""));
		long expectedID = actualExpectedID;
		if (mutationID != expectedID) {
			String message = "Expected ID did not match reported ID "
					+ actualExpectedID + "  - " + mutationID;
			logger.warn(message);
			throw new RuntimeException(message);
		} else {
			ResultReporter rr = instances.get(expectedID);
			rr.touchingTestCases.add(actualTestCase);
			if (!rr.touched) {
				logger.info("Touch called by mutated code in test: "
						+ actualTestCase + " for mutation: " + mutationID
						+ "  Thread " + Thread.currentThread() /*
																 * + "Trace " +
																 * Util.getStackTraceString()
																 */);
				rr.touched = true;
			}
		}
	}

	/**
	 * @return the actualTestCase
	 */
	public static synchronized String getActualTestCase() {
		return actualTestCase;
	}

	/**
	 * @param actualTestCase
	 *            the actualTestCase to set
	 */
	public static synchronized void setActualTestCase(String actualTestCase) {
		logger.debug("Test case set: " + actualTestCase);
		ResultReporter.actualTestCase = actualTestCase;
	}

	/**
	 * @return the actualMutation
	 */
	public static synchronized Mutation getActualMutation() {
		return actualMutation;
	}

	/**
	 * @param actualMutation
	 *            the actualMutation to set
	 */
	public static synchronized void setActualMutation(Mutation actualMutation) {
		ResultReporter.actualMutation = actualMutation;
		ResultReporter.actualExpectedID = actualMutation.getId();
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

	public synchronized boolean isReported(Mutation m) {
		return reportedMutations.contains(m);
	}

}