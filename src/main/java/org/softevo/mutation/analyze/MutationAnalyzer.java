package org.softevo.mutation.analyze;

import org.softevo.mutation.results.Mutation;

public interface MutationAnalyzer {


	public String analyze(Iterable<Mutation> mutations);
}
