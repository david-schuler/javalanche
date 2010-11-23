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
package de.unisb.cs.st.javalanche.mutation.mutationPossibilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationPossibilityCollector {

	Logger logger = Logger.getLogger(MutationPossibilityCollector.class);

	private Set<Mutation> possibilities = new HashSet<Mutation>();

	public void addPossibility(Mutation mutationPossibility) {
		if (possibilities.contains(mutationPossibility)) {
			// throw new RuntimeException("Possibility already contained");
			logger.warn("Possibility already contained" + mutationPossibility);
		}
		possibilities.add(mutationPossibility);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Mutation mutation : possibilities) {
			sb.append(mutation);
			sb.append('\n');
		}
		return sb.toString();
	}

	public void toDB() {
		QueryManager.saveMutations(possibilities);
	}

	/**
	 * Returns the number of collected mutation possibilities.
	 * 
	 * @return The number of mutation possibilities that were collected.
	 */
	public int size() {
		return possibilities.size();
	}

	public void updateDB() {
		int mutations = possibilities.size();
		logger.debug("Collected " + mutations + " mutation possibilities.");
		if (mutations > 0) {
			logger.debug("Trying to save mutations.");
			QueryManager.saveMutations(possibilities);
			logger.info(mutations + " mutations saved");
		}
	}

	public void clear() {
		possibilities.clear();
	}

	public List<Mutation> getPossibilities() {
		return Collections.unmodifiableList(new ArrayList<Mutation>(
				possibilities));
	}
}
