package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.javaagent.MutationPreMain;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;

public class SelectiveTestSuite extends TestSuite {

	/**
	 * $Date$ $LastChangedDate:
	 * 2007-10-01 11:46:48 +0200 (Mon, 01 Oct 2007) $
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean TESTMODE = false;

	static Logger logger = Logger.getLogger(SelectiveTestSuite.class);

	private MutationSwitcher mutationSwitcher;

	private ResultReporter resultReporter = new ResultReporter();

	static {
		System.out.println("Selective Test Suite");
		if (TESTMODE) {
			logger.info("TESTMODE");
		}
		logger.info("$Date$");
		logger
				.info("$LastChangedDate$ a");
	}

	public SelectiveTestSuite() {
		super();
	}

	public SelectiveTestSuite(String name) {
		super(name);
	}

	@Override
	public void run(TestResult result) {
		if (System.getProperty(MutationProperties.SCAN_FOR_MUTATIONS) != null
				|| MutationPreMain.scanningEnabled) {
			logger.info("Running scanner");
			super.run(result);
			return;
		}
		logger.info("Not Running scanner");
		logger.info("DDD");
		Map<String, TestCase> allTests = getAllTests(this);
		logger.log(Level.INFO, "All Tests collected");
		mutationSwitcher = new MutationSwitcher(
				getStringList(allTests.values()));
		int debugCount = 20;
		while (mutationSwitcher.hasNext()) {
			if (TESTMODE) {
				if (debugCount-- < 0) {
					break;
				}
			}
			Mutation mutation = mutationSwitcher.next();
			try {
				@SuppressWarnings("unused")
				Class c = Class.forName(mutation.getClassName());
			} catch (ClassNotFoundException e) {
				logger.info("Class " + mutation.getClassName()
						+ " not on classpath");
				continue;
			}
			if (result.shouldStop())
				break;
			Set<String> testsForThisRun = mutationSwitcher.getTests();
			if (testsForThisRun == null) {
				logger.info("No tests for " + mutation);
				continue;
			}
			TestResult mutationTestResult = new TestResult();
			mutationSwitcher.switchOn();
			MutationTestListener listener = new MutationTestListener();
			mutationTestResult.addListener(listener);
			runTests(allTests, mutationTestResult, testsForThisRun);
			mutationSwitcher.switchOff();
			resultReporter.report(mutationTestResult, mutation, listener);
			logger.info(String.format("runs: %d failures:%d errors:%d ",
					mutationTestResult.runCount(), mutationTestResult
							.failureCount(), mutationTestResult.errorCount()));
		}
		logger.log(Level.INFO, "Test Runs finished");
	}

	/**
	 * Returns a list of TestCase names for given Collection of TestCases.
	 *
	 * @param testCases
	 * @return
	 */
	private Collection<String> getStringList(Collection<TestCase> testCases) {
		List<String> result = new ArrayList<String>();
		for (TestCase tc : testCases) {
			result.add(getFullTestCaseName(tc));
		}
		return result;
	}

	private void runTests(Map<String, TestCase> allTests,
			TestResult testResult, Set<String> testsForThisRun) {
		for (String testName : testsForThisRun) {
			TestCase test = allTests.get(testName);
			if (test == null) {
				// throw new RuntimeException("Test not found " + testName
				// + "\n All Tests: " + allTests);
				logger.warn("Test not found " + testName);
			} else {
				try {
					runTest(test, testResult);
				} catch (Exception e) {
					logger.warn(String.format(
							"Exception thrown by test %s Exception: %s", test
									.toString(), e.toString()));
					testResult.addError(test, e);
				}
			}
		}
	}

	private static Map<String, TestCase> getAllTests(TestSuite s) {
		Map<String, TestCase> resultMap = new HashMap<String, TestCase>();
		for (Enumeration e = s.tests(); e.hasMoreElements();) {
			Object test = e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				resultMap.putAll(getAllTests(suite));
			} else if (test instanceof TestCase) {
				TestCase testCase = (TestCase) test;
				String fullTestName = getFullTestCaseName(testCase);
				resultMap.put(fullTestName, testCase);
				if (fullTestName.contains("AbstractTraceTest")) {
					logger.info("Found abstract Test" + testCase);
				}
			} else if (test instanceof Test) {
				// do nothing
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
}
