/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import java.util.ArrayList;
import java.util.Collections;
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
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationSwitcher;
import de.unisb.cs.st.javalanche.mutation.runtime.ResultReporter;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.Junit3MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners.InvariantPerTestCheckListener;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners.InvariantPerTestListener;
import de.unisb.cs.st.javalanche.coverage.CoverageMutationListener;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

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
	private long timeout = MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS;

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
	 * Instantiate a MutationTestDriver from a property (mutation.test.driver).
	 * And uses this driver to do the mutation tests.
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
	 * Runs the mutation testing. Depending on MutationProperties.RUN_MODE the
	 * corresponding method is called.
	 */
	public final void run() {
		logger.debug("Run Mode" + RUN_MODE);
		if (RUN_MODE == RunMode.MUTATION_TEST
				|| RUN_MODE == RunMode.MUTATION_TEST_INVARIANT
				|| RUN_MODE == RunMode.MUTATION_TEST_INVARIANT_PER_TEST
				|| RUN_MODE == RunMode.MUTATION_TEST_COVERAGE) {
			if (RUN_MODE == RunMode.MUTATION_TEST_INVARIANT_PER_TEST) {
				addMutationTestListener(new InvariantPerTestListener());
			}
			if (RUN_MODE == RunMode.MUTATION_TEST_COVERAGE) {
				addMutationTestListener(new CoverageMutationListener());
				// runNormalTests();
			}
			listeners.addLast(new ResultReporter());
			runMutations();
		} else if (RUN_MODE == RunMode.SCAN || RUN_MODE == RunMode.SCAN_ECLIPSE) {
			scanTests();
		} else if (RUN_MODE == RunMode.CHECK_INVARIANTS_PER_TEST) {
			addMutationTestListener(new InvariantPerTestCheckListener());
			runNormalTests();
		} else if (RUN_MODE == RunMode.CREATE_COVERAGE) {
			// runNormalTests();
			// coldRun();
			addMutationTestListener(new CoverageMutationListener());
			runPermutedTests();
		} else if (RUN_MODE == RunMode.TEST_PERMUTED) {
			runPermutedTests();
		} else {
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
			stopWatch.reset();
			stopWatch.start();
			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			logger.info("Test took "
					+ DurationFormatUtils
							.formatDurationHMS(stopWatch.getTime()) + " "
					+ testName);
			if (!result.hasPassed()) {
				allPass = false;
				failing.add(result);
				logger.warn("Test has not passed " + result.getTestMessage());
			}
			testEnd(testName);
		}
		testsEnd();
		if (allPass) {
			String message = "All " + allTests.size() + " tests passed ";
			System.out.println(message);
			logger.info(message);
		} else {
			logger.warn("Not all tests passed");
			for (SingleTestResult str : failing) {
				logger.warn(str.getTestMessage().getTestCaseName() + ": "
						+ str.getTestMessage());
			}
			XmlIo.toXML(failing, "failed-tests.xml");
		}
	}

	/**
	 * Runs the tests without applying any changes.And executes each test
	 * multiple times and in a different order. This method is used to check if
	 * the driver works correctly.
	 */
	private void runPermutedTests() {
		logger.info("Running permuted tests for project "
				+ MutationProperties.PROJECT_PREFIX);
		addListenersFromProperty();
		List<String> allTests = new ArrayList<String>(getAllTests());
		timeout = Integer.MAX_VALUE;
		List<SingleTestResult> allFailingTests = new ArrayList<SingleTestResult>();
		coldRun(allTests);
		testsStart();
		int permutations = MutationProperties.TEST_PERMUTATIONS;
		for (int i = 0; i < permutations; i++) {
			logger.info("Shuffling tests. Round " + (i + 1));
			Collections.shuffle(allTests);
			List<SingleTestResult> failingTests = runNormalTests(allTests);
			allFailingTests.addAll(failingTests);
		}
		testsEnd();
		if (allFailingTests.size() == 0) {
			String message = "All " + allTests.size() + " tests passed for "
					+ permutations + " permutations.";
			System.out.println(message);
			logger.info(message);
		} else {
			logger.warn("Not all tests passed");
			for (SingleTestResult str : allFailingTests) {
				logger.warn(str.getTestMessage().getTestCaseName() + ": "
						+ str.getTestMessage());
			}
		}
	}

	private List<SingleTestResult> runNormalTests(List<String> tests) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int counter = 0;
		int size = tests.size();
		List<SingleTestResult> failing = new ArrayList<SingleTestResult>();
		for (String testName : tests) {
			counter++;
			String duration = DurationFormatUtils.formatDurationHMS(stopWatch
					.getTime());
			logger.info(duration + " (" + counter + " / " + size
					+ ") Running test:  " + testName);
			MutationTestRunnable runnable = getTestRunnable(testName);
			testStart(testName);

			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			logger.debug(result.getTestMessage());
			if (!result.hasPassed()) {
				failing.add(result);
				logger.warn("Test has not passed " + testName);
			}
			testEnd(testName);
		}
		return failing;
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
	 * Runs the all available tests without any special modifications. This has
	 * the purpose to get all classes loaded that are involved in the testing.
	 * 
	 * @return true, if all tests passed.
	 */
	private boolean coldRun() {
		return coldRun(getAllTests());
	}

	/**
	 * Runs the given list of tests without any special modifications. This has
	 * the purpose to get all classes loaded that are involved in the testing.
	 * 
	 * @param allTests
	 *            the tests to run
	 * @return true, if all tests passed.
	 */
	private boolean coldRun(List<String> allTests) {
		int counter = 0;
		int size = allTests.size();
		logger.info("Start cold run  of tests to get all classes loaded");
		List<String> failed = new ArrayList<String>();
		for (String testName : allTests) {
			counter++;
			logger.info("(" + counter + " / " + size + ") Running test:  "
					+ testName);
			MutationTestRunnable runnable = getTestRunnable(testName);
			runWithTimeout(runnable);
			SingleTestResult result = runnable.getResult();
			if (!result.hasPassed()) {
				failed.add(testName + " : " + result);
			}
		}
		if (failed.size() > 0) {
			logger.warn("Tests failed");
			for (String failMessage : failed) {
				logger.warn(failMessage);
			}
		}
		return failed.size() == 0;
	}

	/**
	 * Method that runs he mutation testsing. All mutations for this run are
	 * caried out and their corresponding tests are run.
	 */
	public void runMutations() {
		logger.info("Running Mutations "
				+ MutationForRun.hasMutationsWithoutResults());
		if (checkMutations()) {
			return;
		}
		shutDownThread = new Thread(new MutationDriverShutdownHook(this));
		addMutationTestListener(new MutationObserver());
		addListenersFromProperty();
		Runtime.getRuntime().addShutdownHook(shutDownThread);
		mutationSwitcher = new MutationSwitcher();
		int totalMutations = 0, totalTests = 0;
		List<String> allTests = getAllTests();
		if (doColdRun) {
			long timeoutBack = getTimeout();
			setTimeout(Integer.MAX_VALUE);
			boolean allPassed = coldRun(allTests);
			if (!allPassed) {
				throw new RuntimeException("Tests in cold run failed");
			}
			setTimeout(timeoutBack);
		}
		testsStart();
		while (mutationSwitcher.hasNext()) {
			currentMutation = mutationSwitcher.next();
			totalMutations++;
			checkClasspath(currentMutation);
			Set<String> coveredTests = mutationSwitcher.getTests();
			Set<String> testsForThisRun = coveredTests.size() > 0 ? coveredTests
					: new HashSet<String>(allTests);
			System.out.println("Applying " + totalMutations
					+ "th mutation with id " + currentMutation.getId()
					+ ". Running " + testsForThisRun.size() + " tests");
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
		checkMutations();
		logger.info("Test Runs finished. Run " + totalTests + " tests for "
				+ totalMutations + " mutations ");
		System.out.println(MutationObserver.summary(true));
		MutationForRun.getInstance().reportAppliedMutations();
		Runtime.getRuntime().removeShutdownHook(shutDownThread);
	}

	private boolean checkMutations() {
		boolean allResults = !MutationForRun.hasMutationsWithoutResults();
		if (allResults) {
			String tag = "ALL_RESULTS";
			System.out.println(tag);
			logger.info(tag);
			String message = "All mutations have results - this means they have already been aplied and executed";
			System.out.println(message);
			logger.info(message);
		}
		return allResults;
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
	 * @return a mutation test result that summarizes the outcome of the tests
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
	 * @param r
	 *            the test to be run
	 * @return the time needed for executing the test
	 */
	protected long runWithTimeout(MutationTestRunnable r) {

		// ArrayList<Thread> threadsPre = ThreadUtil.getThreads();
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<?> future = service.submit(r);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		service.shutdown();
		String exceptionMessage = null;
		Throwable capturedThrowable = null;
		try {
			logger.debug("Start timed test1: ");
			boolean terminated = service.awaitTermination(timeout,
					TimeUnit.SECONDS);
			logger.debug("First timeout");
			long time1 = stopWatch.getTime();
			if (!terminated) {
				service.shutdownNow();
			}
			future.get(timeout, TimeUnit.SECONDS);
			logger.debug("Second timeout");
			long time2 = stopWatch.getTime();
			if (time2 - time1 > 1000) {
				logger.info("Process got some extra time: " + (time2 - time1)
						+ "  " + time2);
			}
			future.cancel(true);

		} catch (InterruptedException e) {
			capturedThrowable = e;
		} catch (ExecutionException e) {
			capturedThrowable = e;
		} catch (TimeoutException e) {
			exceptionMessage = "Mutation causes test timeout";
			capturedThrowable = e;
		} catch (Throwable t) {
			capturedThrowable = t;
		} finally {
			if (capturedThrowable != null) {
				if (exceptionMessage == null) {
					exceptionMessage = "Exception caught during test execution.";
				}
				r.setFailed(exceptionMessage + " - " + capturedThrowable);
			}
		}
		if (!future.isDone()) {
			r.setFailed("Mutated Thread is still running after timeout.");
			switchOfMutation(future);
		}
		stopWatch.stop();

		if (!r.hasFinished()) {
			r
					.setFailed("Mutated Thread is still running after mutation is switched of.");
			if (shutDownThread != null) {
				Runtime.getRuntime().removeShutdownHook(shutDownThread);
			}
			logger.warn(RESTART_MESSAGE);
			TestMessage tm = new TestMessage(currentTestName, RESTART_MESSAGE,
					stopWatch.getTime());
			boolean touched = MutationObserver.getTouchingTestCases().contains(
					currentTestName);
			tm.setTouched(touched);
			setTestMessage(tm);
			String m;
			m = "Mutated Thread is still running";
			logger.warn(m);
			testEnd(currentTestName);
			if (currentMutation != null) {
				mutationEnd(currentMutation);
			}
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
			Thread.sleep(MutationProperties.DEFAULT_TIMEOUT_IN_SECONDS);
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
		if (currentMutation != null) {
			MutationTestResult mutationResult = currentMutation
					.getMutationResult();
			if (mutationResult == null) {
				logger.info("mutation result is null");
				mutationResult = new MutationTestResult();
				currentMutation.setMutationResult(mutationResult);

			} else {
				logger.info("Mutation result:  " + mutationResult);
			}
			mutationResult.addFailure(tm);
			if (tm.isTouched()) {
				mutationResult.setTouched(true);
			}
		}
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

	/**
	 * @return the timeout
	 */
	private long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	private void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
