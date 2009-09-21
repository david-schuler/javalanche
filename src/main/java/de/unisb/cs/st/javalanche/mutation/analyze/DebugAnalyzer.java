/*
* Copyright (C) 2009 Saarland University
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.unisb.cs.st.javalanche.invariants.invariants.ClassInvariants;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class DebugAnalyzer implements MutationAnalyzer {

	private boolean verbose = true;

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		StringBuffer sb = new StringBuffer();
		List<Mutation> invariantMutations = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			MutationTestResult mutationResult = m.getMutationResult();
			if (mutationResult != null) {
				if (!m.isKilled()) {
					sb.append(m).append('\n');
					if (mutationResult.getDifferentViolatedInvariants() > 0) {
						invariantMutations.add(m);
					}
				}
				
			}
		}
		return sb.toString();
	}
//		Collections.sort(invariantMutations, new Comparator<Mutation>() {
//
//			public int compare(Mutation o1, Mutation o2) {
//				MutationTestResult mr1 = o1.getMutationResult();
//				MutationTestResult mr2 = o2.getMutationResult();
//				return mr1.getDifferentViolatedInvariants()
//						- mr2.getDifferentViolatedInvariants();
//			}
//
//		});
//
//		for (Mutation m : invariantMutations) {
//			MutationTestResult mutationResult = m.getMutationResult();
//
//			sb.append(m.getId()).append(" violated invariants: ").append(
//					mutationResult.getDifferentViolatedInvariants()).append(
//					" classname: ").append(m.getClassName()).append('\n');
//			if (verbose) {
//				sb.append("Violated Invariants:\n");
//				ClassInvariants classInvariants = InvariantProperties
//						.getClassInvariants();
//				int[] violatedInvariants = mutationResult
//						.getViolatedInvariants();
//				for (int i = 0; i < violatedInvariants.length; i++) {
//					InvariantChecker invariantChecker = classInvariants
//							.getInvariantChecker(violatedInvariants[i]);
//					sb.append(invariantChecker.toString()).append('\n');
//				}
//				sb.append('\n');
//			}
//
//		}
//		return sb.toString();

	}


