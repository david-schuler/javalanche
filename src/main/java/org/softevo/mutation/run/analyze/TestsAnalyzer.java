/**
 * 
 */
package org.softevo.mutation.run.analyze;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;

public class TestsAnalyzer implements MutatedUnmutatedAnalyzer {

	private List<Mutation> failures = new ArrayList<Mutation>();

	private int passing = 0;

	private int unMutatedHasMoreFailures;

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		SingleTestResult mutatedResult = mutated.getMutationResult();
		SingleTestResult unMutatedResult = unMutated.getMutationResult();

		if (mutatedResult != null && unMutatedResult != null) {
			if (mutatedResult.getRuns() != unMutatedResult.getRuns()) {
				failures.add(mutated);
				failures.add(unMutated);
			} else {
				passing++;
			}
		}

		int unMutatedFailures = unMutatedResult.getNumberOfFailures()
				+ unMutatedResult.getNumberOfErrors();
		int mutatedFailures = mutatedResult.getNumberOfFailures()
				+ mutatedResult.getNumberOfErrors();
		if (unMutatedFailures > mutatedFailures) {
			unMutatedHasMoreFailures++;
		}

	}

	public String getResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("Pairs with unequal number of runs: "
				+ (int) (failures.size() / 2.));
		sb.append('\n');
		sb.append("Pairs with equal number of runs:" + passing);
		sb.append('\n');
		sb.append("Mutations with less errors than unmutated: "
				+ unMutatedHasMoreFailures);
		return sb.toString();
	}
}