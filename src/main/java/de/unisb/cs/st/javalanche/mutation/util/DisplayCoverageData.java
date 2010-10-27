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
package de.unisb.cs.st.javalanche.mutation.util;

import java.util.List;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Displays the coverage data for a number of mutation. The Coverage information
 * for a mutation are the tests that execute the mutation
 * 
 * @author David Schuler
 * 
 */
public class DisplayCoverageData {

	public static void main(String[] args) {
		List<Mutation> mutationListFromDb = QueryManager
				.getMutationIdListFromDb(Integer.MAX_VALUE);
		System.out.println("got " + mutationListFromDb.size()
				+ " mutations from the db");
		for (Mutation mutation : mutationListFromDb) {
			Set<String> coverageData = MutationCoverageFile
					.getCoverageDataId(mutation.getId());
			if (coverageData != null && coverageData.size() > 0) {
				System.out.printf("%03d tests for mutation %d\n", coverageData
						.size(), mutation.getId());
			} else {
				System.out.println("\tNo tests covering this mutation"
						+ mutation.getId());
			}
		}
	}

}
