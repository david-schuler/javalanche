package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * CoverageAnalyzer prints out all mutations that are not covered by tests.
 *
 * @author David Schuler
 *
 */
public class CoverageAnalyzer implements MutationAnalyzer {

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	private Random r = new Random(1816052929);

	public String analyze(Iterable<Mutation> mutations) {
		StringBuilder sb = new StringBuilder();
		List<Mutation> coveredMutations = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			Set<String> testsCollectedData = QueryManager
					.getTestsCollectedData(m);
			if (testsCollectedData == null || testsCollectedData.size() == 0) {
				//sb.append("\nMutation not covered:  " + m);
			} else {
				coveredMutations.add(m);
			}
		}
		for (int i = 0; i < 20; i++) {
			Mutation randomMutation = coveredMutations.remove(r
					.nextInt(coveredMutations.size()));
			sb.append(i+1 + "  " + randomMutation.toShortString() + " \n");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new Random().nextInt());
	}
}
