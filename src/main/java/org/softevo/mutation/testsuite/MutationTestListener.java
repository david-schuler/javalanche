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

	private List<Test> nonPassing = new ArrayList<Test>();

	private Map<String, Long> durations = new HashMap<String, Long>();

	public void addError(Test test, Throwable t) {
		logger.info("Error added");
		errorMessages.add(new TestMessage(test.toString(), t.toString()));
		nonPassing.add(test);
	}

	public void addFailure(Test test, AssertionFailedError t) {
		logger.info("Failure added");
		failureMessages.add(new TestMessage(test.toString(), t.toString()));
		nonPassing.add(test);
	}

	public void endTest(Test test) {
		long duration = System.currentTimeMillis() - start;
		durations.put(test.toString(), duration);
		if (!nonPassing.contains(test)) {
			passingMessages.add(new TestMessage(test.toString(), "test passed",
					duration));
		}
		logger.info("Test ended");
	}

	public void startTest(Test test) {
		logger.info("Test started");
		start = System.currentTimeMillis();
	}

	public List<TestMessage> getErrorMessages() {
		return errorMessages;
	}

	public List<TestMessage> getFailureMessages() {
		return failureMessages;
	}

	/**
	 * @return the durations
	 */
	public Map<String, Long> getDurations() {
		return durations;
	}

	/**
	 * @return the passingMessages
	 */
	public List<TestMessage> getPassingMessages() {
		return passingMessages;
	}

}
