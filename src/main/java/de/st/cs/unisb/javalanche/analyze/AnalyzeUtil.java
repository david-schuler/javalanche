package de.st.cs.unisb.javalanche.analyze;

import java.util.Comparator;

import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationTestResult;

public class AnalyzeUtil {

	static String formatPercent(double d) {
		return String.format("%2.2f%%", d * 100);
	}

	public static String formatPercent(int fraction, int total) {
		return formatPercent((double) fraction / total);
	}

	public static final Comparator<Mutation> TOTAL_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
	
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getTotalViolations()
					- mutationResult2.getTotalViolations();
		}
	
	};
	public static final Comparator<Mutation> DIFFERENT_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
	
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getDifferentViolatedInvariants()
					- mutationResult2.getDifferentViolatedInvariants();
		}
	
	};

}
