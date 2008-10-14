package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.Comparator;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

/**
 * 
 * Utility class that is used by several analyze classes.
 * 
 * @author David Schuler
 * 
 */
public class AnalyzeUtil {

	/**
	 * Formats a given double value to percent.
	 * 
	 * @param percent
	 * @return a formated percent string.
	 */
	static String formatPercent(double percent) {
		return String.format("%2.2f%%", percent * 100);
	}

	/**
	 * Formats a given the given values to percent string.
	 * 
	 * @param fraction
	 * @param total
	 * @return a formated percent string.
	 */
	public static String formatPercent(int fraction, int total) {
		return formatPercent((double) fraction / total);
	}

	/**
	 * Comparator that compares mutations regarding their total number of
	 * invariant violations
	 */
	public static final Comparator<Mutation> TOTAL_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getTotalViolations()
					- mutationResult2.getTotalViolations();
		}

	};

	/**
	 * Comparator that compares mutations regarding their total number of
	 * invariant violations
	 */
	public static final Comparator<Mutation> DIFFERENT_VIOLATIONS_COMPARATOR = new Comparator<Mutation>() {
		public int compare(Mutation o1, Mutation o2) {
			MutationTestResult mutationResult1 = o1.getMutationResult();
			MutationTestResult mutationResult2 = o2.getMutationResult();
			return mutationResult1.getDifferentViolatedInvariants()
					- mutationResult2.getDifferentViolatedInvariants();
		}

	};

}
