package de.st.cs.unisb.javalanche.util;

import java.util.List;

import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationCoverage;
import de.st.cs.unisb.javalanche.results.TestName;
import de.st.cs.unisb.javalanche.results.persistence.QueryManager;

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
				.getMutationIdListFromDb(1000);
		System.out.println("got " + mutationListFromDb.size()
				+ " mutations from the db");
		for (Mutation mutation : mutationListFromDb) {
			System.out.println("Tests for mutation " + mutation);
			MutationCoverage mutationCoverageData = QueryManager
					.getMutationCoverageData(mutation.getId());
			if (mutationCoverageData != null) {
				List<TestName> testsNames = mutationCoverageData
						.getTestsNames();
				for (TestName testName : testsNames) {
					System.out.println("\t" + testName.getName());
				}
			} else {
				System.out.println("\tNo tests covering this mutation");
			}
		}
	}

}
