package de.unisb.cs.st.javalanche.mutation.runtime.testsuites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.Logger;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.Junit3MutationTestDriver;

public class ScanAndCoverageTestSuite extends TestSuite {

	private static final boolean IGNORE_EXCEPTIONS = true;
	static Logger logger = Logger.getLogger(ScanAndCoverageTestSuite.class);

	static {
		logger.info(ScanAndCoverageTestSuite.class + "  is loaded ");
	}

	private boolean newMode = true;

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
		logger.info("TestSuite started");
		if (newMode) {
			logger.info("Running in new mode");
			Junit3MutationTestDriver testDriver = new Junit3MutationTestDriver(this);
			testDriver.run();
			return;
		}
		TestResult firstTestResult = new TestResult();

		// super.run(firstTestResult);
		// logger.info("First test result " + firstTestResult.runCount());

		Map<String, Test> allTests = TestSuiteUtil.getAllTests(this);
		Set<Entry<String, Test>> allTestEntrySet = allTests.entrySet();
		logger.info("Running tests to get all classes loaded");
		for (Map.Entry<String, Test> entry : allTestEntrySet) {
			String testName = entry.getKey();
			try {
				runTest(entry.getValue(), firstTestResult);
			} catch (Error e) {
				logger.warn("Exception During test " + testName + " "
						+ e.getMessage());
				e.printStackTrace();
				if (IGNORE_EXCEPTIONS) {
					logger.warn("Ignoring Exception: no rethrow");
				} else {
					throw e;
				}
			}
		}
		logger.info("Run " + firstTestResult.runCount()
				+ "  tests. Start collecting coverage information.");
		logger.info(firstTestResult);
		// if (allTests.size() != firstTestResult.runCount()) {
		// throw new RuntimeException("Found unequal number of tests"
		// + allTests.size() + " " + firstTestResult.runCount());
		// }
		int testCount = 0;

		List<String> testsRun = new ArrayList<String>();
		result.addListener(new TestListener() {

			public void addError(Test test, Throwable t) {
				logger.warn("Error " + test + "  " + t);
			}

			public void addFailure(Test test, AssertionFailedError t) {
				logger.warn("Failure " + test + "  " + t);
			}

			public void endTest(Test test) {
				logger.warn(test + "  end");

			}

			public void startTest(Test test) {
				logger.warn(test + " start");

			}

		});
		for (Map.Entry<String, Test> entry : allTestEntrySet) {
			testCount++;
			String testName = entry.getKey();
			logger.info("Running Test (" + testCount + "/"
					+ allTestEntrySet.size() + ") " + testName);
			try {
				setTestName(testName);

				runTest(entry.getValue(), result);
				if (result.errorCount() + result.failureCount() > 0) {
					String message = "Test failed" + testName;

					logger.warn(message);
					throw new RuntimeException(message);
				}
				CoverageData.optionalSave();
			} catch (Error e) {
				logger.warn("Exception During test " + testName + " "
						+ e.getMessage());
				e.printStackTrace();
				if (IGNORE_EXCEPTIONS) {
					logger.warn("Ignoring Exception: no rethrow");
				} else {
					throw e;
				}
			} finally {
				unsetTestName(testName);
				testsRun.add(testName);
			}
			logger.info("Test Finished (" + testCount + ") " + testName);
		}
		CoverageData.endCoverage();
		// XmlIo.toXML(testsRun, "tests-runByScanAndCoveragetestSuite.xml");

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
		logger.info("Transforming TestSuite to scan for mutations");
		ScanAndCoverageTestSuite returnTestSuite = new ScanAndCoverageTestSuite(
				testSuite.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
