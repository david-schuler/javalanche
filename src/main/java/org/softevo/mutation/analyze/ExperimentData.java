/**
 *
 */
package org.softevo.mutation.analyze;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.softevo.mutation.io.XmlIo;

class ExperimentData {
	Set<Long> caughtIds = new HashSet<Long>();
	Set<Long> survivedTotalIds = new HashSet<Long>();
	Set<Long> survivedViolatedIds = new HashSet<Long>();
	Set<Long> survivedNonViolatedCoveredIds = new HashSet<Long>();

	public ExperimentData(Set<Long> caughtIds, Set<Long> survivedTotalIds,
			Set<Long> survivedViolatedIds,
			Set<Long> survivedNonViolatedCoveredIds) {
		super();
		this.caughtIds = caughtIds;
		this.survivedTotalIds = survivedTotalIds;
		this.survivedViolatedIds = survivedViolatedIds;
		this.survivedNonViolatedCoveredIds = survivedNonViolatedCoveredIds;
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
		sb
				.append("Mutation ids survived mutations that violated invariants: "
						+ survivedViolatedIds);

		sb.append('\n');
		return sb.toString();
	}

	public String compareToFile(File experimentDataFile) {
		ExperimentData fromXml = (ExperimentData) XmlIo
				.fromXml(experimentDataFile);
		return compareToData(fromXml);

	}

	private String compareToData(ExperimentData other) {
		return compare(this, other);
	}
	static String compare(ExperimentData fullRun,
			ExperimentData other) {
		Set<Long> survivedViolatedIntersection = new HashSet<Long>();
		survivedViolatedIntersection.addAll(fullRun.caughtIds);
		survivedViolatedIntersection.retainAll(other.survivedViolatedIds);
		StringBuilder sb = new StringBuilder();
		sb
				.append(String
						.format(
								"%d out of %d mutation that violated invariants were caught.  %s\n",
								survivedViolatedIntersection.size(),
								other.survivedViolatedIds.size(),
								AnalyzeUtil
										.formatPercent(
												survivedViolatedIntersection
														.size(),
												other.survivedViolatedIds
														.size())));

		Set<Long> survivedNonViolatedCoveredIntersection = new HashSet<Long>();
		survivedNonViolatedCoveredIntersection.addAll(fullRun.caughtIds);
		survivedNonViolatedCoveredIntersection
				.retainAll(other.survivedNonViolatedCoveredIds);
		sb
				.append(String
						.format(
								"%d out of %d mutation that did not violat invariants and are covered were caught. %s\n",
								survivedNonViolatedCoveredIntersection
										.size(),
								other.survivedNonViolatedCoveredIds
										.size(),
								AnalyzeUtil
										.formatPercent(
												survivedNonViolatedCoveredIntersection
														.size(),
												other.survivedNonViolatedCoveredIds
														.size())));
		return sb.toString();
	}


	private static String compare(String fullFilename, String experimentFilename) {
		ExperimentData full = (ExperimentData) XmlIo.fromXml(fullFilename);
		ExperimentData exp = (ExperimentData) XmlIo.fromXml(experimentFilename);
		return compare(full, exp);
	}

	public static void main(String[] args) {
		if(args.length <2){
			System.out.println("Usage: <fullData> <experimentData>");
		}
		else{
			System.out.println("Comparing - Full data:" + args[0] + "  Experiment Data: " + args[1]);
			System.out.println(compare(args[0], args[1]));
		}
	}


}