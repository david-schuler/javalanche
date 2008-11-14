package de.unisb.cs.st.javalanche.mutation.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.invariants.runtime.InvariantObserver;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 *
 * Class that manages the results of the mutation testing and persits them to
 * the database.
 *
 * @author David Schuler
 *
 */
public class ResultReporter {

	private static Logger logger = Logger.getLogger(ResultReporter.class);

	private static List<Mutation> reportedMutations = new ArrayList<Mutation>();

	public static synchronized void report(Mutation mutation) {
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
		if (MutationProperties.RUN_MODE == MutationProperties.RunMode.MUTATION_TEST_INVARIANT) {
			reportInvariantResults(mutation.getMutationResult());
		}
	}

	/**
	 * Add invariant results tie the given {@link MutationTestResult}.
	 *
	 * @param mutationTestResult
	 *            the result to add the invariant results
	 */
	private static void reportInvariantResults(
			MutationTestResult mutationTestResult) {
		InvariantObserver instance = InvariantObserver.getInstance();
		if (instance != null) {
			int totalViolatedInvariants = instance
					.getTotalInvariantViolations();
			int[] violatedInvariants = instance.getViolatedInvariantsArray();
			mutationTestResult.setTotalViolations(totalViolatedInvariants);
			mutationTestResult.setViolatedInvariants(violatedInvariants);
			mutationTestResult
					.setDifferentViolatedInvariants(violatedInvariants.length);
			InvariantObserver.reset();
		}
	}

	/**
	 * Persits the reported mutations to the database.
	 */
	public synchronized static void persist() {
		logger.debug("Start storing " + reportedMutations.size()
				+ " mutation test results in db");
		QueryManager.updateMutations(reportedMutations);
		logger.debug("Stored " + reportedMutations.size()
				+ " mutation test results in db");
		reportedMutations.clear();
	}

	/**
	 * Checks if a given mutation was already reported.
	 *
	 * @param m
	 *            the mutation to check
	 * @return true, if given mutation is already reported
	 */
	public synchronized boolean isReported(Mutation m) {
		return reportedMutations.contains(m);
	}

}
