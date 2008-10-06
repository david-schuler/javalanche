/**
 *
 */
package org.softevo.mutation.analyze;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;

public class ExperimentData2 implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ExperimentData2.class);

	Set<Long> caughtIds = new HashSet<Long>();
	Set<Long> survivedTotalIds = new HashSet<Long>();
	Set<Long> survivedViolatedIds = new HashSet<Long>();
	Set<Long> survivedNonViolatedCoveredIds = new HashSet<Long>();
	private final Map<Long, Mutation> survivedViolatedMap;

	public ExperimentData2(Set<Long> caughtIds, Set<Long> survivedTotalIds,
			Set<Long> survivedViolatedIds,
			Set<Long> survivedNonViolatedCoveredIds,
			Map<Long, Mutation> survivedViolatedMap) {
		super();
		this.caughtIds = caughtIds;
		this.survivedTotalIds = survivedTotalIds;
		this.survivedViolatedIds = survivedViolatedIds;
		this.survivedNonViolatedCoveredIds = survivedNonViolatedCoveredIds;
		this.survivedViolatedMap = survivedViolatedMap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Killed mutations" + caughtIds);
		sb.append('\n');
		sb.append("Mutation ids of survived mutations" + survivedTotalIds);
		sb.append('\n');
		sb
				.append("Mutation ids of covered and survived mutations that violated no invariants: "
						+ survivedNonViolatedCoveredIds);
		sb.append('\n');
		sb.append("Mutation ids survived mutations that violated invariants: "
				+ survivedViolatedIds);

		sb.append('\n');
		return sb.toString();
	}

	public String compareToFile(File experimentDataFile) {
		ExperimentData2 fromXml = (ExperimentData2) XmlIo
				.fromXml(experimentDataFile);
		return compareToData(fromXml);
	}

	private String compareToData(ExperimentData2 other) {
		return compare(this, other);

	}

	static String compare(ExperimentData2 fullRun, ExperimentData2 other) {
		return compare(fullRun.toExperimentData(), other);
	}

	private ExperimentData toExperimentData() {
		return new ExperimentData(caughtIds, survivedTotalIds,
				survivedViolatedIds, survivedNonViolatedCoveredIds);
	}

	static String compare(ExperimentData fullRun, ExperimentData2 other) {
		Set<Long> survivedViolatedIntersection = new HashSet<Long>();

		survivedViolatedIntersection.addAll(fullRun.caughtIds);
		survivedViolatedIntersection.retainAll(other.survivedViolatedIds);

		Set<Long> fullRunCaughtIds = fullRun.caughtIds;
		List<Mutation> violatingIds = new ArrayList<Mutation>(
				other.survivedViolatedMap.values());
		StringBuilder sb = new StringBuilder();
		sb.append("Ranking for different violations\n");
		for (int i = 5; i <= 100; i += 5) {
			String percentKilled = getPercentKilled(fullRunCaughtIds,
					violatingIds, i,
					AnalyzeUtil.DIFFERENT_VIOLATIONS_COMPARATOR);
			sb.append(percentKilled);
			sb.append('\n');
		}
		sb.append("Sorted distribution of different violations:\n");

		String distribution = getDistribution(other.survivedViolatedMap
				.values());
		sb.append(distribution);
		// sb.append("Ranking for total violations\n");
		// for (int i = 5; i <= 100; i += 5) {
		// String percentKilled = getPercentKilled(fullRunCaughtIds,
		// violatingIds, i, AnalyzeUtil.TOTAL_VIOLATIONS_COMPARATOR);
		// sb.append(percentKilled);
		// sb.append('\n');
		// }

		sb.append('\n');
		Set<Long> survivedNonViolatedCoveredIntersection = new HashSet<Long>();
		survivedNonViolatedCoveredIntersection.addAll(fullRun.caughtIds);
		survivedNonViolatedCoveredIntersection
				.retainAll(other.survivedNonViolatedCoveredIds);
		sb
				.append(String
						.format(
								"%d out of %d mutation that did not violate invariants and are covered were caught. %s\n",
								survivedNonViolatedCoveredIntersection.size(),
								other.survivedNonViolatedCoveredIds.size(),
								AnalyzeUtil.formatPercent(
										survivedNonViolatedCoveredIntersection
												.size(),
										other.survivedNonViolatedCoveredIds
												.size())));
		return sb.toString();
	}

	private static String getDistribution(Collection<Mutation> values) {
		List<Mutation> sorted = new ArrayList<Mutation>(values);
		Collections.sort(sorted, AnalyzeUtil.DIFFERENT_VIOLATIONS_COMPARATOR);
		List<Integer> violated = new ArrayList<Integer>();
		for (Mutation m : sorted) {
			violated
					.add(m.getMutationResult().getDifferentViolatedInvariants());
		}
		return violated.toString();
	}

	private static String getPercentKilled(Set<Long> fullRunCaughtIds,
			List<Mutation> invariantViolatingMutations, int percent,
			Comparator<Mutation> comparator) {
		StringBuilder sb = new StringBuilder();

		List<Mutation> sorted = new ArrayList<Mutation>(
				invariantViolatingMutations);
		Collections.sort(sorted, comparator);
		Collections.reverse(sorted);
		if (sorted.size() == 0) {
			String message = "Empty list given " + invariantViolatingMutations
					+ "  " + percent;
			sb.append(message);
			return message;
		}
		int percentListSize = (int) (((double) percent / 100.) * sorted.size());
		logger.debug("Size " + sorted.size() + " Percent size "
				+ percentListSize);
		List<Mutation> percentList = sorted.subList(0, percentListSize);
		Mutation first = percentList.get(0);
		Mutation last = percentList.get(percentListSize - 1);
		logger.debug("First: "
				+ first.getMutationResult().getDifferentViolatedInvariants()
				+ "  / " + first.getMutationResult().getTotalViolations() + " "
				+ first.getMutationResult().getViolatedInvariants().length);
		logger.debug("Last: "
				+ last.getMutationResult().getDifferentViolatedInvariants()
				+ "  / " + last.getMutationResult().getTotalViolations() + " "
				+ last.getMutationResult().getViolatedInvariants().length);

		int killedInvariantViolating = 0;
		for (Mutation m : percentList) {
			if (fullRunCaughtIds.contains(m.getId())) {
				killedInvariantViolating++;
			}
		}
		String percentString = AnalyzeUtil.formatPercent(
				killedInvariantViolating, percentList.size());
		String message = String
				.format(
						"Mutations that are killed out of top %d percent (%d mutations): %s (%d out of %d)",
						percent, percentList.size(), percentString,
						killedInvariantViolating, percentList.size());
		sb.append(message);

		return sb.toString();
	}
}