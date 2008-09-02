package org.softevo.mutation.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;

public class AssertAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(AssertAnalyzer.class);
	private static final String FORMAT = "%-55s %10d\n";
	private static final String FORMAT_STRING = "%-55s %s\n";

	public String analyze(Iterable<Mutation> mutations) {
		List<Mutation> killedByError = new ArrayList<Mutation>();
		List<Mutation> killedByFailure = new ArrayList<Mutation>();
		List<Mutation> killedMutations = new ArrayList<Mutation>();
		List<Mutation> killedAndTouchedMutations = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByError = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByFailure = new ArrayList<Mutation>();
		List<Mutation> invariantViolatingMutations = new ArrayList<Mutation>();
		List<Mutation> nonInvariantViolatingMutations = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			MutationTestResult mutationResult = mutation.getMutationResult();
			if (mutation.isKilled()) {
				killedMutations.add(mutation);
				if (mutationResult != null && mutationResult.isTouched()) {
					killedAndTouchedMutations.add(mutation);
					if (mutationResult.getNumberOfErrors() > 0) {
						killedByError.add(mutation);
						if (mutationResult.getNumberOfFailures() == 0) {
							killedExclusivelyByError.add(mutation);
						}
					}
					if (mutationResult.getNumberOfFailures() > 0) {
						killedByFailure.add(mutation);
						if (mutationResult.getNumberOfErrors() == 0) {
							killedExclusivelyByFailure.add(mutation);
						}
					}
				}
			}
			if (mutationResult != null && mutationResult.isTouched()) {
				if (mutationResult.getTotalViolations() > 0) {
					invariantViolatingMutations.add(mutation);
				} else {
					nonInvariantViolatingMutations.add(mutation);
				}
			}
		}
		StringBuilder sb = new StringBuilder();

		sb.append("DEBUG: Mutations killed : " + killedMutations.size());

		sb.append("\n");
		int debugTouched = nonInvariantViolatingMutations.size()
				+ invariantViolatingMutations.size();
		sb.append("DEBUG: Touched: " + debugTouched);
		sb.append("\n");
		sb.append("DEBUG: Mutation score for covered : "
				+ AnalyzeUtil.formatPercent(killedMutations.size(),
						debugTouched));
		sb.append("\n");

		List<Mutation> debugList = new ArrayList<Mutation>(killedMutations);
		debugList.removeAll(nonInvariantViolatingMutations);
		debugList.removeAll(invariantViolatingMutations);
		sb.append("DEBUG: mutations killed and not touched (should be zero): "
				+ debugList.size());
		sb.append("\n");

		List<Mutation> killedInvariantViolatingMutations = new ArrayList<Mutation>(
				invariantViolatingMutations);
		killedInvariantViolatingMutations.retainAll(killedAndTouchedMutations);
		int killedInvariantViolating = killedInvariantViolatingMutations.size();
		sb.append(String.format(FORMAT_STRING,
				"Mutation score for invariant violating mutations:",
				AnalyzeUtil.formatPercent(killedInvariantViolating,
						invariantViolatingMutations.size())));

		appendAssertEvaluation(killedByError, killedExclusivelyByFailure,
				invariantViolatingMutations, sb);

		List<Mutation> killedNonInvariantViolatingMutations = new ArrayList<Mutation>(
				nonInvariantViolatingMutations);
		killedNonInvariantViolatingMutations
				.retainAll(killedAndTouchedMutations);
		int killedNonInvariantViolating = killedNonInvariantViolatingMutations
				.size();

		sb.append(String.format(FORMAT_STRING,
				"Mutation score for non invariant violating mutations:",
				AnalyzeUtil.formatPercent(killedNonInvariantViolating,
						nonInvariantViolatingMutations.size())));

		appendNonViolatingEvaluation(killedByError, killedExclusivelyByFailure,
				nonInvariantViolatingMutations, sb);
		// sb.append(AnalyzeUtil.formatPercent(nonInvariantAssert,
		// nonInvariantNonError));

		sb.append('\n');
		sb.append("Top Percentages for all invariant violating mutations\n");
		for (int i = 5; i <= 100; i += 5) {
			appendRankedValues(killedAndTouchedMutations,
					invariantViolatingMutations, sb, i);
		}

		sb.append('\n');
		sb
				.append("Comparison: Top Percentages for all non invariant violating mutations\n");
		for (int i = 5; i <= 100; i += 5) {
			appendRankedValues(killedAndTouchedMutations,
					nonInvariantViolatingMutations, sb, i);
		}

		sb.append('\n');
		sb
				.append("Top Percentages for invariant violating mutations that are not killed by errors\n");
		List<Mutation> invariantNonErrorMutations = getNonErrorMutations(
				killedByError, invariantViolatingMutations);
		for (int i = 5; i <= 100; i += 5) {
			appendRankedValues(killedAndTouchedMutations,
					invariantNonErrorMutations, sb, i);
		}

		sb.append('\n');
		sb
				.append("Comparison: Top Percentages for non invariant violating mutations that are not killed by errors\n");
		List<Mutation> nonInvariantNonErrorMutations = getNonErrorMutations(
				killedByError, nonInvariantViolatingMutations);
		for (int i = 5; i <= 100; i += 5) {
			appendRankedValues(killedAndTouchedMutations,
					nonInvariantNonErrorMutations, sb, i);
		}
		return sb.toString();
	}

	private void appendNonViolatingEvaluation(List<Mutation> killedByError,
			List<Mutation> killedExclusivelyByFailure,
			List<Mutation> nonInvariantViolatingMutations, StringBuilder sb) {
		List<Mutation> nonInvariantAssertMutations = new ArrayList<Mutation>();
		nonInvariantAssertMutations.addAll(nonInvariantViolatingMutations);
		nonInvariantAssertMutations.retainAll(killedExclusivelyByFailure);

		List<Mutation> nonInvariantNonErrorMutations = getNonErrorMutations(
				killedByError, nonInvariantViolatingMutations);

		int nonInvariantAssert = nonInvariantAssertMutations.size();
		int nonInvariantNonError = nonInvariantNonErrorMutations.size();
		sb.append(String.format(FORMAT,
				"Number of non invariant violating mutations:",
				nonInvariantViolatingMutations.size()));

		sb
				.append(String
						.format(
								FORMAT,
								"Number of non invariant violating mutations that are killed exclusively by assertions:",
								nonInvariantAssert));
		sb
				.append(String
						.format(
								FORMAT,
								"Number of non invariant violating mutations that are not killed by errors:",
								nonInvariantNonError));
	}

	private void appendAssertEvaluation(List<Mutation> killedByError,
			List<Mutation> killedExclusivelyByFailure,
			List<Mutation> invariantViolatingMutations, StringBuilder sb) {
		List<Mutation> invariantAssertMutations = new ArrayList<Mutation>();
		invariantAssertMutations.addAll(invariantViolatingMutations);
		invariantAssertMutations.retainAll(killedExclusivelyByFailure);

		List<Mutation> invariantNonErrorMutations = getNonErrorMutations(
				killedByError, invariantViolatingMutations);

		int invariantAssert = invariantAssertMutations.size();
		int invariantNonError = invariantNonErrorMutations.size();
		sb.append(String.format(FORMAT,
				"Number of invariant violating mutations:",
				invariantViolatingMutations.size()));

		sb
				.append(String
						.format(
								FORMAT,
								"Number of invariant violating mutations that are killed exclusively by assertions:",
								invariantAssert));

		sb
				.append(String
						.format(
								FORMAT,
								"Number of invariant violating mutations that are not killed by errors:",
								invariantNonError));
		// sb
		// .append(String
		// .format(
		// FORMAT_STRING,AnalyzeUtil.formatPercent(invariantAssert,
		// invariantNonError)));
		sb.append('\n');
	}

	private List<Mutation> getNonErrorMutations(List<Mutation> killedByError,
			List<Mutation> invariantViolatingMutations) {
		List<Mutation> invariantNonErrorMutations = new ArrayList<Mutation>();
		invariantNonErrorMutations.addAll(invariantViolatingMutations);
		invariantNonErrorMutations.removeAll(killedByError);
		return invariantNonErrorMutations;
	}

	private void appendRankedValues(List<Mutation> killedMutations,

	List<Mutation> invariantViolatingMutations, StringBuilder sb, int percent) {
		List<Mutation> sorted = new ArrayList<Mutation>(
				invariantViolatingMutations);
		Collections.sort(sorted, new Comparator<Mutation>() {

			public int compare(Mutation o1, Mutation o2) {
				MutationTestResult mutationResult1 = o1.getMutationResult();
				MutationTestResult mutationResult2 = o2.getMutationResult();
				return mutationResult1.getDifferentViolatedInvariants()
						- mutationResult2.getDifferentViolatedInvariants();
			}

		});
		Collections.reverse(sorted);
		if (sorted.size() == 0) {
			sb.append("Empty list given " + invariantViolatingMutations + "  "
					+ percent);
			return;
		}
		int percentListSize = (int) (((double) percent / 100.) * sorted.size());
		logger.debug("Size " + sorted.size() + " Percent size "
				+ percentListSize);
		List<Mutation> percentList = sorted.subList(0, percentListSize);
		logger.debug("List size" + sorted.size() + " percent: " + percent
				+ " Percent list size: " + percentList.size());
		logger.debug("Sorted first: "
				+ sorted.get(0).getMutationResult()
						.getDifferentViolatedInvariants()
				+ " Sorted last: "
				+ sorted.get(sorted.size() - 1).getMutationResult()
						.getDifferentViolatedInvariants());
		logger.debug("Percent first: "
				+ percentList.get(0).getMutationResult()
						.getDifferentViolatedInvariants()
				+ " Percent last: "
				+ percentList.get(percentList.size() - 1).getMutationResult()
						.getDifferentViolatedInvariants());

		List<Mutation> killedInvariantViolatingMutations = new ArrayList<Mutation>(
				percentList);
		killedInvariantViolatingMutations.retainAll(killedMutations);
		int killedInvariantViolating = killedInvariantViolatingMutations.size();
		sb
				.append(String
						.format(
								"Mutations that are killed out of top %d percent (%d mutations): %s",
								percent, percentList.size(), AnalyzeUtil
										.formatPercent(
												killedInvariantViolating,
												percentList.size())));

		// List<Mutation> invariantAssertMutations = new ArrayList<Mutation>();
		// invariantAssertMutations.addAll(invariantViolatingMutations);
		// invariantAssertMutations.retainAll(killedExclusivelyByFailure);
		//
		// List<Mutation> invariantNonErrorMutations = new
		// ArrayList<Mutation>();
		// invariantNonErrorMutations.addAll(invariantViolatingMutations);
		// invariantNonErrorMutations.removeAll(killedByError);
		//
		// int invariantAssert = invariantAssertMutations.size();
		// int invariantNonError = invariantNonErrorMutations.size();
		// sb.append(String.format(FORMAT,
		// "Number of invariant violating mutations:",
		// invariantViolatingMutations.size()));
		//
		// sb
		// .append(String
		// .format(
		// FORMAT,
		// "Number of invariant violating mutations that are killed exclusively
		// by assertions:",
		// invariantAssert));
		//
		// sb
		// .append(String
		// .format(
		// FORMAT,
		// "Number of invariant violating mutations that are not killed by
		// errors:",
		// invariantNonError));
		// sb
		// .append(AnalyzeUtil.formatPercent(invariantAssert,
		// invariantNonError));
		sb.append('\n');
	}
}
