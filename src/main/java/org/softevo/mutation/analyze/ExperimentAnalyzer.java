package org.softevo.mutation.analyze;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;

public class ExperimentAnalyzer implements MutationAnalyzer {

	private static final String EXPERIMENT_DATA_FILE = "experimentData.xml";

	private static class ExperimentData {
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
			Set<Long> survivedViolatedIntersection = new HashSet<Long>();
			survivedViolatedIntersection.addAll(caughtIds);
			survivedViolatedIntersection.retainAll(other.survivedViolatedIds);
			StringBuilder sb = new StringBuilder();
			sb
					.append(String
							.format(
									"%d out of %d mutation that violated invariants were caught\n",
									survivedViolatedIntersection.size(),
									survivedViolatedIds.size()));

			Set<Long> survivedNonViolatedCoveredIntersection = new HashSet<Long>();
			survivedNonViolatedCoveredIntersection.addAll(caughtIds);
			survivedNonViolatedCoveredIntersection
					.retainAll(other.survivedNonViolatedCoveredIds);
			sb
					.append(String
							.format(
									"%d out of %d mutation that did not violat invariants and are covered were caught\n",
									survivedNonViolatedCoveredIntersection
											.size(),
									survivedNonViolatedCoveredIds.size()));
			return sb.toString();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.softevo.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	public String analyze(Iterable<Mutation> mutations) {
		Set<Long> caughtIds = new HashSet<Long>();
		Set<Long> survivedTotalIds = new HashSet<Long>();
		Set<Long> survivedViolatedIds = new HashSet<Long>();
		Set<Long> survivedNonViolatedCoveredIds = new HashSet<Long>();
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				caughtIds.add(m.getId());
			} else {
				survivedTotalIds.add(m.getId());
			}

			MutationTestResult mt = m.getMutationResult();
			if (mt != null && !m.isKilled()) {
				if (mt.getTotalViolations() > 0) {
					survivedViolatedIds.add(m.getId());
				} else {
					if (mt.isTouched()) {
						survivedNonViolatedCoveredIds.add(m.getId());
					}
				}
			}
		}
		ExperimentData experimentData = new ExperimentData(caughtIds,
				survivedTotalIds, survivedViolatedIds,
				survivedNonViolatedCoveredIds);
		File experimentDataFile = new File(EXPERIMENT_DATA_FILE);
		if (experimentDataFile.exists()) {
			return experimentData.compareToFile(experimentDataFile);
		} else {
			XmlIo.toXML(experimentData, EXPERIMENT_DATA_FILE);
		}
		return experimentData.toString();
	}
}
