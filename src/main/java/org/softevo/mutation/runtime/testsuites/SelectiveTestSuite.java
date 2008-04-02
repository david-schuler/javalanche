package org.softevo.mutation.runtime.testsuites;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
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
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.javaagent.MutationPreMain;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.runtime.MutationSwitcher;
import org.softevo.mutation.runtime.MutationTestListener;
import org.softevo.mutation.runtime.ResultReporter;

/**
 * Subclass of JUnits {@link TestSuite} class. It is used to execute the tests
 * for the mutated program. It repeatedly executes the test-cases for every
 * mutation, but only executes the tests that cover the mutation.
 *
 * @author David Schuler
 *
 */
public class SelectiveTestSuite extends TestSuite {

	private static final long serialVersionUID = 1L;

	/**
	 * Enables debugging ~ only a limited number of mutations are executed.
	 */
	private static final boolean DEBUG = false;

	/**
	 * Number of mutations that are executed during debugging.
	 */
	private static final int DEBUG_MUTATION_TO_EXECUTE = 20;

	/**
	 * Timeout for one single test.
	 */
	private static final long DEFAULT_TIMEOUT_IN_SECONDS = 10;

	/**
	 * Execute the same tests with mutation disabled right after the mutation
	 * was tested.
	 */
	private static final boolean CHECK_UNMUTATED_REPEAT = true;

	/**
	 * Log4J logger.
	 */
	static Logger logger = Logger.getLogger(SelectiveTestSuite.class);

	/**
	 * Mutation Switcher to enable and disable mutations.
	 */
	private MutationSwitcher mutationSwitcher;

	/**
	 * To report the results of the mutation testing.
	 */
	private ResultReporter resultReporter = new ResultReporter();

	/**
	 * Shutdownhook to collect test results when System.exit() is called.
	 */
	private Thread shutDownHook;

	/**
	 * Currently used MutationTestListner.
	 */
	private MutationTestListener actualListener;

	/**
	 * Currently active Mutation.
	 */
	private Mutation actualMutation;

	/**
	 * Currently used TestResult.
	 */
	private TestResult actualMutationTestResult;

	/**
	 * Currently active test.
	 */
	private Test actualTest;

	static {
		staticLogMessage();
	}

	/**
	 * Prints out a static log message to check if the SelectiveTestSuite is
	 * integrated in the Process.
	 */
	private static void staticLogMessage() {
		logger.info("Class SelectiveTestSuite is initialized");
		if (DEBUG) {
			logger.warn("SelecitveTestSuite is in DEBUG MODE");
		}
		logger.info(System.getProperty("java.security.policy"));
	}

	private void addShutdownHook() {
		shutDownHook = new Thread() {

			private static final boolean SLEEP = false;

			protected static final int SECOND = 1000;

			public void run() {
				logger.info("Shutdown hook activated");
				logger.info("ActualListener: " + actualListener
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
					resultReporter.report(actualMutationTestResult,
							actualMutation, actualListener);
				} else {
					logger
							.warn("Maybe error that caused the shutdown could not report. Caused by mutation: "
									+ actualMutation);
				}
				logger.info("" + resultReporter.summary(false));
				MutationForRun.getInstance().reportAppliedMutations();
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
		Thread currentThread = Thread.currentThread();
		StackTraceElement[] sts = currentThread.getStackTrace();
		String stackTraceString = Arrays.toString(sts);
		logger.info("SelectiveTestSuite.run entered. Stacktrace:\n"
				+ stackTraceString);
		logger.log(Level.DEBUG, "All Tests collected");
		mutationSwitcher = new MutationSwitcher();
		Map<String, TestCase> allTests = getAllTests(this);
		int debugCount = DEBUG_MUTATION_TO_EXECUTE;
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
			if (!MutationProperties.COVERAGE_INFFORMATION) {
				testsForThisRun = allTests.keySet();
			}
			if (testsForThisRun == null) {
				logger.warn("No tests for " + actualMutation);
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
			logResults();
			if (CHECK_UNMUTATED_REPEAT) {
				testUnmutated(allTests, testsForThisRun);
			}
		}
		Runtime.getRuntime().removeShutdownHook(shutDownHook);
		logger.log(Level.INFO, "Test Runs finished");
		logger.info("" + resultReporter.summary(true));
		MutationForRun.getInstance().reportAppliedMutations();
	}

	/**
	 * Executes the same tests as for the previously enabled mutation and
	 * reports these results.
	 *
	 * @param allTests
	 *            All tests in the TestSuite
	 * @param testsForThisRun
	 *            Tests that should be executed
	 */
	private void testUnmutated(Map<String, TestCase> allTests,
			Set<String> testsForThisRun) {
		actualMutation = QueryManager.generateUnmutated(actualMutation);
		if (actualMutation.getMutationResult() == null) {
			logger.info("Starting unmutated tests");
			ResultReporter.setActualMutation(actualMutation);
			resultReporter.addUnmutated(actualMutation);
			logger.info("Unmutated mutation:" + actualMutation);
			TestResult unmutatedTestResult = new TestResult();
			actualMutationTestResult = unmutatedTestResult;
			MutationTestListener unmutatedListener = new MutationTestListener();
			actualListener = unmutatedListener;
			unmutatedTestResult.addListener(unmutatedListener);
			runTests(allTests, unmutatedTestResult, testsForThisRun);
			resultReporter.report(unmutatedTestResult, actualMutation,
					unmutatedListener);
			logResults();
		}
	}

	private void logResults() {
		logger
				.info(String.format(
						"Check Result runs: %d failures:%d errors:%d ",
						actualMutationTestResult.runCount(),
						actualMutationTestResult.failureCount(),
						actualMutationTestResult.errorCount()));
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
	 *            Tests that should be executed in this run.
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

	/**
	 * Runs given test in a new thread with specified timeout
	 * (DEFAULT_TIMEOUT_IN_SECONDS) and stores the results in given testResult.
	 *
	 * @param test
	 *            TestCase that is run.
	 * @param testResult
	 *            TestResult that is used to store the results.
	 *
	 */
	private void runWithTimeout(final TestCase test, final TestResult testResult) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Callable<Object> callable = getCallable(test, testResult);
		Future<Object> result = service.submit(callable);
		logger.info("start timed test");
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
		logger.info("end timed test");

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
	private Callable<Object> getCallable(final TestCase test,
			final TestResult testResult) {
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
		return callable;
	}

	/**
	 * Returns a Map with all test contained in this TestSuite. The TestSuite is
	 * recursively traversed to search for TestCases.
	 *
	 * @param testSuite
	 *            TestSuite for which the tests are collected.
	 * @return Return a Map that with all test contained in this TestSuite.
	 */
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

	/**
	 * Returns the full (JUnit) name for the given TestCase.
	 *
	 * @param testCase
	 *            TestCase for which the name is computed.
	 * @return The string representation of this TestCase.
	 */
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

	/**
	 * Transforms a {@link TestSuite} to a {@link SelectiveTestSuite}. This
	 * method is called by instrumented code to insert this class instead of the
	 * TestSuite.
	 *
	 * @param testSuite
	 *            The original TestSuite.
	 * @return The {@link SelectiveTestSuite} that contains the given TestSuite.
	 */
	public static SelectiveTestSuite toSelectiveTestSuite(TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		SelectiveTestSuite returnTestSuite = new SelectiveTestSuite(testSuite
				.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
