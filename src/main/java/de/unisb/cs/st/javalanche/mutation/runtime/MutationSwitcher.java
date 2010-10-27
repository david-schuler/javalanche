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

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationsForRun;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Class handles the activation and deactivation of the mutations during
 * runtime.
 * 
 * @author David Schuler
 * 
 */
public class MutationSwitcher {

	private static Logger logger = Logger.getLogger(MutationSwitcher.class);

	/**
	 * The mutations that get activated in this run.
	 */
	private Collection<Mutation> mutations;

	private Iterator<Mutation> iter;

	/**
	 * Holds the currently activated mutation.
	 */
	private Mutation currentMutation;

	private StopWatch stopWatch = new StopWatch();

	public MutationSwitcher() {
		mutations = MutationsForRun.getFromDefaultLocation().getMutations();
		logger.info(mutations);
		iter = mutations.iterator();
	}

	/**
	 * Checks if there is a mutation to apply.
	 * 
	 * @return True, if next() will return a mutation.
	 */
	public boolean hasNext() {
		return iter.hasNext();
	}

	/**
	 * Takes the next mutation without a result and sets it as the current
	 * mutation.
	 * 
	 * @return The mutation that is now the current mutation.
	 */
	public Mutation next() {
		while (iter.hasNext()) {
			currentMutation = iter.next();
			if (currentMutation.getMutationResult() == null) {
				return currentMutation;
			} else {
				logger.info("Mutation already got Results");
			}
		}
		return currentMutation;
	}

	/**
	 * Turns the current mutation on.
	 */
	public void switchOn() {
		if (currentMutation != null) {
			logger.info("enabling mutation: "
					+ currentMutation.getMutationVariable() + " in line "
					+ currentMutation.getLineNumber() + " - "
					+ currentMutation.toString());
			stopWatch.reset();
			stopWatch.start();
			System.setProperty(currentMutation.getMutationVariable(), "1");
			System.setProperty(MutationProperties.CURRENT_MUTATION_KEY,
					currentMutation.getId() + "");

		}
	}

	/**
	 * Turns the current mutation off.
	 */
	public void switchOff() {
		if (currentMutation != null) {
			System.clearProperty(currentMutation.getMutationVariable());
			System.clearProperty(MutationProperties.CURRENT_MUTATION_KEY);
			stopWatch.stop();
			logger.info("Disabling mutation: "
					+ currentMutation.getMutationVariable()
					+ " Time active "
					+ DurationFormatUtils
							.formatDurationHMS(stopWatch.getTime()));
			currentMutation = null;
		}
	}

	
}
