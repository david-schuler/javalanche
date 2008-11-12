package de.unisb.cs.st.javalanche.mutation.runtime.testDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class SingleTestResult {

	public enum TestOutcome {
		PASS, FAIL, ERROR
	};

	public TestOutcome result;

	public TestMessage testMessage;

	private boolean touched;

	public SingleTestResult(String testCaseName, String message, boolean pass,
			long duration) {
		super();
		this.result = pass ? TestOutcome.PASS : TestOutcome.FAIL;
		this.testMessage = new TestMessage(testCaseName, message, duration);
	}

	public static MutationTestResult toMutationTestResult(
			Collection<SingleTestResult> testScriptResults) {
		MutationTestResult result = new MutationTestResult();
		List<TestMessage> passing = new ArrayList<TestMessage>();
		List<TestMessage> failing = new ArrayList<TestMessage>();
		boolean touched = false;
		for (SingleTestResult tsr : testScriptResults) {
			if (tsr.touched) {
				touched = tsr.touched;
			}
			if (tsr.result == TestOutcome.PASS) {
				passing.add(tsr.testMessage);
			} else {
				failing.add(tsr.testMessage);
			}
		}
		result.setPassing(passing);
		result.setFailures(failing);
		result.setDate(new Date());
		result.setRuns(testScriptResults.size());
		result.setTouched(touched);
		return result;
	}

	public void setTouched(boolean b) {
		this.touched = b;
	}

	public long getDuration() {
		return testMessage.getDuration();
	}

	public boolean hasPassed() {
		return result == TestOutcome.PASS;
	}
}
