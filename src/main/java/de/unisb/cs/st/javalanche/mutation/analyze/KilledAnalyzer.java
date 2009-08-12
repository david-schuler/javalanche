package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class KilledAnalyzer implements MutationAnalyzer {

	private static final String FORMAT = "%-55s %10d\n";

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		List<Mutation> killedByError = new ArrayList<Mutation>();
		List<Mutation> killedByFailure = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByError = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByFailure = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				if (mutationResult.getNumberOfErrors() > 0) {
					killedByError.add(mutation);
					if (mutationResult.getNumberOfFailures() == 0) {
						killedExclusivelyByError.add(mutation);
					}
				}
				if (mutationResult.getNumberOfFailures() > 0) {
					killedByFailure.add(mutation);
					if (mutationResult.getNumberOfErrors() == 0) {
						killedExclusivelyByFailure.add(mutation);
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(FORMAT,
				"Number of mutations killed by errors:", killedByError.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed exclusively by errors:",
				killedExclusivelyByError.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed by failures:", killedByFailure
						.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed exclusively by failures:",
				killedExclusivelyByFailure.size()));
		return sb.toString();
	}

	
}
