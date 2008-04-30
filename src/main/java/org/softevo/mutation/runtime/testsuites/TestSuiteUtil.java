package org.softevo.mutation.runtime.testsuites;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class TestSuiteUtil {

	private static Logger logger = Logger.getLogger(TestSuiteUtil.class);

	/**
	 * Prevent initialization
	 */
	private TestSuiteUtil() {
	}

	public static Map<String, Test> getAllTests(TestSuite testSuite) {
		Map<String, Test> resultMap = new HashMap<String, Test>();
		return collectTests(testSuite, resultMap);
	}

	private static Map<String, Test> collectTests(TestSuite testSuite,
			Map<String, Test> resultMap) {
		for (Enumeration<Test> e = testSuite.tests(); e.hasMoreElements();) {
			Test test = e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				collectTests(suite, resultMap);
			} else if (test instanceof TestCase) {
				TestCase testCase = (TestCase) test;
				String fullTestName = ScanAndCoverageTestSuite
						.getFullTestCaseName(testCase);
				String nameForMap = checkName(fullTestName, resultMap);
				resultMap.put(nameForMap, testCase);
			} else if (test instanceof Test) {
				String testName = getNameForTest(test);
				String keyForMap = checkName(testName, resultMap);
				resultMap.put(keyForMap, test);
			} else {
				throw new RuntimeException("Not handled type: "
						+ test.getClass());
			}
		}
		return resultMap;
	}

	private static String getNameForTest(Test test) {
		String testName = test.getClass().getCanonicalName();
		if (testName == null) {
			testName = "UnknownTest";
		}
		return testName;
	}

	private static String checkName(String testName, Map<String, Test> resultMap) {
		String nameToReturn = testName;
		if (resultMap.containsKey(nameToReturn)) {
			logger.debug("Key already contained: " + testName);
			int i = 1;
			while (resultMap.containsKey(nameToReturn)) {
				i++;
				nameToReturn = testName + "-instance-" + i;
			}
		}
		return nameToReturn;
	}

	/**
	 * Helper method that returns a Callable that executes the given test and
	 * testResult.
	 *
	 * @param test
	 *            TestCase that is executed.
	 * @param testResult
	 *            TestResult where the the testResults are collected.
	 * @return The Callable that executes the test.
	 */
	public static Callable<Object> getCallable(final Test test,
			final TestResult testResult) {
		Callable<Object> callable = new Callable<Object>() {
			public Object call() throws Exception {
				if (Thread.interrupted())
					throw new InterruptedException();
				try {
					test.run(testResult);
				} catch (Exception e) {
					logger.info("Caught exception" + e);
					e.printStackTrace();
					testResult.addError(test, e);
				}
				return null;
			}
		};
		return callable;
	}

}
