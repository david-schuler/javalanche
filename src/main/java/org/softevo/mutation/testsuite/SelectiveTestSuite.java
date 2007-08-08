package org.softevo.mutation.testsuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.softevo.mutation.io.Io;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class SelectiveTestSuite extends TestSuite {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean TESTMODE = true;

	static Logger logger = Logger
			.getLogger(SelectiveTestSuite.class.toString());

	private MutationSwitcher mutationSwitcher = new MutationSwitcher();

	private ResultReporter resultReporter = new ResultReporter();

	static {
		logger.setLevel(Level.INFO);
		if (TESTMODE) {
			logger.info("TESTMODE");
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
			if (result.shouldStop())
				break;
			Set<String> tests = mutationSwitcher.getTests();
			TestResult mutationTestResult = new TestResult();
			mutationSwitcher.switchOn();
			runTests(allTests, mutationTestResult, tests);
			mutationSwitcher.switchOff();
			resultReporter.report(mutationTestResult,
					mutation);
			logger.info(String.format("runs %d failures:%d errors:%d", result
					.runCount(), result.failureCount(), result.errorCount()));
		}
//		logger.log(Level.INFO, "Writing results to db");
//		resultReporter.toDb();
	}

	private void runTests(Map<String, TestCase> allTests,
			TestResult testResult, Set<String> tests) {
		for (String testName : tests) {
			TestCase test = allTests.get(testName);
			if (test == null) {
				throw new RuntimeException("No test found" + testName);
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
					// logger.log(Level.OFF,"Test collected: " + fullTestName);
				}

			}

		}
		return resultMap;
	}

}
