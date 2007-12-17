package org.softevo.mutation.run.analyze;

import org.softevo.mutation.results.Mutation;

public interface MutatedUnmutatedAnalyzer {

	public void handleMutation(Mutation mutated, Mutation unMutated);

	public String getResults();

}
