package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationSwitcher;
import de.unisb.cs.st.javalanche.mutation.runtime.ResultReporter;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.Junit3MutationTestDriver;

/**
 * Abstract class that drives the mutation test process. Driver for specific
 * test architectures must subclass this class.
 *
 * @see Junit3MutationTestDriver
 *
 * @author David Schuler
 *
 */
public abstract class MutationTestDriver {

	private static final String DRIVER_KEY = "mutation.test.driver";

	private static final String MUTATION_TEST_LISTENER_KEY = "mutation.test.listener";

	private static Logger logger = Logger.getLogger(MutationTestDriver.class);

	/**
	 * Timeout for the test. After this time a test is stopped.
	 */
	private static long timeout = MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS;

	/**
	 * The mutation that is currently active.
	 */
	private Mutation currentMutation;

	/**
	 * The name of the test that is currently active.
	 */
	private String currentTestName;

	/**
	 * Mutation switcher that is used to enable and disable mutations.
	 */
	private MutationSwitcher mutationSwitcher;

	/**
	 * Flag that indicates if the shutdown method was called.
	 */
	private boolean shutdownMethodCalled;

	/**
	 * The sve intervall in which the mutation results are written to the
	 * database.
	 */
	protected static final int saveIntervall = MutationProperties.SAVE_INTERVAL;

	/**
	 * The listeneres that are informed about mutation events.
	 */
	private List<MutationTestListener> listeners = new ArrayList<MutationTestListener>();

