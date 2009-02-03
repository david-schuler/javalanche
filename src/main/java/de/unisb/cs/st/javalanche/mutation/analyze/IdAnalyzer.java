package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.Collection;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class IdAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations) {
		StringBuilder sb = new StringBuilder();
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				sb.append("Killed mutation: " + m.getId() + " \n");
				MutationTestResult mutationResult = m.getMutationResult();
				Collection<TestMessage> errors = mutationResult.getErrors();
				sb.append("Errors:\n");
				for (TestMessage e : errors) {
					sb.append("\t" + e.getTestCaseName() + "\n");
				}
				sb.append("Failures:\n");
				Collection<TestMessage> failures = mutationResult.getFailures();
				for (TestMessage e : failures) {
					sb.append("\t" + e.getTestCaseName() + "\n");
				}
			}
		}
		return sb.toString();
	}

}
