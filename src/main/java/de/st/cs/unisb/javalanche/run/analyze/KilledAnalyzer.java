/**
 *
 */
package de.st.cs.unisb.javalanche.run.analyze;

import java.util.ArrayList;
import java.util.List;

import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationTestResult;

public class KilledAnalyzer implements MutatedUnmutatedAnalyzer {

	private List<String> killedMutations = new ArrayList<String>();

	private List<String> notKilledMutations = new ArrayList<String>();

	private int killed;

	private int notKilled;

	public void handleMutation(Mutation mutated, Mutation unMutated) {
		MutationTestResult mutatedResult = mutated.getMutationResult();
		MutationTestResult unMutatedResult = unMutated.getMutationResult();

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
			killedMutations.add(mutated.toString());
		} else {
			notKilledMutations.add(mutated.toString());
			notKilled++;
		}

	}

	public String getResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("Killed Mutations:");
		for (String killed : killedMutations) {
			sb.append(killed.toString());
		}
		sb.append("\n\nNot Killed Mutations:");
		for (String killed : notKilledMutations) {
			sb.append("\t" + killed.toString());
		}
		sb.append('\n');
		sb.append('\n');
		sb.append(String.format("Mutations killed: %d\n"
				+ "Mutations not killed: %d\n" + "Total: %d", killedMutations
				.size(), notKilledMutations.size(), killedMutations.size()
				+ notKilledMutations.size()));
		return sb.toString();
	}
}