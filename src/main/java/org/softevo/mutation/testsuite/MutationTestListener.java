package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softevo.mutation.results.TestMessage;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class MutationTestListener implements TestListener {

	private static Logger logger = Logger.getLogger(MutationTestListener.class);

	private long start;

	private List<TestMessage> errorMessages = new ArrayList<TestMessage>();

	private List<TestMessage> failureMessages = new ArrayList<TestMessage>();

	private List<TestMessage> passingMessages = new ArrayList<TestMessage>();

	private List<Test> alreadyReported = new ArrayList<Test>();

	public void addError(Test test, Throwable t) {
		if (alreadyReported.contains(test)) {
			logger.warn("Result for this test was already reported " + test);
			return;
		}
		logger.info("Error added");
		long duration = getDuration();
		errorMessages.add(new TestMessage(test.toString(), t.toString(), duration));
		alreadyReported.add(test);
	}

	public void addFailure(Test test, AssertionFailedError t) {
		if (alreadyReported.contains(test)) {
			logger.warn("Result for this test was already reported " + test);
			return;
		}
		logger.info("Failure added");
		long duration = getDuration();
		failureMessages.add(new TestMessage(test.toString(), t.toString(), duration));
		alreadyReported.add(test);
	}

	public void endTest(Test test) {
		if (!alreadyReported.contains(test)) {
			long duration = getDuration();
// 			durations.put(test.toString(), duration);
			passingMessages.add(new TestMessage(test.toString(), "test passed",
					duration));
		}
		logger.info("Test ended:" + test);
	}

	private long getDuration() {
		long duration = System.currentTimeMillis() - start;
		return duration;
	}

	public void startTest(Test test) {
		logger.info("Test started: " + test);
		start = System.currentTimeMillis();
	}

	public List<TestMessage> getErrorMessages() {
		return errorMessages;
	}

	public List<TestMessage> getFailureMessages() {
		return failureMessages;
	}


	/**
	 * @return the passingMessages
	 */
	public List<TestMessage> getPassingMessages() {
		return passingMessages;
	}

}
