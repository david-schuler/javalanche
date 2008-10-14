package de.unisb.cs.st.javalanche.mutation.run.analyze;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public interface MutatedUnmutatedAnalyzer {

	public void handleMutation(Mutation mutated, Mutation unMutated);

	public String getResults();

}
