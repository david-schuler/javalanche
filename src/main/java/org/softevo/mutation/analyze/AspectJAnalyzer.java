package org.softevo.mutation.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;
import org.softevo.mutation.results.TestMessage;

public class AspectJAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations) {
		List<TestMessage> errorMessages = new ArrayList<TestMessage>();
		List<TestMessage> failureMessages = new ArrayList<TestMessage>();

		Map<String, Integer> failureTestCases = new HashMap<String, Integer>();
		// Map<String, Integer> errorTestCases = new HashMap<String, Integer>();
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				MutationTestResult mutationResult = m.getMutationResult();
				errorMessages.addAll(mutationResult.getErrors());
				failureMessages.addAll(mutationResult.getFailures());
				for (TestMessage tm : mutationResult.getFailures()) {
					String testCaseName = tm.getTestCaseName();
					failureTestCases.put(testCaseName, failureTestCases
							.containsKey(testCaseName) ? failureTestCases
							.get(testCaseName) + 1 : 1);
				}

				for (TestMessage tm : mutationResult.getErrors()) {
					String testCaseName = tm.getTestCaseName();
					failureTestCases.put(testCaseName, failureTestCases
							.containsKey(testCaseName) ? failureTestCases
							.get(testCaseName) + 1 : 1);
				}

			}

//			if (errorMessages.size() > 1000 && failureMessages.size() > 1000) {
//				break;
//			}

		}
		StringBuilder sb = new StringBuilder();
		sb.append("\nFailures:\n");
		for (TestMessage tm : failureMessages) {
			sb.append(tm.getMessage());
			sb.append('\n');
		}
		sb.append("\nErrors:\n");
		for (TestMessage tm : errorMessages) {
			sb.append(tm.getMessage());
			sb.append('\n');
		}
		// sb.append("Error Testcases: \n" + errorTestCases);
		Set<Entry<String, Integer>> entrySet = failureTestCases.entrySet();
		List<Entry<String, Integer>> sortedEntryList = new ArrayList<Entry<String, Integer>>(entrySet);
		Comparator<Entry<String, Integer>> comp = new Comparator<Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}

		};
		Collections.sort(sortedEntryList, comp);
		sb.append("Failure Testcases: \n");
		for (Entry<String, Integer> entry : sortedEntryList) {
			sb.append(entry);
			sb.append('\n');
		}
		return sb.toString();
	}
}
