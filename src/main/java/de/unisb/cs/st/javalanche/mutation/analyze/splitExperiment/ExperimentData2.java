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
/**
 *
 */
package de.unisb.cs.st.javalanche.mutation.analyze.splitExperiment;

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
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.AnalyzeUtil;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class ExperimentData2 implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ExperimentData2.class);

	Set<Long> detectedIds = new HashSet<Long>();
	Set<Long> notDetectedIds = new HashSet<Long>();
	Set<Long> notDetectedViolatedIds = new HashSet<Long>();
	Set<Long> notDetectedNotViolatedIds = new HashSet<Long>();
	private final Map<Long, Mutation> notDetectedViolatedMap;

	public ExperimentData2(Set<Long> detectedIds, Set<Long> notDetectedIds,
			Set<Long> notDetectedViolatedIds,
			Set<Long> survivedNonViolatedCoveredIds,
			Map<Long, Mutation> survivedViolatedMap) {
		super();
		this.detectedIds = detectedIds;
		this.notDetectedIds = notDetectedIds;
		this.notDetectedViolatedIds = notDetectedViolatedIds;
		this.notDetectedNotViolatedIds = survivedNonViolatedCoveredIds;
		this.notDetectedViolatedMap = survivedViolatedMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Killed mutations: " + detectedIds);
		sb.append('\n');
		sb.append("Mutation ids of survived mutations" + notDetectedIds);
		sb.append('\n');
		sb
				.append("Mutation ids of covered and survived mutations that violated no invariants: "
						+ notDetectedNotViolatedIds);
		sb.append('\n');
		sb.append("Mutation ids survived mutations that violated invariants: "
				+ notDetectedViolatedIds);
		sb.append('\n');
		return sb.toString();
	}

	public String compareToFile(File experimentDataFile) {
		ExperimentData2 fromXml = XmlIo.get(experimentDataFile);
		return compareToData(fromXml);
	}

	private String compareToData(ExperimentData2 other) {
		return compare(this, other);

	}

	static String compare(ExperimentData2 fullRun, ExperimentData2 other) {

		Set<Long> notDetectedViolatedIntersection = new HashSet<Long>(fullRun.detectedIds);
		notDetectedViolatedIntersection.retainAll(other.notDetectedViolatedIds);

		Set<Long> fullRunDetectedIds = fullRun.detectedIds;
		List<Mutation> violatingIds = new ArrayList<Mutation>(
				other.notDetectedViolatedMap.values());
		StringBuilder sb = new StringBuilder();
		sb.append("Ranking for different violations\n");

		for (int i = 5; i <= 100; i += 5) {
			String percentKilled = getPercentKilled(fullRunDetectedIds,
					violatingIds, i,
					AnalyzeUtil.DIFFERENT_VIOLATIONS_COMPARATOR);
			sb.append(percentKilled);
			sb.append('\n');
		}

		sb.append("Sorted distribution of different violations:\n");

		String distribution = getDistribution(other.notDetectedViolatedMap
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
		Set<Long> notDetectedNotViolatedIntersection = new HashSet<Long>(fullRun.detectedIds);
		notDetectedNotViolatedIntersection
				.retainAll(other.notDetectedNotViolatedIds);
		sb
				.append(String
						.format(
								"%d out of %d covered mutation that did not violate invariants were caught. %s\n",
								notDetectedNotViolatedIntersection.size(),
								other.notDetectedNotViolatedIds.size(),
								AnalyzeUtil.formatPercent(
										notDetectedNotViolatedIntersection
												.size(),
										other.notDetectedNotViolatedIds.size())));
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
