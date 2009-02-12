package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.TestSuiteUtil;

/**
 * Mutation test driver for Junit 3 tests.
 *
 * @author David Schuler
 *
 */
public class Junit3MutationTestDriver extends MutationTestDriver {
	private static Logger logger = Logger
			.getLogger(Junit3MutationTestDriver.class);

	private final class SingleTestListener implements TestListener {

		private String message;

		public void addError(Test test, Throwable t) {
			message = "Error: " + test + " - " + t;
		}

		public void addFailure(Test test, AssertionFailedError t) {
			message = "Failure: " + test + " - " + t;
		}

		public void endTest(Test test) {
		}

		public void startTest(Test test) {
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message
		 *            the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
	}

	// private final TestSuite suite;

	private final Map<String, Test> allTests;

	// private List<SingleTestResult> resultsForMutation = new
	// ArrayList<SingleTestResult>();

	public Junit3MutationTestDriver(TestSuite suite) {
		// this.suite = suite;
		allTests = TestSuiteUtil.getAllTests(suite);
	}

	@Override
	protected List<String> getAllTests() {
		return Collections.unmodifiableList(new ArrayList<String>(allTests
				.keySet()));
	}

	@Override
	protected MutationTestRunnable getTestRunnable(final String testName) {
		MutationTestRunnable r = new MutationTestRunnable() {

			boolean finished = false;

			final TestResult result = new TestResult();

			private SingleTestListener listener = new SingleTestListener();

			private boolean failed = false;

			private StopWatch stopWatch = new StopWatch();

			public void run() {
				try {
					stopWatch.start();
					Test actualtest = allTests.get(testName);
					result.addListener(listener);
					actualtest.run(result);
				} catch (Exception e) {
					logger.debug("Cought exception from test " + e
							+ " Message " + e.getMessage());
				} finally {
					stopWatch.stop();
					finished = true;
				}
			}

			public synchronized boolean hasFinished() {
				return finished;
			}

			public SingleTestResult getResult() {

				String message = listener.getMessage();
				if (message == null) {
					message = "";
				}

				TestOutcome outcome = TestOutcome.PASS;
				if (result.failureCount() > 0) {
					outcome = TestOutcome.FAIL;
				} else if (result.errorCount() > 0) {
					outcome = TestOutcome.ERROR;
				}
				SingleTestResult res = new SingleTestResult(testName, message,
						outcome, stopWatch.getTime());
				return res;
			}

			public void setFailed(boolean failed) {
				TimeoutException timeoutException = new TimeoutException(
						"Test took to long " + stopWatch.getTime() + " ms");
				result.addError(allTests.get(testName), timeoutException);
				listener.addError(allTests.get(testName), timeoutException);
			}

		};
		return r;
	}
}