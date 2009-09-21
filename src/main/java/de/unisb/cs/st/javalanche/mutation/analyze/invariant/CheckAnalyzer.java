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
package de.unisb.cs.st.javalanche.mutation.analyze.invariant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class CheckAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

		List<Mutation> detected = new ArrayList<Mutation>();
		Map<String, Integer> testDetectMap = new HashMap<String, Integer>();
		final Map<String, Integer> testExecutionMap = new HashMap<String, Integer>();

		for (Mutation m : mutations) {
			if (m.isKilled()) {
				detected.add(m);
			}
			MutationTestResult mutationResult = m.getMutationResult();
			if (mutationResult != null) {
				List<TestMessage> messages = new ArrayList<TestMessage>(
						mutationResult.getFailures());
				messages.addAll(mutationResult.getErrors());
				for (TestMessage testMessage : mutationResult.getErrors()) {
					String testName = testMessage.getTestCaseName();
					Integer value = Integer.valueOf(1);
					Integer executionValue = Integer.valueOf(1);
					if (testDetectMap.containsKey(testName)) {
						value += testDetectMap.get(testName);
					}
					if (testExecutionMap.containsKey(testName)) {
						executionValue += testExecutionMap.get(testName);
					}
					testDetectMap.put(testName, value);
					testExecutionMap.put(testName, executionValue);

				}
				for (TestMessage testMessage : mutationResult.getFailures()) {
					String testName = testMessage.getTestCaseName();
					Integer value = Integer.valueOf(1);
					Integer executionValue = Integer.valueOf(1);
					if (testDetectMap.containsKey(testName)) {
						value += testDetectMap.get(testName);
					}
					if (testExecutionMap.containsKey(testName)) {
						executionValue += testExecutionMap.get(testName);
					}
					testDetectMap.put(testName, value);
					testExecutionMap.put(testName, executionValue);

				}
				for (TestMessage testMessage : mutationResult.getPassing()) {
					String testName = testMessage.getTestCaseName();
					Integer executionValue = Integer.valueOf(1);
					if (testExecutionMap.containsKey(testName)) {
						executionValue += testExecutionMap.get(testName);
					}

					testExecutionMap.put(testName, executionValue);

				}

			}
		}

		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(
				testDetectMap.entrySet());
//		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
//
//			public int compare(Entry<String, Integer> o1,
//					Entry<String, Integer> o2) {
//
//				return o1.getValue() - o2.getValue();
//			}
//
//		});
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				double percent1 = percent(o1.getValue(), testExecutionMap
						.get(o1.getKey()));
				double percent2 = percent(o2.getValue(), testExecutionMap
						.get(o2.getKey()));
				double diff = percent1 - percent2;
				return diff == 0 ? 0 : diff > 0 ? 1 : -1;
			}

			private double percent(int f, int total) {
				return f * 1. / total;

			}

		});
		StringBuffer sb = new StringBuffer();

		for (Entry<String, Integer> entry : entries) {
			int executions = testExecutionMap.get(entry.getKey());
			int detections = entry.getValue();
			sb.append(detections + " / " + executions + "  "
					+ (detections * 1. / executions) + "  " + entry.getKey()
					+ " \n");
		}
		// Collections.reverse(entries);
		// if (entries.size() > 0) {
		// String testName = entries.get(0).getKey();
		// for (Mutation m : detected) {
		// MutationTestResult mutationResult = m.getMutationResult();
		// List<TestMessage> messages = new ArrayList<TestMessage>(
		// mutationResult.getFailures());
		// messages.addAll(mutationResult.getErrors());
		// for (TestMessage testMessage : messages) {
		// if (testMessage.getTestCaseName().equals(testName)) {
		// sb.append(testMessage);
		// }
		// }
		// }
		// }
		return sb.toString();
	}
}
