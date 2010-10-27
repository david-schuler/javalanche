/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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
