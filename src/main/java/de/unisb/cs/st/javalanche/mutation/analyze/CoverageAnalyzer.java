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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * CoverageAnalyzer prints out all mutations that are not covered by tests.
 * 
 * @author David Schuler
 * 
 */
public class CoverageAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(CoverageAnalyzer.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer#analyze(java
	 * .lang.Iterable)
	 */
	private Random r = new Random(1816052929);

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		StringBuilder sb = new StringBuilder();
		List<Mutation> coveredNotKilledMutations = new ArrayList<Mutation>();
		List<Mutation> coveredMutations = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			Set<String> testsCollectedData = MutationCoverageFile
					.getCoverageData(m);
			if (testsCollectedData == null || testsCollectedData.size() == 0) {
				// sb.append("\nMutation not covered: " + m);
			} else {
				coveredMutations.add(m);
				if (!m.isKilled()) {
					coveredNotKilledMutations.add(m);
				}
			}
		}
		logger.info("Covered mutations: " + coveredMutations.size());
		logger.info("Covered not killed: " + coveredNotKilledMutations.size());
		List<String> usedClasses = new ArrayList<String>();
		for (int i = 0; i < 20;) {
			Mutation randomMutation = coveredNotKilledMutations.remove(r
					.nextInt(coveredNotKilledMutations.size()));
			if (!usedClasses.contains(randomMutation.getClassName())) {
				usedClasses.add(randomMutation.getClassName());
				i++;
				sb.append(i + 1 + " " + randomMutation.toShortString());

				sb.append("\t Tests: "
						+ MutationCoverageFile.getCoverageDataId(randomMutation
								.getId()) + " \n");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new Random().nextInt());
	}
}
