/*
* Copyright (C) 2011 Saarland University
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

import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;

//de.unisb.cs.st.javalanche.mutation.analyze.ThesisAnalyzer 
public class ThesisAnalyzer implements MutationAnalyzer {

	@Override
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		JavalancheConfiguration javalancheConfiguration = ConfigurationLocator
				.getJavalancheConfiguration();
		int numberOfMutations = 0;
		int covered = 0;
		int detected = 0;
		Set<MutationType> set = new HashSet<MutationType>();
		for (Mutation mutation : mutations) {
			numberOfMutations++;
			boolean isCovered = MutationCoverageFile
					.isCovered(mutation.getId());
			covered += isCovered ? 1 : 0;
			detected += mutation.isKilled() ? 1 : 0;
			MutationType type = mutation.getMutationType();
			set.add(type);
		}
		String coveredPercent = getPercent(covered, numberOfMutations);
		String detectedPercent = getPercent(detected, numberOfMutations);
		String detectedCoveredPercent = getPercent(detected, covered);
		String result = String.format("%s & %d & %d & %d",
				javalancheConfiguration.getProjectPrefix(), numberOfMutations,
				covered,
				detected);
		String result2 = String.format("%s & %d & %s & %s & %s",
				javalancheConfiguration.getProjectPrefix(), numberOfMutations,
				coveredPercent, detectedPercent, detectedCoveredPercent);
		System.out.println(result);
		System.out.println(result2);
		System.out.println(set);
		return result + "\n" + result2;

	}

	private String getPercent(int fraction, int total) {
		String formatPercent = AnalyzeUtil.formatPercent(fraction, total);
		String replace = formatPercent.replace('%', ' ');
		return replace;
	}

}
