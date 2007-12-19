/**
 *
 */
package org.softevo.mutation.run.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.softevo.mutation.io.Io;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.TestMessage;

public class UnMutatedTestAnalyzer implements
		MutatedUnmutatedAnalyzer {

	private static class TestCaseOutcome {

		String name;

		int passed;

		int failed;

		Map<String, Integer> failureMessages = new HashMap<String, Integer>();

		public TestCaseOutcome(String name) {
			super();
			this.name = name;
		}

		public void addPassed() {
			passed++;
		}

		public void addFailed(String string) {
			if(failureMessages.containsKey(string)){
				failureMessages.put(string, failureMessages.get(string) + 1);
			}else{
				failureMessages.put(string, 1);
			}
			failed++;
		}
	}

	Map<String, UnMutatedTestAnalyzer.TestCaseOutcome> testCaseMap = new HashMap<String, UnMutatedTestAnalyzer.TestCaseOutcome>();

	public String getResults() {
		int inconsistent = 0;
		int total = 0;
		for (UnMutatedTestAnalyzer.TestCaseOutcome outcome : testCaseMap.values()) {
			if (outcome.failed != 0 && outcome.passed != 0) {
				inconsistent++;
			}
			total++;
		}
		writeResultFile();
		return "Tests with inconsistent outcome: " + inconsistent
				+ " out of " + total;
	}

	public void writeResultFile() {
		StringBuffer sb = new StringBuffer();
		for (UnMutatedTestAnalyzer.TestCaseOutcome outcome : testCaseMap.values()) {
			if (outcome.failed != 0 && outcome.passed != 0) {
				sb.append(outcome.name);
				sb.append('\n');
				sb.append("Messages:\n");
				for (Entry<String, Integer> entry : outcome.failureMessages.entrySet()) {
					sb.append('\t' + entry.getValue() + "x " + entry.getKey() );
					sb.append('\n');
				}
				sb
						.append("--------------------------------------------------\n");
			}
		}
		Io.writeFile(sb.toString(), new File("unMutatedAnalyze.txt"));
	}

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		SingleTestResult testResult = unMutated.getMutationResult();
		for (TestMessage tm : testResult.getPassing()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addPassed();
		}
		for (TestMessage tm : testResult.getFailures()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addFailed(tm.getMessage());
		}
		for (TestMessage tm : testResult.getErrors()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addFailed(tm.getMessage());
		}

	}

	private UnMutatedTestAnalyzer.TestCaseOutcome getOutcome(TestMessage tm) {
		UnMutatedTestAnalyzer.TestCaseOutcome outcome = null;
		String testCaseName = tm.getTestCaseName();
		if (testCaseMap.containsKey(testCaseName)) {
			outcome = testCaseMap.get(testCaseName);
		} else {
			outcome = new TestCaseOutcome(testCaseName);
			testCaseMap.put(testCaseName, outcome);
		}
		return outcome;
	}

}