package de.unisb.cs.st.javalanche.mutation.analyze;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

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
	public String analyze(Iterable<Mutation> mutations, HtmlReport report);
	
	
}
