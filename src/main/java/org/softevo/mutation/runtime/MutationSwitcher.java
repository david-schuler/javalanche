package org.softevo.mutation.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

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
	 * The mutations that are activated in this run
	 */
	private Collection<Mutation> mutations;

	private Iterator<Mutation> iter;

	/**
	 * Holds the currently activated mutation.
	 */
	private Mutation actualMutation;



	private void initMutations() {
		if (mutations == null) {
			mutations = MutationForRun.getInstance().getMutations();
			logger.info(mutations);
			iter = mutations.iterator();
		} else {
			throw new RuntimeException("Already initialized");
		}
	}

	/**
	 * @return The test cases that cover the actual activated mutation.
	 */
	public Set<String> getTests() {
		String[] testCases = QueryManager.getTestCases(actualMutation);
		if (testCases == null) {
			return null;
		}
		return new HashSet<String>(Arrays.asList(testCases));
	}

	/**
	 * Checks if there is a mutation to apply.
	 * @return True, if next() will return a mutation.
	 */
	public boolean hasNext() {
		if (iter == null) {
			initMutations();
		}
		return iter.hasNext();
	}

	/**
	 * Takes the next mutation without a result and sets it as the actual mutation.
	 *
	 * @return The mutation that is now the actual mutation.
	 */
	public Mutation next() {
		if (iter == null) {
			initMutations();
		}
		while (iter.hasNext()) {
			actualMutation = iter.next();
			if (actualMutation.getMutationResult() == null) {
				return actualMutation;
			} else {
				logger.info("Mutation already got Results");
			}
		}
		return actualMutation;
	}

	/**
	 * Turns the actual mutation on.
	 */
	public void switchOn() {
		if (actualMutation != null) {
			logger.info("enabling mutation: "
					+ actualMutation.getMutationVariable() + " in line "
					+ actualMutation.getLineNumber() + " - "
					+ actualMutation.toString());
			System.setProperty(actualMutation.getMutationVariable(), "1");
			ResultReporter.setActualMutation(actualMutation);
		}
	}

	/**
	 * Turns the actual mutation off.
	 */
	public void switchOff() {
		if (actualMutation != null) {
			System.clearProperty(actualMutation.getMutationVariable());
			logger.info("disabling mutation: "
					+ actualMutation.getMutationVariable());
			ResultReporter.setActualMutation(null);
		}
	}
}
