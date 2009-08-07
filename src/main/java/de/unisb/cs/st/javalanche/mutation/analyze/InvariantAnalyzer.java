package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class InvariantAnalyzer implements MutationAnalyzer {

	protected static String formatPercent(double d) {
		return null;
	}

	public String analyze(Iterable<Mutation> mutations) {
		int total = 0;
		int withResult = 0;
		int violated = 0;
		int violatedNotCaught = 0;
		int killed = 0;
		List<Mutation> violatedNotCaughtList = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				killed++;
			}
			MutationTestResult mutationResult = mutation.getMutationResult();
			if (mutationResult != null
					&& mutationResult.getDifferentViolatedInvariants() > 0) {
				violated++;
				if (!mutation.isKilled()) {
					violatedNotCaught++;
					violatedNotCaughtList.add(mutation);
				}
			}
			if (mutationResult != null) {
				withResult++;

			}
			total++;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Total Mutations: " + total);
		sb.append('\n');
		sb.append("Killed mutations: " + killed + "\n");
		sb
				.append(String
						.format(
								"Mutations that violated invariants: %d (%s relative to all mutations / %s relative to mutations that where covered)",
								violated, AnalyzeUtil.formatPercent(violated,
										total), AnalyzeUtil.formatPercent(
										violated, withResult)));
		sb.append('\n');
		sb
				.append(String
						.format(
								"Mutations that violated invariants and were not detected: %d (%s relative to all mutations  / %s relative to mutations that where covered)",
								violatedNotCaught, AnalyzeUtil.formatPercent(
										violatedNotCaught, total), AnalyzeUtil
										.formatPercent(violatedNotCaught,
												withResult)));
		sb.append('\n');
		int violatedCaught = violated - violatedNotCaught;
		sb
				.append(String
						.format(
								"Mutations that violated invariants and were detected:  %d (%s relative to all mutations  / %s relative to mutations that where covered)",
								violatedCaught, AnalyzeUtil.formatPercent(
										violatedCaught, total), AnalyzeUtil
										.formatPercent(violatedCaught,
												withResult)));
		if (true) {
			sb
					.append("\n\nList of mutations that violated invariants and were not caught:\n\n");

			Collections.sort(violatedNotCaughtList, new Comparator<Mutation>() {

				public int compare(Mutation o1, Mutation o2) {
					int i1 = o1.getMutationResult()
							.getDifferentViolatedInvariants();
					int i2 = o2.getMutationResult()
							.getDifferentViolatedInvariants();
					return i1 - i2;
				}

			});
			for (Mutation mutation2 : violatedNotCaughtList) {
				String add = "";
				if (mutation2.getMutationForLine() != 0) {
					add = " (" + mutation2.getMutationForLine() + ")";
				}
				sb.append(String.format("Class: %s Line: %d%s  Type: %s",
						mutation2.getClassName(), mutation2.getLineNumber(),
						add, mutation2.getMutationType().toString()));
				sb.append('\n');
				MutationTestResult mr = mutation2.getMutationResult();
				sb.append(String.format("Violated invariants: %d (Ids: %s)", mr
						.getDifferentViolatedInvariants(), Arrays.toString(mr
						.getViolatedInvariants())));
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
