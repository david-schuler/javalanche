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
package de.unisb.cs.st.javalanche.mutation.results.persistence;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationObserver;

/**
 * Decides if a mutation should be applied in bytecode when the class is loaded.
 * 
 * @author David Schuler
 * 
 */
public class MutationManager {

	/**
	 * If set to true all mutations from the database are applied, otherwise
	 * only the mutations given by the {@link MutationsForRun}
	 */
	private boolean applyAllMutation = false;

	private static Logger logger = Logger.getLogger(MutationManager.class);

	private MutationsForRun mutationsForRun;

	public MutationManager() {
		this(false);

	}

	public MutationManager(boolean applyAllMutationsInDb) {
		applyAllMutation = applyAllMutationsInDb;
		mutationsForRun = MutationsForRun.getFromDefaultLocation();
	}

	public boolean shouldApplyMutation(Mutation mutation) {
		boolean result = false;
		if (mutation == null) {
			logger.warn("Null Mutation");
			return false;
		}
		Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
		if (applyAllMutation) {
			result = true;
		} else if (mutationsForRun.containsMutation(mutation)) {
			if (mutationFromDb == null) {
				logger.warn("Mutation not in db: " + mutation);
				return false;
			}
			if (mutationFromDb.getMutationResult() == null) {
				result = true;
			}
		}
		if (result) {
			logger.debug("Applying mutation: " + mutationFromDb);
			MutationObserver.mutationApplied(mutationFromDb);
		}
		return result;
	}

	public void reportAppliedMutations(Mutation... mutations) {
		for (Mutation mutation : mutations) {
			Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDb != null) {
				MutationObserver.mutationApplied(mutationFromDb);
			}
		}
	}

}
