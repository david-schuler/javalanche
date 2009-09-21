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
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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

	public final static class SingleTestListener implements TestListener {

		private String message;

		public void addError(Test test, Throwable t) {
			message = "Error: " + test + " - " + t + " - " + getStackTrace(t);
		}

		public void addFailure(Test test, AssertionFailedError t) {
			message = "Failure: " + test + " - " + t + " - " + getStackTrace(t);
		}

		public static String getStackTrace(Throwable t) {
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			t.printStackTrace(printWriter);
			return result.toString();
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
					if (actualtest == null) {
						String message = "Test not found in: " + testName
								+ "\n All Tests:" + allTests;
						System.out.println(message);
						logger.warn(message);
						System.exit(0);
					}
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

			public void setFailed(String message) {
				Exception e = new Exception(message);
				result.addError(allTests.get(testName), e);
			}

		};
		return r;
	}
}