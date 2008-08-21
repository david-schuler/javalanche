package org.softevo.mutation.run.analyze;

import org.softevo.mutation.results.Mutation;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;

public class MutatedKilledAnalyzer implements MutatedAnalyzer {

	private List<String> killedMutations = new ArrayList<String>();

	private List<String> notKilledMutations = new ArrayList<String>();

	private int killed;

	private int notKilled;

	public void handleMutation(Mutation mutation) {
		MutationTestResult mutatedResult = mutation.getMutationResult();

		int mutatedFailures = mutatedResult.getNumberOfFailures();
		int mutatedErrors = mutatedResult.getNumberOfErrors();

		if (mutatedErrors + mutatedFailures > 0) {
			killed++;
			killedMutations.add(mutation.toString());
		} else {
			notKilledMutations.add(mutation.toString());
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
