/**
 *
 */
package org.softevo.mutation.run.analyze;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;

public class KilledAnalyzer implements MutatedUnmutatedAnalyzer {

	private List<Mutation> failures = new ArrayList<Mutation>();

	private int passing = 0;

	private int killed;

	private int notKilled;

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		SingleTestResult mutatedResult = mutated.getMutationResult();
		SingleTestResult unMutatedResult = unMutated.getMutationResult();

		int unMutatedFailures = unMutatedResult.getNumberOfFailures();
		int unMutatedErrors = unMutatedResult.getNumberOfErrors();
		int unMutatedRuns = unMutatedResult.getRuns();

		int mutatedFailures = mutatedResult.getNumberOfFailures();
		int mutatedErrors = mutatedResult.getNumberOfErrors();
		int mutatedRuns = mutatedResult.getRuns();

		if (unMutatedRuns == mutatedRuns
				&& mutatedErrors + mutatedFailures > unMutatedErrors
						+ unMutatedFailures) {
			killed++;
		} else {
			notKilled++;
		}

	}

	public String getResults() {
		return String.format("Mutations killed %d\n"
				+ "Mutations not killed: %d\n" + "Total: %d", killed,
				notKilled, killed + notKilled);
	}

}