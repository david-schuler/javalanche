package de.st.cs.unisb.javalanche.run.analyze;

import de.st.cs.unisb.javalanche.results.Mutation;

public interface MutatedAnalyzer {

	public void handleMutation(Mutation mutation);

	public String getResults();

}
