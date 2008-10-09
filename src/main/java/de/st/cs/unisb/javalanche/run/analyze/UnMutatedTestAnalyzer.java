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

import de.st.cs.unisb.ds.util.io.Io;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;
import org.softevo.mutation.results.TestMessage;

public class UnMutatedTestAnalyzer implements MutatedUnmutatedAnalyzer {

	private static class TestCaseOutcome {

		String name;

		int passed;

		int failed;

		Map<String, List<Mutation>> failureMessages = new HashMap<String, List<Mutation>>();

		public TestCaseOutcome(String name) {
			super();
			this.name = name;
		}

		public void addPassed() {
			passed++;
		}

		public void addFailed(String name, Mutation m) {
			List<Mutation> mutations = null;
			if (failureMessages.containsKey(name)) {
				mutations = failureMessages.get(name);

			} else {
				mutations = new ArrayList<Mutation>();
				failureMessages.put(name, mutations);
			}
			mutations.add(m);
			failed++;
		}

		/**
		 * @return the failed
		 */
		public int getFailed() {
			return failed;
		}

		/**
		 * @return the passed
		 */
		public int getPassed() {
			return passed;
		}
	}

	Map<String, UnMutatedTestAnalyzer.TestCaseOutcome> testCaseMap = new HashMap<String, UnMutatedTestAnalyzer.TestCaseOutcome>();

	public String getResults() {
		int inconsistent = 0;
		int total = 0;
		for (UnMutatedTestAnalyzer.TestCaseOutcome outcome : testCaseMap
				.values()) {
			if (outcome.failed != 0 && outcome.passed != 0) {
				inconsistent++;
			}
			total++;
		}
		writeResultFile();
		return "Tests with inconsistent outcome: " + inconsistent + " out of "
				+ total;
	}

	public void writeResultFile() {
		StringBuffer sb = new StringBuffer();
		for (UnMutatedTestAnalyzer.TestCaseOutcome outcome : testCaseMap
				.values()) {
			if (outcome.getFailed() != 0 && outcome.getPassed() != 0) {
				sb.append(outcome.name);
				sb.append("Passed " +  outcome.getPassed());
				sb.append("Failed " +  outcome.getFailed());
				sb.append('\n');
				sb.append("Messages:\n");
				for (Entry<String, List<Mutation>> entry : outcome.failureMessages
						.entrySet()) {
					sb.append('\t' + entry.getValue().size() + "x "
							+ entry.getKey());
					for (Mutation m : entry.getValue()) {
						sb.append('\n');
						sb.append("ID: " + m.getId() + " Classname: "
								+ m.getClassName() + " Linenumber:"
								+ m.getLineNumber());
					}
					sb.append('\n');
				}
				sb
						.append("--------------------------------------------------\n");
			}
		}
		Io.writeFile(sb.toString(), new File("unMutatedAnalyze.txt"));
	}

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		MutationTestResult testResult = unMutated.getMutationResult();
		for (TestMessage tm : testResult.getPassing()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addPassed();
		}
		for (TestMessage tm : testResult.getFailures()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addFailed(tm.getMessage(), unMutated);
		}
		for (TestMessage tm : testResult.getErrors()) {
			UnMutatedTestAnalyzer.TestCaseOutcome outcome = getOutcome(tm);
			outcome.addFailed(tm.getMessage(), unMutated);
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