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

	private int mutations;

	private int touched;

	private int numberOfAppliedMutations;

	private Set<Mutation> touchedMutations;

	private Set<Mutation> reportedMutations;

	private List<Mutation> appliedMutations;

	private List<Long> touchedIds = new ArrayList<Long>();

	private List<Long> reportedIds = new ArrayList<Long>();

	private List<Long> appliedIds = new ArrayList<Long>();


	/**
	 * @return the mutations the number of for which results are stored.
	 */
	public int getMutations() {
		return mutations;
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
		this.mutations = reportedMutations.size();
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
				numberOfAppliedMutations, mutations, touched);
	}
}
