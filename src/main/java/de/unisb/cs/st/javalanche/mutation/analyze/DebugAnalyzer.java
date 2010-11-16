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

import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class DebugAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		StringBuffer sb = new StringBuffer();
		for (Mutation m : mutations) {
			MutationTestResult mutationResult = m.getMutationResult();
			sb.append(m.toShortString()).append('\n');
			Set<String> testsCollectedData = MutationCoverageFile
					.getCoverageData(m);
			if (testsCollectedData == null || testsCollectedData.size() == 0) {
				sb.append("not covered\n");

			} else {
				sb.append("covered\n");
			}
			if (mutationResult != null) {
				sb.append("Violated invariants: "
						+ mutationResult.getDifferentViolatedInvariants()
						+ "\n");
			}
			sb.append("-------------------------\n");
		}
		System.out.println("DebugAnalyzer.analyze()");
		System.out.println(sb);
		return sb.toString();
	}
}
