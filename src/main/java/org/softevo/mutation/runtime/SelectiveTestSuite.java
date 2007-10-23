package org.softevo.mutation.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.softevo.mutation.javaagent.MutationPreMain;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;

/**
 * Subclass of Junits {@link TestSuite} class. It is used to execute the tests
 * for the mutated program. It repeatedly executes the test-cases for every
 * mutation, but only executes the tests that cover the mutation.
 *
 * @author David Schuler
 *
 */
public class SelectiveTestSuite extends TestSuite {


	/**
	 * $Date: 2007-10-19 11:37:30 +0200 (Fri, 19 Oct 2007) $
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;

	/**
	 * Timeout for one single test
	 */
	private static final long DEFAULT_TIMEOUT_IN_SECONDS = 5;

	static Logger logger = Logger.getLogger(SelectiveTestSuite.class);

	private MutationSwitcher mutationSwitcher;

	private ResultReporter resultReporter = new ResultReporter();

	private MutationTestListener actualListener;

	private Mutation actualMutation;

	private TestResult actualMutationTestResult;

	private Thread shutDownHook;

	private Test actualTest;

	static {
		staticLogMessage();
		// setSecurityManager();
	}

	private static void staticLogMessage() {
		System.out.println("Selective Test Suite");
		if (DEBUG) {
			logger.info("TESTMODE");
		}
		logger.info(System.getProperty("java.security.policy"));
		logger.info("$Date: 2007-10-19 11:37:30 +0200 (Fri, 19 Oct 2007) $");
		logger
				.info("$LastChangedDate: 2007-10-19 11:37:30 +0200 (Fri, 19 Oct 2007) $");
	}

	private void addShutdownHook() {
		shutDownHook = new Thread() {

			private static final boolean SLEEP = false;

			protected static final int SECOND = 1000;

			public void run() {
				logger.info("Shutdown hook activated");
				logger.info("actualListener: " + actualListener
						+ "\nresultReporter: " + resultReporter);
				if (SLEEP) {
					try {
						logger.info("Sleeping for 10 seconds");
						sleep(10 * SECOND);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					logger.info("Woke up");
				}
				if (actualListener != null) {
					actualListener.addError(actualTest, new RuntimeException(
							"JVM shut down because of mutation"));
					// resultReporter.report(actualMutationTestResult,
					// actualMutation, actualListener);
				}

				logger.info("" + resultReporter.summary());
			}
		};
		Runtime.getRuntime().addShutdownHook(shutDownHook);
	}

	public SelectiveTestSuite() {
		super();
		addShutdownHook();
	}

	public SelectiveTestSuite(String name) {
		super(name);
		addShutdownHook();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestSuite#run(junit.framework.TestResult)
	 */
	@Override
	public void run(TestResult result) {
		if (System.getProperty(MutationProperties.SCAN_FOR_MUTATIONS) != null
				|| MutationPreMain.scanningEnabled) {
			logger.info("Running scanner");
			super.run(result);
			return;
		}
		logger.info("Not Running scanner");
		logger.log(Level.INFO, "All Tests collected");
		mutationSwitcher = new MutationSwitcher();
		Map<String, TestCase> allTests = getAllTests(this);
		int debugCount = 20;
		while (mutationSwitcher.hasNext()) {
			if (DEBUG) {
				if (debugCount-- < 0) {
					break;
				}
			}
			actualMutation = mutationSwitcher.next();
			try {
				@SuppressWarnings("unused")
				Class c = Class.forName(actualMutation.getClassName());
			} catch (ClassNotFoundException e) {
				logger.info("Class " + actualMutation.getClassName()
						+ " not on classpath");
				continue;
			}
			if (result.shouldStop())
				break;
			Set<String> testsForThisRun = mutationSwitcher.getTests();
			if (testsForThisRun == null) {
				logger.info("No tests for " + actualMutation);
				continue;
			}
			actualMutationTestResult = new TestResult();
			mutationSwitcher.switchOn();
			actualListener = new MutationTestListener();
			actualMutationTestResult.addListener(actualListener);
			runTests(allTests, actualMutationTestResult, testsForThisRun);
			mutationSwitcher.switchOff();
			resultReporter.report(actualMutationTestResult, actualMutation,
					actualListener);
			logger.info(String.format("runs: %d failures:%d errors:%d ",
					actualMutationTestResult.runCount(),
					actualMutationTestResult.failureCount(),
					actualMutationTestResult.errorCount()));
		}
		Runtime.getRuntime().removeShutdownHook(shutDownHook);
		logger.log(Level.INFO, "Test Runs finished");
		logger.info("" + resultReporter.summary());
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

	/**
	 * Executes the specified tests. Used to trigger the special tests for this
	 * mutation.
	 *
	 * @param allTests
	 *            A Map of all available to this test suite.
	 * @param testResult
	 *            Test Result that will hold the results of the tests.
	 * @param testsForThisRun
	 *            Testts that should be executed in this run.
	 */
	private void runTests(Map<String, TestCase> allTests,
			TestResult testResult, Set<String> testsForThisRun) {
		for (String testName : testsForThisRun) {
			TestCase test = allTests.get(testName);
			actualTest = test;
			if (test == null) {
				// throw new RuntimeException("Test not found " + testName
				// + "\n All Tests: " + allTests);
				logger.warn("Test not found " + testName);
			} else {
				try {
					ResultReporter.setActualTestCase(test.toString());
					runWithTimeout(test, testResult);
					// runTest(test, testResult);
				} catch (Exception e) {
					logger.warn(String.format(
							"Exception thrown by test %s Exception: %s", test
									.toString(), e.toString()));
					testResult.addError(test, e);
				}
			}
		}
	}

	private void runWithTimeout(final TestCase test, final TestResult testResult) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Callable<Object> callable = new Callable<Object>() {
			public Object call() throws Exception {
				try {
					runTest(test, testResult);
				} catch (Exception e) {
					logger.info("Caught exception" + e);
					e.printStackTrace();
					testResult.addError(test, e);
				}
				return null;
			}
		};
		Future<Object> result = service.submit(callable);
		service.shutdown();
		try {
			boolean terminated = service.awaitTermination(
					DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			if (!terminated) {
				service.shutdownNow();
			}
			result.get(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.MILLISECONDS);
			result.cancel(true);
		} catch (TimeoutException e) {
			testResult.addError(test, new Exception(String.format(
					"test timed out after %d seconds",
					DEFAULT_TIMEOUT_IN_SECONDS)));
		} catch (Exception e) {
			e.printStackTrace();
			testResult.addError(test, e);
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

	/**
	 * @return the actualMutation that is currently applied.
	 */
	public Mutation getActualMutation() {
		return actualMutation;
	}

}