	/**
	 * True if all tests should be run once before the actual mutation testing.
	 */
	protected boolean doColdRun;

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		runFromProperty();
	}

	/**
	 * Instanciate a MutationTestDriver from a property (mutation.test.driver).
	 * And uses this driver to un the mutation tests
	 *
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static void runFromProperty() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String driver = Util.getPropertyOrFail(DRIVER_KEY);
		Class<? extends MutationTestDriver> clazz = (Class<? extends MutationTestDriver>) Class
				.forName(driver);
		MutationTestDriver newInstance = clazz.newInstance();
		newInstance.run();
	}

	public void run() {
		if (MutationProperties.RUN_MODE == RunMode.MUTATION_TEST
				|| MutationProperties.RUN_MODE == RunMode.MUTATION_TEST_INVARIANT) {
			runMutations();
		} else if (MutationProperties.RUN_MODE == RunMode.SCAN) {
			scanTests();
		} else {
			runNormalTests();
		}
	}

	/**
	 * Runs the tests whitout applyin any changes. This method is used to check
	 * if the driver works correctly.
	 */
	private void runNormalTests() {
		logger.info("Running tests of project " + MutationProperties.PROJECT_PREFIX);
		List<String> allTests = getAllTests();
		int counter = 0;
		int size = allTests.size();
		timeout = 120;
		boolean allPass = true;
		List<SingleTestResult> failing = new ArrayList<SingleTestResult>();
		for (String testName : allTests) {
			counter++;
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);
			MutationTestRunnable runnable = getTestRunnable(testName);
			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			logger.info(result.getTestMessage());
			if (!result.hasPassed()) {
				allPass = false;
				failing.add(result);
				logger.warn("Test has not passed " + testName);
			}
		}
		if (allPass) {
			logger.info("All " + allTests.size() + " tests passed ");
		} else {
			logger.warn("Not all tests passed");
			for (SingleTestResult str : failing) {
				logger.warn(str.getTestMessage().getTestCaseName() + ": "
						+ str.getTestMessage());
			}

		}
	}

	/**
	 * Method that runs the tests to scan for mutation possibilities.
	 *
	 */
	@SuppressWarnings("unchecked")
	public void scanTests() {
		logger.info("Running tests to scan for mutations");
		List<String> allTests = getAllTests();
		int counter = 0;
		int size = allTests.size();
		timeout = 120;
		if (doColdRun) {
			coldRun(allTests);
		}
		logger.info("Start run of tests and collect coverage data");
		for (String testName : allTests) {
			counter++;
			CoverageData.setTestName(testName);
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);

			MutationTestRunnable runnable = getTestRunnable(testName);
			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			long duration = result.getDuration();
			TestName tm = new TestName(testName,
					MutationProperties.PROJECT_PREFIX, duration);
			QueryManager.save(tm);
			CoverageData.unsetTestName(testName);
		}
		CoverageData.endCoverage();
	}

	/**
	 * Runs the given list of tests whitout any special modifications. This has
	 * the purpose to get all classes loaded that are involved in the testsing.
	 *
	 * @param allTests
	 *            the tests to run
	 */
	private void coldRun(List<String> allTests) {
		int counter = 0;
		int size = allTests.size();
		logger.info("Start cold run  of tests to get all classes loaded");
		for (String testName : allTests) {
			counter++;
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);
			MutationTestRunnable runnable = getTestRunnable(testName);
			runWithTimeout(runnable);
		}
	}

	/**
	 * Method that runs he mutation testsing. All mutations for this run are
	 * caried out and their corresponding tests are run.
	 */
	public void runMutations() {
		logger.info("Running Mutations");
		Thread shutDownThread = new Thread(new MutationDriverShutdownHook(this));
		addMutationTestListener(new MutationObserver());
		addListenersFromProperty();
		Runtime.getRuntime().addShutdownHook(shutDownThread);
		mutationSwitcher = new MutationSwitcher();
		int totalMutations = 0, totalTests = 0;
		List<String> allTests = getAllTests();
		while (mutationSwitcher.hasNext()) {
			currentMutation = mutationSwitcher.next();
			totalMutations++;
			checkClasspath(currentMutation);
			Set<String> testsForThisRun = (MutationProperties.COVERAGE_INFORMATION ? mutationSwitcher
					.getTests()
					: new HashSet<String>(allTests));
			if (testsForThisRun == null) {
				logger.warn("No tests for " + currentMutation);
				continue;
			}
			totalTests += testsForThisRun.size();
			logger.info("Applying " + totalMutations + "th mutation with id "
					+ currentMutation.getId() + ". Running "
					+ testsForThisRun.size() + " tests");
			// Do the mutation test
			mutationSwitcher.switchOn();
			mutationStart(currentMutation);
			MutationTestResult mutationTestResult = runTests(testsForThisRun);
			mutationSwitcher.switchOff();
			mutationEnd(currentMutation);
			// Report the results
			currentMutation.setMutationResult(mutationTestResult);
			ResultReporter.report(currentMutation);
			if (totalMutations % saveIntervall == 0) {
				logger.info("Saving " + saveIntervall + " mutations");
				ResultReporter.persist();
			}
		}
		ResultReporter.persist();
		logger.info("Test Runs finished. Run " + totalTests + " tests for "
				+ totalMutations + " mutations ");
		logger.info("" + MutationObserver.summary(true));
		MutationForRun.getInstance().reportAppliedMutations();
		Runtime.getRuntime().removeShutdownHook(shutDownThread);
	}

	/**
	 * Check if class of given mutation is on the classpath. When this is not
	 * the case an exception ist thrown.
	 *
	 * @param mutation
	 *            the mutation that should be checked
	 */
	private void checkClasspath(Mutation mutation) {
		try {
			@SuppressWarnings("unused")
			Class<?> c = Class.forName(mutation.getClassName());
		} catch (ClassNotFoundException e) {
			logger.error("Class " + mutation.getClassName()
					+ " not on classpath");
			throw new RuntimeException(
					"Mutation classes are missing on the class path ", e);
		}
	}

	private MutationTestResult runTests(Set<String> testsForThisRun) {
		int counter = 0;
		int size = testsForThisRun.size();
		// prepareTests();
		List<SingleTestResult> resultsForMutation = new ArrayList<SingleTestResult>();
		for (String testName : testsForThisRun) {
			counter++;
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);

			// File scriptFile = new File(TEST_BASE_PATH, testName);
			// SingleTestResult testResult = testScript(scriptFile);
			// if (reporter != null) {
			// boolean touched = reporter.getTouchingTestCases()
			// .contains(testName);
			// testResult.setTouched(touched);
			// logger.info("Test " + testName
			// + (touched ? " touched" : " did not touch") + " mutation ");
			//
			// }
			// resultsForMutation.add(testResult);
			// return testResult;

			currentTestName = testName;
			MutationTestRunnable runnable = getTestRunnable(testName);
			testStart(testName);
			// SingleTestResult runTest = runTest(testName, actualReporter);
			runWithTimeout(runnable);
			testEnd(testName);
			SingleTestResult result = runnable.getResult();
			if (MutationProperties.STOP_AFTER_FIRST_FAIL && !result.hasPassed()) {
				logger
						.info("Test failed for mutation not running more tests. Test: "
								+ testName);
				TestMessage testMessage = result.getTestMessage();
				logger.info("Message: " + testMessage.getMessage());
				break;
			}
		}
		currentTestName = "No test name set";
		MutationTestResult mutationTestResult = SingleTestResult
				.toMutationTestResult(resultsForMutation);
		return mutationTestResult;
	}

	/**
	 * Return runnable that executes the given test.
	 *
	 * @param testName
	 *            the test to create the runnable for
	 * @return a runnable that executes the given test
	 */
	protected abstract MutationTestRunnable getTestRunnable(String testName);

	/**
	 * Return all tests that are availble to this test suite
	 *
	 * @return a list of all tests
	 */
	protected abstract List<String> getAllTests();

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
	private long runWithTimeout(MutationTestRunnable r) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<?> future = service.submit(r);
		logger.debug("Start timed test: ");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		service.shutdown();
		try {
			boolean terminated = service.awaitTermination(timeout,
					TimeUnit.SECONDS);

			long time1 = stopWatch.getTime();
			if (!terminated) {
				service.shutdownNow();
			}
			future.get(timeout, TimeUnit.SECONDS);
			long time2 = stopWatch.getTime();
			if (time2 - time1 > 1000) {
				logger.info("Get got process some extra time " + time1 + "  "
						+ time2);
			}
			future.cancel(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("Exception thrown", e);
		} catch (ExecutionException e) {
			e.printStackTrace();
			logger.error("Exception thrown", e);
		} catch (TimeoutException e) {
			e.printStackTrace();
			logger.error("Exception thrown", e);
		} catch (Throwable t) {
			t.printStackTrace();
			logger.error("Exception thrown", t);
		}
		if (!future.isDone()) {
			switchOfMutation(future);
		}
		stopWatch.stop();
		if (!r.hasFinished()) {
			String message = "Mutated Thread is still running";
			logger.warn(message);
			System.out.println(message);
			System.out.println("Exiting now");
			System.exit(10);
		}
		logger.debug("End timed test, it took " + stopWatch.getTime() + " ms");
		return stopWatch.getTime();
	}

	/**
	 * This method tries to stop a thread by disabling the current mutation.
	 * This method is called when a thread that executes a mutation does not
	 * return, e.g it is stuck in an endless loop.
	 *
	 * @param future
	 *            the future that executes the mutation
	 */
	private void switchOfMutation(Future<?> future) {
		String message1 = "Could not kill thread for mutation: "
				+ currentMutation;
		logger.info(message1 + " - Switching mutation of and wait");
		mutationSwitcher.switchOff();
		future.cancel(true);
		try {
			Thread.sleep(MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void unexpectedShutdown() {
		if (!shutdownMethodCalled) {
			shutdownMethodCalled = true;
			MutationTestResult mutationResult = currentMutation
					.getMutationResult();
			if (mutationResult == null) {
				logger.info("mutation result is null");
				mutationResult = new MutationTestResult();
				currentMutation.setMutationResult(mutationResult);
			} else {
				logger.info("Mutation result:  " + mutationResult);
			}
			String message = "Test caused the JVM to shutdown. Either to an unexpeted failure or the mutation caused an inifinite loop.";
			logger.warn(message);
			TestMessage tm = new TestMessage(currentTestName, message);
			mutationResult.addFailure(tm);
			ResultReporter.report(currentMutation);
			ResultReporter.persist();
		} else {
			logger.warn("Method already called");
		}
	}

	/**
	 * Adds a mutation listener.
	 *
	 */
	public void addMutationTestListener(MutationTestListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a mutation listener.
	 */
	public void removeMutationTestListener(MutationTestListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Inform all listeners that the tests for a mutation start.
	 *
	 * @param m
	 *            the mutation that is now applied
	 */
	private void mutationStart(Mutation m) {
		for (MutationTestListener listener : listeners) {
			listener.mutationStart(m);
		}
	}

	/**
	 * Inform all listeners that the tests for a mutation have ended.
	 *
	 * @param m
	 *            the mutation that has ended
	 */
	private void mutationEnd(Mutation m) {
		for (MutationTestListener listener : listeners) {
			listener.mutationEnd(m);
		}
	}

	/**
	 * Inform all listeners that a test starts.
	 *
	 * @param testName
	 *            the test that starts
	 */
	private void testEnd(String testName) {
		for (MutationTestListener listener : listeners) {
			listener.testEnd(testName);
		}
	}

	/**
	 * Inform all listeners that a test has ended.
	 *
	 * @param testName
	 *            the test that ends
	 */
	private void testStart(String testName) {
		for (MutationTestListener listener : listeners) {
			listener.testStart(testName);
		}
	}

	/**
	 * Adds a {@link MutationTestListener} from a property. Multiple listeners
	 * are sperated by commas.
	 */
	@SuppressWarnings("unchecked")
	private void addListenersFromProperty() {
		String listenerString = System.getProperty(MUTATION_TEST_LISTENER_KEY);
		if (listenerString != null) {
			String[] split = listenerString.split(",");
			for (String listenerName : split) {
				logger.info("Trying to add mutation test listener: "
						+ listenerName);
				try {
					Class<? extends MutationTestListener> clazz = (Class<? extends MutationTestListener>) Class
							.forName(listenerName);
					MutationTestListener listenerInstance = clazz.newInstance();
					addMutationTestListener(listenerInstance);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
