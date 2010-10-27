/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.runtime.testsuites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import de.unisb.cs.st.ds.util.io.XmlIo;

/**
 * Executes the tests in a random order multiple times. In order to see if they
 * are independent of each other (e.g. produce the same results when executed
 * multiple times).
 * 
 * For a run three files are produced:
 * <ul>
 * <li>A file that contains all failing tests</li>
 * <li>A file that contains all passing tests</li>
 * <li>A file that contains all tests that had a different result for multiple
 * runs</li>
 * <li>A file that contains all tests that had a the same result for multiple
 * runs</li>
 * </ul>
 * 
 * @author David Schuler
 * 
 */
public class RandomPermutationTestSuite extends TestSuite {

	private static final String TESTS_FAILING_FILENAME = "tests-failing.xml";

	private static final String TESTS_PASSING_FILENAME = "tests-passing.xml";

	private static final String TESTS_COMMON_OUTCOME_FILENAME = "tests-common-outcome.xml";

	private static final String TESTS_DIFFERENT_OUTCOME_FILENAME = "tests-different-outcome.xml";

	private static Logger logger = Logger
			.getLogger(RandomPermutationTestSuite.class);

	private static final int DEFAULT_LIMIT = 6;

	private static final boolean PARALLEL = false;

	public RandomPermutationTestSuite(String name) {
		super(name);
	}

	@Override
	public void run(TestResult result) {
		if (PARALLEL) {
			runInParrallel(6);
		} else {
			Map<String, Test> allTests = TestSuiteUtil.getAllTests(this);
			Map<String, List<TestResult>> testResults = new HashMap<String, List<TestResult>>();
			int totalTestsRun = 0;
			long start = System.currentTimeMillis();
			for (int i = 0; i < DEFAULT_LIMIT; i++) {
				logger.info("Round" + i);
				List<Map.Entry<String, Test>> shuffeledList = getShuffledTestList(allTests);
				int testsRun = runListOfTests(shuffeledList, testResults);
				totalTestsRun += testsRun;
			}
			analyzeTestResults(testResults, allTests);
			long time = System.currentTimeMillis() - start;
			logger.info("Run " + totalTestsRun + " tests in " + time + " ms");
		}
	}

