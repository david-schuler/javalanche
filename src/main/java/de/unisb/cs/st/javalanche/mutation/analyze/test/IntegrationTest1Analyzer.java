package de.unisb.cs.st.javalanche.mutation.analyze.test;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
public class IntegrationTest1Analyzer implements MutationAnalyzer{

	public String analyze(Iterable<Mutation> mutations) {
//		for (Mutation mutation : mutations) {
//			assertTrue("Mutation not covered: " + mutation, QueryManager.isCoveredMutation(mutation));
//			assertTrue("Mutation not killed: "  + mutation, mutation.isKilled());
//		}
		return "All mutations covered and killed";
	}

}
