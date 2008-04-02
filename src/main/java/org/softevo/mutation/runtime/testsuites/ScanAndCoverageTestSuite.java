package org.softevo.mutation.runtime.testsuites;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.mutationCoverage.CoverageData;

public class ScanAndCoverageTestSuite extends TestSuite {

	private static Logger logger = Logger
			.getLogger(ScanAndCoverageTestSuite.class);

	public ScanAndCoverageTestSuite(String name) {
		super(name);
	}

	public static Map<String, TestCase> getAllTests(TestSuite testSuite) {
		Map<String, TestCase> resultMap = new HashMap<String, TestCase>();

		for (Enumeration<Test> e = testSuite.tests(); e.hasMoreElements();) {
			Test test = e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				Map<String, TestCase> suiteTests = getAllTests(suite);
				Set<Entry<String, TestCase>> set = suiteTests.entrySet();
				for (Entry<String, TestCase> entry : set) {
					if (resultMap.containsKey(entry.getKey())) {
						logger.error("Key already contained " + entry.getKey());
						throw new RuntimeException("Test already contained");
					} else {
						resultMap.put(entry.getKey(), entry.getValue());
					}
				}

			} else if (test instanceof TestCase) {
				TestCase testCase = (TestCase) test;
				String fullTestName = getFullTestCaseName(testCase);
				if (resultMap.containsKey(fullTestName)) {
					logger.error("Key already contained" + fullTestName);
					String key = fullTestName;
					int i = 1;
					while (resultMap.containsKey(key)) {
						i++;
						key = fullTestName + "-" + i;
					}
					resultMap.put(key, testCase);
//					throw new RuntimeException("Kee already contained");

				} else {
					resultMap.put(fullTestName, testCase);
				}
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
		TestResult testResult = new TestResult();
		super.run(testResult);
		System.out.println("First Test Result " + testResult.runCount());
		Map<String, TestCase> allTests = getAllTests(this);
		if (allTests.size() != testResult.runCount()) {
			throw new RuntimeException("found to less tests");
		}
		int testCount = 0;
		Set<Entry<String, TestCase>> allTestEntrySet = allTests.entrySet();
		for (Map.Entry<String, TestCase> entry : allTestEntrySet) {
			testCount++;
			logger.info("Running Test (" + testCount + "/"
					+ allTestEntrySet.size() + ")" + entry.getValue());
			try {
				setTestName(entry.getKey());
				runTest(entry.getValue(), result);
				unsetTestName(entry.getKey());
			} catch (Error e) {
				logger.warn("Exception During test " + e.getMessage());
				e.printStackTrace();
			}
			logger.info("Test Finished (" + testCount + ")" + entry.getValue());
		}
		CoverageData.endCoverage();
	}

	private void unsetTestName(String testName) {
		CoverageData.unsetTestName(testName);
	}

	private void setTestName(String testName) {
		CoverageData.setTestName(testName);

	}

	/**
	 * Transforms a {@link TestSuite} to a {@link ScanAndCoverageTestSuite}.
	 * This method is called by instrumented code to insert this class instead
	 * of the TestSuite.
	 *
	 * @param testSuite
	 *            The original TestSuite.
	 * @return The {@link ScanAndCoverageTestSuite} that contains the given
	 *         TestSuite.
	 */
	public static ScanAndCoverageTestSuite toScanAndCoverageTestSuite(
			TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		ScanAndCoverageTestSuite returnTestSuite = new ScanAndCoverageTestSuite(
				testSuite.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
