package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class MutationPrioritizationAnalyzer implements MutationAnalyzer {

	public static final File ADD_MUTATION_INV_FILE = new File("additional-invariant-mutaiton.csv");
	public static final File TOTAL_MUTATION_INV_FILE = new File("total-invariant-mutaiton.csv");
	public static final File ADD_MUTATION_FILE = new File("additional-mutaiton.csv");
	public static final File TOTAL_MUTATION_FILE = new File("total-mutaiton.csv");

	public String analyze(Iterable<Mutation> mutations) {
		SetMultimap<String, Long> detectTestMap = new HashMultimap<String, Long>();
		SetMultimap<String, Long> invariantdetectTestMap = new HashMultimap<String, Long>();
		StringBuilder sb = new StringBuilder();
		for (Mutation m : mutations) {
			MutationTestResult result = m.getMutationResult();
			if (result != null) {
				Collection<TestMessage> failures = result.getFailures();
				for (TestMessage testMessage : failures) {
					String testCaseName = testMessage.getTestCaseName();
					detectTestMap.put(testCaseName, m.getId());
					if (result.getTotalViolations() > 0) {
						invariantdetectTestMap.put(testCaseName, m.getId());
					}
				}
				Collection<TestMessage> errors = result.getErrors();
				for (TestMessage testMessage : errors) {
					String testCaseName = testMessage.getTestCaseName();
					detectTestMap.put(testCaseName, m.getId());
					if (result.getTotalViolations() > 0) {
						invariantdetectTestMap.put(testCaseName, m.getId());
					}
				}
			}
		}
		String total = getTotalPrioritization(detectTestMap);
		String additional = getAdditionalPriotization(detectTestMap);
		Io.writeFile(total, TOTAL_MUTATION_FILE);
		Io.writeFile(additional, ADD_MUTATION_FILE);
		String totalInvariant = getTotalPrioritization(detectTestMap);
		String additionalInvariant = getAdditionalPriotization(detectTestMap);
		Io.writeFile(totalInvariant, TOTAL_MUTATION_INV_FILE);
		Io.writeFile(additionalInvariant, ADD_MUTATION_INV_FILE);
		return sb.toString();
	}


	private static String getTotalPrioritization(
			final SetMultimap<String, Long> detectTestMap) {
		StringBuilder sb = new StringBuilder();
		List<String> testList = new ArrayList<String>(detectTestMap.keySet());
		Collections.sort(testList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int i1 = detectTestMap.get(o1).size();
				int i2 = detectTestMap.get(o2).size();
				return i1 - i2;
			}
		});
		for (String testName : testList) {
			sb.append(testName);
			sb.append(",");
			sb.append(detectTestMap.get(testName).size());
			sb.append('\n');
		}
		return sb.toString();
	}

	private static String getAdditionalPriotization(
			SetMultimap<String, Long> detectTestMap) {
		StringBuilder sb = new StringBuilder();
		List<String> testList = new ArrayList<String>(detectTestMap.keySet());
		Set<Long> usedIds = new HashSet<Long>();
		while (testList.size() > 0) {
			String next = getNext(testList, usedIds,detectTestMap);
			sb.append(next + "," + detectTestMap.get(next).size() + "\n");
			// detectTestMap.removeAll(next);
			usedIds.addAll(detectTestMap.get(next));
			testList.remove(next);
		}
		return sb.toString();
	}

	private static String getNext(List<String> testList, Set<Long> usedIds, SetMultimap<String, Long> detectTestMap) {
		int max = Integer.MIN_VALUE;
		String next = "";
		for (String testName : testList) {
			Set<Long> set = detectTestMap.get(testName);
			set.removeAll(usedIds);
			if (set.size() > max) {
				max = set.size();
				next = testName;
			}

		}
		return next;
	}

}
