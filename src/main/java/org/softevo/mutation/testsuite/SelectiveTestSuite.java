package org.softevo.mutation.testsuite;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.results.Mutation;

public class SelectiveTestSuite extends TestSuite {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean TESTMODE = false;

	static Logger logger = Logger
			.getLogger(SelectiveTestSuite.class);

	private MutationSwitcher mutationSwitcher = new MutationSwitcher();

	private ResultReporter resultReporter = new ResultReporter();

	static {
		logger.setLevel(Level.INFO);
		System.out.println("Selective Test Suite");
		if (TESTMODE) {
			logger.info("TESTMODE 1");
		}
	}

	public SelectiveTestSuite() {
		super();
	}

	public SelectiveTestSuite(Class theClass, String name) {
		super(theClass, name);
	}

	public SelectiveTestSuite(final Class theClass) {
		super(theClass);
	}

	public SelectiveTestSuite(String name) {
		super(name);
	}

	// @Override
	public void run(TestResult result) {
		logger.info("debug");
		Map<String, TestCase> allTests = getAllTests(this);
		logger.log(Level.INFO, "All Tests colleceted");
		int debugCount = 20;
		while (mutationSwitcher.hasNext()) {
			if (TESTMODE) {
				if (debugCount-- < 0) {
					break;
				}
			}
			Mutation mutation = mutationSwitcher.next();
			try {
				Class c = Class.forName(mutation.getClassName());
			} catch (ClassNotFoundException e) {
				logger.info("Class " + mutation.getClassName()
						+ " not on classpath");
				continue;
			}
			if (result.shouldStop())
				break;
			Set<String> tests = mutationSwitcher.getTests();
			if (tests == null) {
				logger.info("No tests for " + mutation);
				continue;
			}
			TestResult mutationTestResult = new TestResult();
			mutationSwitcher.switchOn();
			runTests(allTests, mutationTestResult, tests);
			mutationSwitcher.switchOff();
			resultReporter.report(mutationTestResult, mutation);
			logger.info(String.format("runs %d failures:%d errors:%d",
					mutationTestResult.runCount(), mutationTestResult
							.failureCount(), mutationTestResult.errorCount()));
		}
		logger.log(Level.INFO, "Test Runs finished");
	}

	private void runTests(Map<String, TestCase> allTests,
			TestResult testResult, Set<String> tests) {
		for (String testName : tests) {
			TestCase test = allTests.get(testName);
			if (test == null) {
				System.out.println(allTests);
				throw new RuntimeException("Test not found " + testName
						+ "\n All Tests: " + allTests);
			}
			runTest(test, testResult);
		}
	}

	private static Map<String, TestCase> getAllTests(TestSuite s) {
		Map<String, TestCase> resultMap = new HashMap<String, TestCase>();
		for (Enumeration e = s.tests(); e.hasMoreElements();) {
			Test test = (Test) e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				resultMap.putAll(getAllTests(suite));
			} else {
				if (test instanceof TestCase) {
					TestCase testCase = (TestCase) test;
					String fullTestName = testCase.getClass().getName() + "."
							+ testCase.getName();
					resultMap.put(fullTestName, testCase);
				}

			}

		}
		return resultMap;
	}

}
