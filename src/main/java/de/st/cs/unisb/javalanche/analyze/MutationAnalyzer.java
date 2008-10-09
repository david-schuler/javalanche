package de.st.cs.unisb.javalanche.analyze;

import de.st.cs.unisb.javalanche.results.Mutation;

public interface MutationAnalyzer {


	public String analyze(Iterable<Mutation> mutations);
}
