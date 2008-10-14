package de.unisb.cs.st.javalanche.mutation.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import org.apache.log4j.Logger;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

/**
 * {@link TestListener} used for the tests executed during mutation testing .It
 * collects the passing error and failing tests and their duration.
 *
 * @author David Schuler
 *
 */
public class MutationTestListener implements TestListener {

	private static Logger logger = Logger.getLogger(MutationTestListener.class);

	/**
	 * Stores the start time of a test not thread safe if the same test runs in
	 * different threads, which shouldn't be the case.
	 */
	private Map<Test, Long> startTime = new HashMap<Test, Long>();

	/**
	 * Stores the error messages of test that failed because of an error.
	 */
	private List<TestMessage> errorMessages = new ArrayList<TestMessage>();

	/**
	 * Stores the messages of tests were an assertion failed.
	 */
	private List<TestMessage> failureMessages = new ArrayList<TestMessage>();

	/**
	 * Stores the messages of tests that passed.
	 */
	private List<TestMessage> passingMessages = new ArrayList<TestMessage>();

	private List<Test> alreadyReported = new ArrayList<Test>();

	private Test previousTest;

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 *      java.lang.Throwable)
	 */
	public void addError(Test test, Throwable t) {
		if (alreadyReported.contains(test)) {
			logger.warn("Result for this test was already reported " + test);
			return;
		}
		logger.debug("Error added for test: " + test + "\nStack Trace:\n"
				+ Arrays.toString(t.getStackTrace()));
		long duration = getDuration(test);
		errorMessages.add(new TestMessage(test.toString(), t.toString()
				+ "\nStack Trace:\n" + stackTraceToString(t.getStackTrace()),
				duration));
		alreadyReported.add(test);
	}

	private String stackTraceToString(StackTraceElement[] stackTrace) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement stackTraceElement : stackTrace) {
			sb.append(stackTraceElement.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 *      junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test test, AssertionFailedError t) {
		if (alreadyReported.contains(test)) {
			logger.warn("Result for this test was already reported " + test);
			return;
		}
		logger.debug("Failure added for test: " + test);
		long duration = getDuration(test);
		failureMessages.add(new TestMessage(test.toString(), t.toString(),
				duration));
		alreadyReported.add(test);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test test) {
		if (!alreadyReported.contains(test)) {
			long duration = getDuration(test);
			passingMessages.add(new TestMessage(test.toString(), "test passed",
					duration));
			alreadyReported.add(test);
			logger.debug("Test ended normaly:" + test);
		}
	}

	/**
	 * Returns the duration for the test.
	 *
	 * @param test
	 *            The test for which the duration is computed for.
	 * @return The duration in milliseconds for this test.
	 */
	private long getDuration(Test test) {
		long duration = 0;
		if (startTime.containsKey(test)) {
			duration = System.currentTimeMillis() - startTime.get(test);
		} else {
			logger.warn("No start time for test: " + test);
		}
		return duration;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test test) {
		if (previousTest != null) {
			if (!alreadyReported.contains(previousTest)) {
				logger.warn("No result reported for " + test);
			} else {
				logger.debug("Got result for " + test);
			}
		}
		logger.debug("Test started: " + test);
		long start = System.currentTimeMillis();
		startTime.put(test, start);
	}

	/**
	 * @return The error messages.
	 */
	public List<TestMessage> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * @return The failure messages.
	 */
	public List<TestMessage> getFailureMessages() {
		return failureMessages;
	}

	/**
	 * @return The passing messages.
	 */
	public List<TestMessage> getPassingMessages() {
		return passingMessages;
	}

	/**
	 * For test purposes.
	 *
	 * @param errorMessages
	 *            the errorMessages to set
	 */
	public void setErrorMessages(List<TestMessage> errorMessages) {
		this.errorMessages = errorMessages;
	}

	/**
	 * For test purposes.
	 *
	 * @param failureMessages
	 *            the failureMessages to set
	 */
	public void setFailureMessages(List<TestMessage> failureMessages) {
		this.failureMessages = failureMessages;
	}

	/**
	 * For test purposes.
	 *
	 * @param passingMessages
	 *            the passingMessages to set
	 */
	public void setPassingMessages(List<TestMessage> passingMessages) {
		this.passingMessages = passingMessages;
	}

}