	private void runInParrallel(int processes) {

		Map<String, Test> allTests = TestSuiteUtil.getAllTests(this);
		Map<String, List<TestResult>> testResults = new HashMap<String, List<TestResult>>();
		int totalTestsRun = 0;
		long start = System.currentTimeMillis();
		for (Map.Entry<String, Test> entry : allTests.entrySet()) {
			String testName = entry.getKey();
//			if(!testName.equals("de.susebox.jtopas.TestPluginTokenizer.testContentsFormatting-instance-13")){
//				continue;
//			}
			logger.info("Running test " + testName + " in " + processes
					+ " threads");
			ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(processes);
			List<TestResult> resultsForThisTest = new ArrayList<TestResult>();

			List<Future<Object>> futures = new ArrayList<Future<Object>>();
			Map<Future<Object>, TestResult> resultForFuture = new HashMap<Future<Object>, TestResult>();
			for (int i = 0; i < processes; i++) {
				TestResult resultForTest = new TestResult();
				Callable<Object> callable = TestSuiteUtil.getCallable(entry
						.getValue(), resultForTest);
				Future<Object> submit = pool.submit(callable);

				futures.add(submit);

			}
			long startPool = System.currentTimeMillis();
			while (!pool.isTerminated()) {
				try {
					boolean allProcessesFinished = pool.awaitTermination(10,
							TimeUnit.SECONDS);
					if (pool.getActiveCount() == 0) {
						pool.shutdown();
					}
					if (System.currentTimeMillis() - startPool > 20000) {
						logger.info("Shutdown pool because of time limit");
						for (Future<Object> f : futures) {
							logger.info("Canceling future" + f);
							logger.info("Currently Running threads");
							Set<Entry<Thread, StackTraceElement[]>> entrySet = Thread
									.getAllStackTraces().entrySet();
							for (Entry<Thread, StackTraceElement[]> threadEntry : entrySet) {
								Thread t = threadEntry.getKey();
								StackTraceElement[] stacktrace = threadEntry
										.getValue();
								if (t != null) {
									if(stacktrace!=null)
									logger.info(t + "  "
											+ Arrays.toString(stacktrace));
								}
								else{
									logger.info("Thread has no stack trace" + t);
								}
									logger.info("Trying to stop" + t);
								}


							try {
								f.get(10, TimeUnit.MILLISECONDS);
							} catch (ExecutionException e) {
								resultForFuture.get(f).addError(
										entry.getValue(), e);
								e.printStackTrace();
								logger.fatal(testName, e);
							} catch (TimeoutException e) {
								resultForFuture.get(f).addError(
										entry.getValue(), e);
								e.printStackTrace();
								logger.fatal(testName, e);
							}
							logger.info("canceled?  " + f.isCancelled());
							f.cancel(true);
						}
						pool.shutdownNow();
					}
					// int processesFinished = getNumberOfFinishedProcesses();
					// int processesRunning = handleRunningProcess();
					// logger.info(processesFinished + " processes are finished
					// and "
					// + processesRunning + " are running");
					logger.info("Processess finished: " + allProcessesFinished
							+ " Active: " + pool.getActiveCount()
							+ " Completed: " + pool.getCompletedTaskCount());
					if (allProcessesFinished) {
						pool.shutdown();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			totalTestsRun += resultsForThisTest.size();
			logger.info("All tests finished for: " + testName);
			testResults.put(testName, resultsForThisTest);
		}
		analyzeTestResults(testResults, allTests);
		long time = System.currentTimeMillis() - start;
		logger.info("Run " + totalTestsRun + " tests in " + time + " ms");

	}

	private void analyzeTestResults(Map<String, List<TestResult>> testResults,
			Map<String, Test> allTests) {
		Set<String> testsDifferentOutcome = new HashSet<String>();
		Set<String> testsFailing = new HashSet<String>();
		for (Entry<String, List<TestResult>> entry : testResults.entrySet()) {
			String testName = entry.getKey();
			boolean first = true;
			int failures = 0;
			int errors = 0;
			int runs = 0;
			for (TestResult testResult : entry.getValue()) {
				if (first) {
					first = false;
					failures = testResult.failureCount();
					errors = testResult.errorCount();
					runs = testResult.runCount();
				} else {
					if (failures != testResult.failureCount()
							|| errors != testResult.errorCount()
							|| runs != testResult.runCount()) {
						logger.warn("Different TestResults for test"
								+ entry.getKey());
						testsDifferentOutcome.add(testName);
					}
				}
				if (testResult.errorCount() >= 1
						|| testResult.failureCount() >= 1) {
					testsFailing.add(testName);
				}
			}
		}
		Set<String> testsCommonOutcome = new HashSet<String>();
		Set<String> testsPassing = new HashSet<String>();

		for (String testName : allTests.keySet()) {
			if (!testsDifferentOutcome.contains(testName)) {
				testsCommonOutcome.add(testName);
			}
			if (!testsFailing.contains(testName)) {
				testsPassing.add(testName);
			}
		}
		XmlIo.toXML(testsDifferentOutcome, TESTS_DIFFERENT_OUTCOME_FILENAME);
		XmlIo.toXML(testsCommonOutcome, TESTS_COMMON_OUTCOME_FILENAME);
		XmlIo.toXML(testsFailing, TESTS_FAILING_FILENAME);
		XmlIo.toXML(testsPassing, TESTS_PASSING_FILENAME);
		printResultInfo(testsDifferentOutcome, testsFailing);

	}

	private void printResultInfo(Set<String> testsDifferentOutcome,
			Set<String> testsFailing) {
		if (testsFailing.size() > 0) {
			System.out.println("There were failing tests");
			for (String failingTest : testsFailing) {
				System.out.println("\t" + failingTest);
			}
		} else {
			System.out.println("All tests passed");
		}
		if (testsDifferentOutcome.size() > 0) {
			System.out
					.println("there where tests with a different outcome for different runs");
			for (String testName : testsDifferentOutcome) {
				System.out.println("\t" + testName);
			}
		} else {
			System.out.println("All tests had the same outcome in all runs");
		}
	}

	private List<Map.Entry<String, Test>> getShuffledTestList(
			Map<String, Test> allTests) {
		Set<Map.Entry<String, Test>> entrySet = allTests.entrySet();
		List<Map.Entry<String, Test>> shuffeledList = new ArrayList<Entry<String, Test>>(
				entrySet);
		Collections.shuffle(shuffeledList);
		return shuffeledList;
	}

	private int runListOfTests(List<Map.Entry<String, Test>> shuffeledList,
			Map<String, List<TestResult>> testResults) {
		int testsRun = 0;
		for (Map.Entry<String, Test> entry : shuffeledList) {
			TestResult resultForTest = new TestResult();
			String testName = entry.getKey();
			logger.info("Running Test " + entry.getValue());
			try {
				runTest(entry.getValue(), resultForTest);
			} catch (Error e) {
				logger.warn("Exception During test " + e.getMessage());
				e.printStackTrace();
				if (resultForTest.failureCount() + resultForTest.errorCount() < 1) {
					resultForTest.addError(entry.getValue(), e);
				}
			}
			logger
					.debug("Run count for last test: "
							+ resultForTest.runCount());
			testsRun++;
			List<TestResult> resultList;
			if (testResults.get(testName) != null) {
				resultList = testResults.get(testName);
			} else {
				resultList = new ArrayList<TestResult>();
				testResults.put(testName, resultList);
			}
			resultList.add(resultForTest);
		}
		return testsRun;
	}

	/**
	 * Transforms a {@link TestSuite} to a {@link RandomPermutationTestSuite}.
	 * This method is called by instrumented code to insert this class instead
	 * of the TestSuite.
	 *
	 * @param testSuite
	 *            The original TestSuite.
	 * @return The {@link RandomPermutationTestSuite} that contains the given
	 *         TestSuite.
	 */
	public static RandomPermutationTestSuite toRandomPermutationTestSuite(
			TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		RandomPermutationTestSuite returnTestSuite = new RandomPermutationTestSuite(
				testSuite.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
