package de.st.cs.unisb.javalanche.run.analyze;

import de.st.cs.unisb.javalanche.results.Mutation;

public interface MutatedUnmutatedAnalyzer {

	public void handleMutation(Mutation mutated, Mutation unMutated);

	public String getResults();

}
