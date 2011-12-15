/*
 * Copyright (C) 2011 Saarland University
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
import java.util.List;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * 
 * Class that manages the results of the mutation testing and persists them to
 * the database.
 * 
 * 
 * @author David Schuler
 * 
 */
public class ResultReporter implements MutationTestListener {

	private static Logger logger = Logger.getLogger(ResultReporter.class);

	/**
	 * All mutations results have been reported for.
	 */
	private List<Mutation> reportedMutations = new ArrayList<Mutation>();

	/**
	 * Counts the number of mutations that are reported to the db.
	 */
	private int reportCount;

	private int saveInterval = ConfigurationLocator
			.getJavalancheConfiguration().getSaveInterval();

	/**
	 * Reports the results of the given mutation.
	 * 
	 * @param mutation
	 *            The mutation the result is reported for.
	 */
	private synchronized void report(Mutation mutation) {
		if (mutation == null) {
			throw new IllegalArgumentException(
					"Argument was null: " + mutation == null ? ", mutation"
							: "");
		}
		if (mutation.getMutationResult() == null) {
			throw new IllegalArgumentException(
					"Given mutation does not contain a mutation test result "
							+ mutation);
		}

		if (!reportedMutations.contains(mutation)) {
			reportedMutations.add(mutation);
		} else {
			String message = "Mutation " + mutation + " already reported ";
			logger.info(message);
			throw new RuntimeException(message);
		}
		// if (ConfigurationLocator.getJavalancheConfiguration().getRunMode() ==
		// RunMode.MUTATION_TEST_INVARIANT) {
		// reportInvariantResults(mutation.getMutationResult());
		// }
	}

	/**
	 * Add invariant results tie the given {@link MutationTestResult}.
	 * 
	 * @param mutationTestResult
	 *            the result to add the invariant results
	 */
	// private static void reportInvariantResults(
	// MutationTestResult mutationTestResult) {
	// InvariantObserver instance = InvariantObserver.getInstance();
	// if (instance != null) {
	// int totalViolatedInvariants = instance
	// .getTotalInvariantViolations();
	// int[] violatedInvariants = instance.getViolatedInvariantsArray();
	// mutationTestResult.setTotalViolations(totalViolatedInvariants);
	// mutationTestResult.setViolatedInvariants(violatedInvariants);
	// mutationTestResult
	// .setDifferentViolatedInvariants(violatedInvariants.length);
	// InvariantObserver.reset();
	// }
	// }

	/**
	 * Persists the reported mutations to the database.
	 */
	public synchronized void persist() {
		logger.debug("Start storing " + reportedMutations.size()
				+ " mutation test results in db");
		QueryManager.updateMutations(reportedMutations);
		logger.debug("Stored " + reportedMutations.size()
				+ " mutation test results in db");
		reportedMutations.clear();
	}

	public void end() {
		persist();
	}

	/**
	 * Report the result of the mutation and store them to the db in regular
	 * intervals.
	 */
	public void mutationEnd(Mutation mutation) {
		if (!reportedMutations.contains(mutation)) {
			report(mutation);
			reportCount++;
			if (reportCount % saveInterval == 0) {
				logger.info("Reached save intervall. Saving " + saveInterval
						+ " mutations. Total mutations tested until now: "
						+ reportCount);
				persist();
			}
		}
	}

	/**
	 * Not used
	 */
	public void mutationStart(Mutation mutation) {
	}

	/**
	 * Not used
	 */
	public void start() {
	}

	/**
	 * Not used
	 */
	public void testEnd(String testName) {
	}

	/**
	 * Not used
	 */
	public void testStart(String testName) {
	}

}
