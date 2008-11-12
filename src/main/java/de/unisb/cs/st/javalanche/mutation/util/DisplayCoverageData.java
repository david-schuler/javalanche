package de.unisb.cs.st.javalanche.mutation.util;

import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverage;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Displays the coverage data for a number of mutation. The Coverage information
 * for a mutation are the tests that execute the mutation
 *
 * @author David Schuler
 *
 */
public class DisplayCoverageData {

	public static void main(String[] args) {
		List<Mutation> mutationListFromDb = QueryManager
				.getMutationIdListFromDb(Integer.MAX_VALUE);
		System.out.println("got " + mutationListFromDb.size()
				+ " mutations from the db");
		for (Mutation mutation : mutationListFromDb) {
			MutationCoverage mutationCoverageData = QueryManager
					.getMutationCoverageData(mutation.getId());
			if (mutationCoverageData != null
					&& mutationCoverageData.getTestsNames().size() > 0) {
				System.out.printf("%03d tests for mutation %d\n",
						mutationCoverageData.getTestsNames().size(), mutation
								.getId());
				// List<TestName> testsNames = mutationCoverageData
				// .getTestsNames();
				// for (TestName testName : testsNames) {
				// System.out.println("\t" + testName.getName());
				// }
			} else {
//				System.out.println("\tNo tests covering this mutation"
//						+ mutation.getId());
			}
		}
	}

}
