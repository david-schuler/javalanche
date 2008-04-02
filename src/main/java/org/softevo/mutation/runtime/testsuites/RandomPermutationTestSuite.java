package org.softevo.mutation.runtime.testsuites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Executes the tests in a random order multiple times. In order to see if they
 * are independent of each other (e.g. produce the same results when executed
 * multiple times {@see limit).
 *
 * For a run three files are produced:
 * <ul>
 * <li> A file that contains all failing tests </li>
 * <li> A file that contains all passing tests </li>
 * <li> A file that contains all tests that had a different result for multiple
 * runs</li>
 * <li> A file that contains all tests that had a the same result for multiple
 * runs</li>
 * </ul>
 *
 * @author David Schuler
 *
 */
public class RandomPermutationTestSuite extends TestSuite {

	private static final String TESTS_FAILING_FILENAME = "tests-failing.xml";

	private static final String TESTS_PASSING_FILENAME = "tests-passing.xml";

	private static final String TESTS_COMMON_OUTCOME_FILENAME = "tests-common-outcome.xml";

	private static final String TESTS_DIFFERENT_OUTCOME_FILENAME = "tests-different-outcome.xml";

	private static Logger logger = Logger
			.getLogger(RandomPermutationTestSuite.class);

	private static final int DEFAULT_LIMIT = 6;


	public RandomPermutationTestSuite(String name) {
		super(name);
	}

	public static Map<String, TestCase> getAllTests(TestSuite testSuite) {
		Map<String, TestCase> resultMap = new HashMap<String, TestCase>();

		for (Enumeration e = testSuite.tests(); e.hasMoreElements();) {
			Object test = e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				resultMap.putAll(getAllTests(suite));
			} else if (test instanceof TestCase) {
				TestCase testCase = (TestCase) test;
				String fullTestName = getFullTestCaseName(testCase);
				resultMap.put(fullTestName, testCase);
			} else if (test instanceof Test) {
				logger.info("Test not added. Class: " + test.getClass());
			} else {
				throw new RuntimeException("Not handled type: "
						+ test.getClass());
			}
		}
		return resultMap;
	}

	private static String getFullTestCaseName(TestCase testCase) {
		String fullTestName = testCase.getClass().getName() + "."
				+ testCase.getName();
		return fullTestName;
	}

	@Override
	public void run(TestResult result) {
		Map<String, TestCase> allTests = getAllTests(this);
		Map<String, List<TestResult>> testResults = new HashMap<String, List<TestResult>>();
		int totalTestsRun = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < DEFAULT_LIMIT; i++) {
			logger.info("Round" + i);
			List<Map.Entry<String, TestCase>> shuffeledList = getShuffledTestList(allTests);
			int testsRun = runListOfTests(shuffeledList, testResults);
			totalTestsRun += testsRun;
		}
		analyzeTestResults(testResults, allTests);
		long time = System.currentTimeMillis() - start;
		logger.info("Run " + totalTestsRun + " tests in " + time + " ms");
	}

	private void analyzeTestResults(Map<String, List<TestResult>> testResults,
			Map<String, TestCase> allTests) {
		Set<String> testsDifferentOutcome = new HashSet<String>();
		Set<String> testsFailing = new HashSet<String>();
		for (Entry<String, List<TestResult>> entry : testResults.entrySet()) {
			String testName = entry.getKey();
			boolean first = true;
			int failures = 0;
			int errors = 0;
			int runs = 0;
			for (TestResult testResult : entry.getValue()) {
				if (first) {
					first = false;
					failures = testResult.failureCount();
					errors = testResult.errorCount();
					runs = testResult.runCount();
				} else {
					if (failures != testResult.failureCount()
							|| errors != testResult.errorCount()
							|| runs != testResult.runCount()) {
						logger.warn("Different TestResults for test"
								+ entry.getKey());
						testsDifferentOutcome.add(testName);
					}
				}
				if (testResult.errorCount() >= 1
						|| testResult.failureCount() >= 1) {
					testsFailing.add(testName);
				}
			}
		}
		Set<String> testsCommonOutcome = new HashSet<String>();
		Set<String> testsPassing = new HashSet<String>();

		for (String testName : allTests.keySet()) {
			if (!testsDifferentOutcome.contains(testName)) {
				testsCommonOutcome.add(testName);
			}
			if (!testsFailing.contains(testName)) {
				testsPassing.add(testName);
			}
		}
		XmlIo.toXML(testsDifferentOutcome, TESTS_DIFFERENT_OUTCOME_FILENAME);
		XmlIo.toXML(testsCommonOutcome, TESTS_COMMON_OUTCOME_FILENAME);
		XmlIo.toXML(testsFailing, TESTS_FAILING_FILENAME);
		XmlIo.toXML(testsPassing, TESTS_PASSING_FILENAME);

	}

	private List<Map.Entry<String, TestCase>> getShuffledTestList(
			Map<String, TestCase> allTests) {
		Set<Map.Entry<String, TestCase>> entrySet = allTests.entrySet();
		List<Map.Entry<String, TestCase>> shuffeledList = new ArrayList<Entry<String, TestCase>>(
				entrySet);
		Collections.shuffle(shuffeledList);
		return shuffeledList;
	}

	private int runListOfTests(List<Map.Entry<String, TestCase>> shuffeledList,
			Map<String, List<TestResult>> testResults) {
		int testsRun = 0;
		for (Map.Entry<String, TestCase> entry : shuffeledList) {
			TestResult resultForTest = new TestResult();
			String testName = entry.getKey();
			logger.info("Running Test " + entry.getValue());
			try {
				runTest(entry.getValue(), resultForTest);
			} catch (Error e) {
				logger.warn("Exception During test " + e.getMessage());
				e.printStackTrace();
				if (resultForTest.failureCount() + resultForTest.errorCount() < 1) {
					resultForTest.addError(entry.getValue(), e);
				}
			}
			logger
					.debug("Run count for last test: "
							+ resultForTest.runCount());
			testsRun++;
			List<TestResult> resultList;
			if (testResults.get(testName) != null) {
				resultList = testResults.get(testName);
			} else {
				resultList = new ArrayList<TestResult>();
				testResults.put(testName, resultList);
			}
			resultList.add(resultForTest);
		}
		return testsRun;
	}

	/**
	 * Transforms a {@link TestSuite} to a {@link RandomPermutationTestSuite}.
	 * This method is called by instrumented code to insert this class instead
	 * of the TestSuite.
	 *
	 * @param testSuite
	 *            The original TestSuite.
	 * @return The {@link RandomPermutationTestSuite} that contains the given
	 *         TestSuite.
	 */
	public static RandomPermutationTestSuite toRandomPermutationTestSuite(
			TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		RandomPermutationTestSuite returnTestSuite = new RandomPermutationTestSuite(
				testSuite.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
