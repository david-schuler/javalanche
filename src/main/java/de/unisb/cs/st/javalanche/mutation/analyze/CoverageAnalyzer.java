package de.unisb.cs.st.javalanche.mutation.analyze;

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

	/* (non-Javadoc)
	 * @see de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	public String analyze(Iterable<Mutation> mutations) {
		StringBuilder sb = new StringBuilder();
		for (Mutation m : mutations) {
			Set<String> testsCollectedData = QueryManager
					.getTestsCollectedData(m);
			if (testsCollectedData == null || testsCollectedData.size() == 0) {
				sb.append("\nMutaiton not covered:  " + m);
			}
		}
		return sb.toString();
	}

}
