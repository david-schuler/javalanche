package org.softevo.mutation.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.softevo.mutation.results.Mutation;

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
			Set<Mutation> touchedMutations, List<Mutation> appliedMutations) {
		super();
		this.reported = reportedMutations.size();
		this.reportedMutations = reportedMutations;
		this.touched = touchedMutations.size();
		this.touchedMutations = touchedMutations;
		this.numberOfAppliedMutations = appliedMutations.size();
		this.appliedMutations = appliedMutations;
		for (Mutation m : touchedMutations) {
			touchedIds.add(m.getId());
		}
		for (Mutation m : reportedMutations) {
			reportedIds.add(m.getId());
		}

		for (Mutation m : appliedMutations) {
			appliedIds.add(m.getId());
		}

	}

	@Override
	public String toString() {
		return String.format("%d mutations were applied"
				+ "\n%d Mutation Results were recorded"
				+ "\n%d Mutations where actually touched.",
				numberOfAppliedMutations, reported, touched);
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
