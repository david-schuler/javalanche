package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationSwitcher;
import de.unisb.cs.st.javalanche.mutation.runtime.ResultReporter;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners.InvariantPerTestListener;

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

	protected static final String SINGLE_TEST_NAME_KEY = "single.test.name";

	private static final String DRIVER_KEY = "mutation.test.driver";

	private static final String MUTATION_TEST_LISTENER_KEY = "javalanche.mutation.test.listener";

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

	public static final String RESTART_MESSAGE = "Mutation test thread could not be stopped. Shutting down JVM.";

	/**
	 * The listeneres that are informed about mutation events. The order in
	 * which the listeners are called is not specified. However the
	 * {@link ResultReporter} that stores the results to the database will
	 * always be called at last.
	 */
	private LinkedList<MutationTestListener> listeners = new LinkedList<MutationTestListener>();

	/**
	 * True if all tests should be run once before the actual mutation testing.
	 */
	protected boolean doColdRun = true;

	private Thread shutDownThread;

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

	/**
	 * Runs the mutation testing. Depending on
	 * {@link MutationProperties.RUN_MODE} the corresponding method is called.
	 */
	public final void run() {
		if (MutationProperties.RUN_MODE == RunMode.MUTATION_TEST
				|| MutationProperties.RUN_MODE == RunMode.MUTATION_TEST_INVARIANT
				|| MutationProperties.RUN_MODE == RunMode.MUTATION_TEST_INVARIANT_PER_TEST) {
			if (MutationProperties.RUN_MODE == RunMode.MUTATION_TEST_INVARIANT_PER_TEST) {
				addMutationTestListener(new InvariantPerTestListener());
			}
			listeners.addLast(new ResultReporter());
			runMutations();
		} else if (MutationProperties.RUN_MODE == RunMode.SCAN) {
			scanTests();
		} else {
			System.out.println("MutationTestDriver.run()"
					+ MutationProperties.RUN_MODE);
			runNormalTests();
		}

	}

	/**
	 * Runs the tests without applying any changes. This method is used to check
	 * if the driver works correctly.
	 */
	private void runNormalTests() {
		logger.info("Running tests of project "
				+ MutationProperties.PROJECT_PREFIX);
		// addMutationTestListener(new AdabuListener());
		addListenersFromProperty();
		List<String> allTests = getAllTests();
		int counter = 0;
		int size = allTests.size();
		timeout = Integer.MAX_VALUE;
		boolean allPass = true;
		List<SingleTestResult> failing = new ArrayList<SingleTestResult>();
		testsStart();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		coldRun(allTests);
		for (String testName : allTests) {
			counter++;
			logger.info(DurationFormatUtils.formatDurationHMS(stopWatch
					.getTime())
					+ " ("
					+ counter
					+ " / "
					+ size
					+ ") Running test:  "
					+ testName);
			MutationTestRunnable runnable = getTestRunnable(testName);
			testStart(testName);

			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			logger.info(result.getTestMessage());
			if (!result.hasPassed()) {
				allPass = false;
				failing.add(result);
				logger.warn("Test has not passed " + testName);
			}
			testEnd(testName);
		}
		testsEnd();
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
			logger.debug("Set testName " + testName);
			CoverageDataRuntime.setTestName(testName);
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);

			MutationTestRunnable runnable = getTestRunnable(testName);
			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			long duration = result.getDuration();
			TestName tm = new TestName(testName,
					MutationProperties.PROJECT_PREFIX, duration);
			QueryManager.save(tm);
			CoverageDataRuntime.unsetTestName(testName);
		}
		CoverageDataUtil.endCoverage();
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
		shutDownThread = new Thread(new MutationDriverShutdownHook(this));
		addMutationTestListener(new MutationObserver());
		addListenersFromProperty();
		Runtime.getRuntime().addShutdownHook(shutDownThread);
		mutationSwitcher = new MutationSwitcher();
		int totalMutations = 0, totalTests = 0;
		List<String> allTests = getAllTests();
		if (doColdRun) {
			coldRun(allTests);
		}
		testsStart();
		while (mutationSwitcher.hasNext()) {
			currentMutation = mutationSwitcher.next();
			totalMutations++;
			checkClasspath(currentMutation);
			Set<String> testsForThisRun = (MutationProperties.COVERAGE_INFORMATION ? mutationSwitcher
					.getTests()
					: new HashSet<String>(allTests));
			if (testsForThisRun == null) {
				logger.warn("No tests for " + currentMutation);
				currentMutation.setMutationResult(MutationTestResult.NO_RESULT);
				// report(currentMutation);
				continue;
			}
			logger.info("Applying " + totalMutations + "th mutation with id "
					+ currentMutation.getId() + ". Running "
					+ testsForThisRun.size() + " tests");
			// Do the mutation test
			mutationSwitcher.switchOn();
			mutationStart(currentMutation);
			MutationTestResult mutationTestResult = runTests(testsForThisRun);
			totalTests += mutationTestResult.getRuns();
			mutationSwitcher.switchOff();
			// Report the results
			currentMutation.setMutationResult(mutationTestResult);
			mutationEnd(currentMutation);

		}
		testsEnd();
		logger.info("Test Runs finished. Run " + totalTests + " tests for "
				+ totalMutations + " mutations ");
		logger.info("" + MutationObserver.summary(true));
		MutationForRun.getInstance().reportAppliedMutations();
		Runtime.getRuntime().removeShutdownHook(shutDownThread);
	}

	/**
	 * Check if class of given mutation is on the classpath. When this is not
	 * the case an exception is thrown.
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

	/**
	 * Run all the given tests
	 *
	 * @param testsForThisRun
	 *            a set of tests to be run
	 * @return a mutaiton test result that sumarizes the outcome of the tests
	 */
	private MutationTestResult runTests(Set<String> testsForThisRun) {
		int counter = 0;
		int size = testsForThisRun.size();
		// prepareTests();
		List<SingleTestResult> resultsForMutation = new ArrayList<SingleTestResult>();
		for (String testName : testsForThisRun) {
			counter++;
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);

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
			runWithTimeout(runnable);
			testEnd(testName);
			SingleTestResult result = runnable.getResult();
			boolean touched = MutationObserver.getTouchingTestCases().contains(
					testName);
			result.setTouched(touched);
			resultsForMutation.add(result);
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
	protected long runWithTimeout(MutationTestRunnable r) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<?> future = service.submit(r);
		logger.debug("Start timed test: ");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		service.shutdown();
		boolean exception = false;
		try {
			boolean terminated = service.awaitTermination(timeout,
					TimeUnit.SECONDS);

			long time1 = stopWatch.getTime();
			if (!terminated) {
				service.shutdownNow();
			}
			// Class.forName("org.apache.log4j.spi.ThrowableInformation");
			future.get(timeout, TimeUnit.SECONDS);
			long time2 = stopWatch.getTime();
			if (time2 - time1 > 1000) {
				logger.info("Process got some extra time: " + (time2 - time1)
						+ "  " + time2);
			}
			future.cancel(true);

		} catch (InterruptedException e) {
			e.printStackTrace();
			exception = true;
			logger.error("Exception thrown", e);
		} catch (ExecutionException e) {
			exception = true;
			e.printStackTrace();
			logger.error("Exception thrown", e);
		} catch (TimeoutException e) {
			exception = true;
			e.printStackTrace();
			logger.error("Exception thrown", e);
		} catch (Throwable t) {
			exception = true;
			t.printStackTrace();
			logger.error("Exception thrown", t);
		} finally {
			if (exception) {
				r.setFailed(true);
			}
		}
		if (!future.isDone()) {
			r.setFailed(true);
			switchOfMutation(future);
		}
		stopWatch.stop();
		if (!r.hasFinished()) {
			r.setFailed(true);
			if (shutDownThread != null) {
				Runtime.getRuntime().removeShutdownHook(shutDownThread);
			}
			logger.warn(RESTART_MESSAGE);
			setTestMessage(new TestMessage(currentTestName, RESTART_MESSAGE,
					stopWatch.getTime()));
			String m = "Mutated Thread is still running";
			logger.warn(m);
			testEnd(currentTestName);
			mutationEnd(currentMutation);
			testsEnd();
			System.out.println(m);
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
		logger.info(message1 + " - Switching mutation of");
		if (mutationSwitcher != null) {
			mutationSwitcher.switchOff();
		}
		future.cancel(true);
		try {
			logger.info("Sleeping   ");
			Thread.sleep(MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method gets called when the mutation testing is finished because of an
	 * unexpected event. Most likely this will be an endless loop caused by a
	 * mutation.
	 */
	public void unexpectedShutdown() {
		if (!shutdownMethodCalled) {
			shutdownMethodCalled = true;
			String message = "Test caused the JVM to shutdown.";
			logger.warn(message);
			setTestMessage(new TestMessage(currentTestName, message, 0));
			testEnd(currentTestName);
			mutationEnd(currentMutation);
			testsEnd();
		} else {
			logger.warn("Method already called");
		}
	}

	private void setTestMessage(TestMessage tm) {
		MutationTestResult mutationResult = currentMutation.getMutationResult();
		if (mutationResult == null) {
			logger.info("mutation result is null");
			mutationResult = new MutationTestResult();
			currentMutation.setMutationResult(mutationResult);
		} else {
			logger.info("Mutation result:  " + mutationResult);
		}
		mutationResult.addFailure(tm);
	}

	/**
	 * Adds a mutation listener.
	 *
	 */
	public void addMutationTestListener(MutationTestListener listener) {
		listeners.addFirst(listener);
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
	 * Inform all listeners that the test process has started.
	 */
	private void testsStart() {
		for (MutationTestListener listener : listeners) {
			listener.start();
		}
	}

	/**
	 * Inform all listeners that the test process has finished.
	 */
	private void testsEnd() {
		for (MutationTestListener listener : listeners) {
			listener.end();
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

	/**
	 * Run one single given test.
	 */
	protected void runSingleTest() {
		String testName = System.getProperty(SINGLE_TEST_NAME_KEY);
		logger.info("Running single test" + testName);
		MutationTestRunnable runnable = getTestRunnable(testName);
		runWithTimeout(runnable);
		SingleTestResult result = runnable.getResult();
		logger.info("Test result: " + result);
		if (!result.hasPassed()) {
			logger.warn("Test has not passed " + testName);
		} else {
			logger.info("Test passed " + testName);
		}
	}
}
