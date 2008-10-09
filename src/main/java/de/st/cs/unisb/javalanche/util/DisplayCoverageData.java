package org.softevo.mutation.util;

import java.util.List;

import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationCoverage;
import org.softevo.mutation.results.TestName;
import org.softevo.mutation.results.persistence.QueryManager;

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
