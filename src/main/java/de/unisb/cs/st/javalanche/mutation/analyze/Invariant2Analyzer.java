package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.unisb.cs.st.javalanche.invariants.invariants.ClassInvariants;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class Invariant2Analyzer implements MutationAnalyzer {

	private final class InvariantComparator implements Comparator<Mutation> {
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getDifferentViolatedInvariants()
					- mutationResult2.getDifferentViolatedInvariants();
		}
	}

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		List<Mutation> all = new ArrayList<Mutation>();
		List<Mutation> survivedList = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			if (m.getMutationResult() != null) {
				all.add(m);
				if (!m.isKilled() && m.getMutationResult().isTouched()) {
					survivedList.add(m);
				}
			}
		}
		Collections.sort(all, new InvariantComparator());
		Collections.sort(survivedList, new InvariantComparator());
		Collections.reverse(all);
		Collections.reverse(survivedList);
		StringBuilder sb = new StringBuilder();
		ClassInvariants classInvariants = InvariantProperties
				.getClassInvariants();
		sb.append("All mutations\n");
		String stringRepresentation = getStringRepresentation(all,
				classInvariants, 5);
		sb.append(stringRepresentation);
		sb.append("Survived mutations\n");
		stringRepresentation = getStringRepresentation(survivedList,
				classInvariants, 5);
		sb.append(stringRepresentation);
		return sb.toString();
	}

	private String getStringRepresentation(List<Mutation> all,
			ClassInvariants classInvariants, int limit) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < limit; i++) {
			Mutation mutation = all.get(i);
			MutationTestResult mutationResult = mutation.getMutationResult();
			sb.append("Violated Invariants "
					+ mutationResult.getDifferentViolatedInvariants());
			sb.append("  " + mutation.toShortString() + " \n");
			int[] violatedInvariants = mutationResult.getViolatedInvariants();
			for (int j = 0; j < violatedInvariants.length; j++) {
				InvariantChecker invariantChecker = classInvariants
						.getInvariantChecker(violatedInvariants[j]);
				sb.append("\t" + invariantChecker + "\n");
			}
		}
		return sb.toString();
	}
}
