package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.lang.time.StopWatch;

import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.TestSuiteUtil;

public class Junit3MutationTestDriver extends MutationTestDriver {

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

	private final TestSuite suite;

	private final Map<String, Test> allTests;

	private List<SingleTestResult> resultsForMutation = new ArrayList<SingleTestResult>();

	public Junit3MutationTestDriver(TestSuite suite) {
		this.suite = suite;
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

			SingleTestListener listener = new SingleTestListener();

			private long duration;

			public void run() {
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				Test test = allTests.get(testName);
				test.run(result);
				stopWatch.stop();
				duration = stopWatch.getTime();
				finished = true;

			}

			public synchronized boolean hasFinished() {
				return finished;
			}

			public SingleTestResult getResult() {
				String message =  listener
				.getMessage();
				if(message==null){
					message = "";
				}
				SingleTestResult res = new SingleTestResult(testName,message, result.failureCount()
						+ result.errorCount() < 1, duration);
				return res;
			}

		};
		return r;
	}
}