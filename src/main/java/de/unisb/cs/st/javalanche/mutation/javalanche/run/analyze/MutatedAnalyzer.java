package de.unisb.cs.st.javalanche.mutation.run.analyze;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public interface MutatedAnalyzer {

	public void handleMutation(Mutation mutation);

	public String getResults();

}
