package org.softevo.mutation.run.analyze;

import org.softevo.mutation.results.Mutation;

public interface MutatedAnalyzer {

	public void handleMutation(Mutation mutation);

	public String getResults();

}
