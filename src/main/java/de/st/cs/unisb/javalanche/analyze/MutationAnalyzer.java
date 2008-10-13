package de.st.cs.unisb.javalanche.analyze;

import de.st.cs.unisb.javalanche.results.Mutation;

/**
 * Classes that implement this interface analyze the results of the mutation
 * testing.
 *
 * @see AnalyzeMain
 *
 * @author David Schuler
 */
public interface MutationAnalyzer {

	/**
	 * Returns a string that a summarizes the results of the mutation testing.
	 *
	 * @param mutations
	 *            Iterator over all mutations of a project.
	 *
	 * @return a String that summarizes the results.
	 */
	public String analyze(Iterable<Mutation> mutations);
}
