package org.softevo.mutation.testsuite;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.TestMessage;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class MutationTestListener implements TestListener {

	private List<TestMessage> errorMessages = new ArrayList<TestMessage>();

	private List<TestMessage> failureMessages = new ArrayList<TestMessage>();

	public void addError(Test test, Throwable t) {
		errorMessages.add(new TestMessage(test.toString(), t.toString()));
	}

	public void addFailure(Test test, AssertionFailedError t) {
		failureMessages.add(new TestMessage(test.toString(), t.toString()));

	}

	public void endTest(Test test) {

	}

	public void startTest(Test test) {

	}

	public List<TestMessage> getErrorMessages() {
		return errorMessages;
	}

	public List<TestMessage> getFailureMessages() {
		return failureMessages;
	}

}
