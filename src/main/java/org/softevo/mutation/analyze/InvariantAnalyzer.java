package org.softevo.mutation.analyze;

import java.util.ArrayList;
import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;

public class InvariantAnalyzer implements MutationAnalyzer {

	List<Mutation> notCaught = new ArrayList<Mutation>();

	public String analyze(Iterable<Mutation> mutations) {
		int violated = 0;
		int violatedNotCaught = 0;
		int total = 0;
		for (Mutation mutation : mutations) {
			SingleTestResult mutationResult = mutation.getMutationResult();
			if (mutationResult != null &&   mutationResult.getDifferentViolatedInvariants() > 1) {
				violated++;
				if (!mutation.isKilled()) {
					violatedNotCaught++;
					notCaught.add(mutation);
				}
			}
			total++;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Total Mutations: " + total);
		sb.append('\n');
		sb.append("Mutations that violated invariants: " + violated);
		sb.append('\n');
		sb.append("Mutations that violated invariants and were not caught: " + violatedNotCaught);
		sb.append('\n');
		sb.append("List of mutations that violated invariants and were not caught:\n" );
		for (Mutation mutation2 : notCaught) {
			sb.append(mutation2.toShortString());
		}
		return sb.toString();
	}

}
