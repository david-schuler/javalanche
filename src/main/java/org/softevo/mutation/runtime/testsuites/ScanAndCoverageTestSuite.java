package org.softevo.mutation.runtime.testsuites;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import org.softevo.mutation.bytecodeMutations.mutationCoverage.CoverageData;

public class ScanAndCoverageTestSuite extends TestSuite {

	static Logger logger = Logger.getLogger(ScanAndCoverageTestSuite.class);

	public ScanAndCoverageTestSuite(String name) {
		super(name);
	}

	static String getFullTestCaseName(TestCase testCase) {
		String fullTestName = testCase.getClass().getName() + "."
				+ testCase.getName();
		return fullTestName;
	}

	@Override
	public void run(TestResult result) {
		TestResult firstTestResult = new TestResult();
		super.run(firstTestResult);
		logger.info("First test result " + firstTestResult.runCount());
		Map<String, Test> allTests = TestSuiteUtil.getAllTests(this);
		if (allTests.size() != firstTestResult.runCount()) {
			throw new RuntimeException("Found unequal number of tests"
					+ allTests.size() + "  " + firstTestResult.runCount());
		}
		int testCount = 0;
		Set<Entry<String, Test>> allTestEntrySet = allTests.entrySet();
		for (Map.Entry<String, Test> entry : allTestEntrySet) {
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
	 *            the original TestSuite.
	 * @return the {@link ScanAndCoverageTestSuite} that contains the given
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
