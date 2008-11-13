package de.unisb.cs.st.javalanche.mutation.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

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
	 * Indicates if this run finished normally or with an exception.
	 */
	private final boolean finishedNormal;

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
			 boolean finishedNormal) {
		super();
		this.finishedNormal = finishedNormal;
		this.reported = reportedMutations.size();
		this.reportedMutations = reportedMutations;
		this.touched = touchedMutations.size();
		this.touchedMutations = touchedMutations;
		this.numberOfAppliedMutations = appliedMutations.size();
		this.appliedMutations = appliedMutations;
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
		String s = String.format("%d mutations were applied. "
				+ "%d Mutation results were recorded."
				+ "%d Mutations where actually touched.",
				numberOfAppliedMutations, reported,
				touched);
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

	/**
	 * @return the finishedNormal
	 */
	public boolean isFinishedNormal() {
		return finishedNormal;
	}
}
