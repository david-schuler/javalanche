/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	private Collection<Mutation> touchedMutations;

	@SuppressWarnings("unused")
	private Collection<Mutation> reportedMutations;

	@SuppressWarnings("unused")
	private Collection<Mutation> appliedMutations;

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

	public RunResult(Collection<Mutation> reportedMutations,
			Collection<Mutation> touchedMutations, Collection<Mutation> appliedMutations,
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
				+ "%d Mutation results were recorded. "
				+ "%d Mutations where actually touched.",
				numberOfAppliedMutations, reported,
				touched);
		return s;
	}

	/**
	 * @return the reportedMutations
	 */
	public Collection<Mutation> getReportedMutations() {
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
