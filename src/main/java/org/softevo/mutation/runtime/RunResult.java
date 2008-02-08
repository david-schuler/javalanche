package org.softevo.mutation.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;

/**
 * Class that holds the result for one run of the program. Needed when run in
 * several JVMs.
 *
 * @author David Schuler
 *
 */
public class RunResult {

	private int reported;

	private int touched;

	private int numberOfAppliedMutations;

	private int numberOfUnMutatedMutations;

	@SuppressWarnings("unused")
	private Set<Mutation> touchedMutations;

	@SuppressWarnings("unused")
	private Set<Mutation> reportedMutations;

	@SuppressWarnings("unused")
	private List<Mutation> appliedMutations;

	private List<Long> touchedIds = new ArrayList<Long>();

	private List<Long> reportedIds = new ArrayList<Long>();

	private List<Long> appliedIds = new ArrayList<Long>();

	/**
	 * @return the mutations the number of for which results are stored.
	 */
	public int getMutations() {
		return reported;
	}

	/**
	 * @return the number touched mutation
	 */
	public int getTouched() {
		return touched;
	}

	public RunResult(Set<Mutation> reportedMutations,
			Set<Mutation> touchedMutations, List<Mutation> appliedMutations,
			Set<Mutation> unMutatedMutations) {
		super();
		this.reported = reportedMutations.size();
		this.reportedMutations = reportedMutations;
		this.touched = touchedMutations.size();
		this.touchedMutations = touchedMutations;
		this.numberOfAppliedMutations = appliedMutations.size();
		this.appliedMutations = appliedMutations;
		this.numberOfUnMutatedMutations = unMutatedMutations.size();
		touched = 0;
		reported = 0;
		numberOfAppliedMutations = 0;
		for (Mutation m : touchedMutations) {
			if (!m.getMutationType().equals(MutationType.NO_MUTATION)) {
				touched++;
			}
			touchedIds.add(m.getId());
		}
		for (Mutation m : reportedMutations) {
			if (!m.getMutationType().equals(MutationType.NO_MUTATION)) {
				reported++;
			}
			reportedIds.add(m.getId());
		}

		for (Mutation m : appliedMutations) {
			if (!m.getMutationType().equals(MutationType.NO_MUTATION)) {
				numberOfAppliedMutations++;
			}
			appliedIds.add(m.getId());
		}

	}

	@Override
	public String toString() {
		String s = String.format("%d mutations were applied (Checks %d)"
				+ "\n%d Mutation Results were recorded"
				+ "\n%d Mutations where actually touched.",
				numberOfAppliedMutations, numberOfUnMutatedMutations, reported,
				touched);
//		s += '\n';
//		s += touched + "  " + touchedMutations.size() + '\n';
//		s += reported + "  " + reportedMutations.size() + '\n';
//		s += numberOfAppliedMutations + "  " + appliedMutations.size() + '\n';
		return s;
	}

	/**
	 * @return the reportedMutations
	 */
	public Set<Mutation> getReportedMutations() {
		return reportedMutations;
	}

	/**
	 * @return the reportedIds
	 */
	public List<Long> getReportedIds() {
		return reportedIds;
	}
}
