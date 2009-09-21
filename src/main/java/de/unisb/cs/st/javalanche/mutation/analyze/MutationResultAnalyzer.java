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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class MutationResultAnalyzer implements MutationAnalyzer {

	private static final boolean WRITE_FILES = false;

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		int touched = 0;
		int notTouched = 0;
		int classInit = 0;
		List<Mutation> killedList = new ArrayList<Mutation>();
		List<Mutation> survivedList = new ArrayList<Mutation>();
		List<Mutation> survivedTouchedList = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation == null) {
				throw new RuntimeException("Null fetched from db");
			}
			MutationTestResult mutationResult = mutation.getMutationResult();

			if (mutation.isKilled()) {
				killedList.add(mutation);
			} else {
				survivedList.add(mutation);
				if (mutationResult != null && mutationResult.isTouched()) {
					survivedTouchedList.add(mutation);
				}
			}
			if (mutationResult != null && mutationResult.isTouched()) {
				touched++;
			} else {
				notTouched++;
			}
			if (mutation.isClassInit()) {
				classInit++;
			}
		}
		int killed = killedList.size();
		int survived = survivedList.size();
		int total = survived + killed;
		assert (survived + killed) == (notTouched + touched);
		StringBuilder sb = new StringBuilder();

		Set<Long> killedIds = new TreeSet<Long>();
		Set<Long> survivedIds = new TreeSet<Long>();
		for (Mutation mutation : killedList) {
			killedIds.add(mutation.getId());
		}
		for (Mutation mutation : survivedTouchedList) {
			survivedIds.add(mutation.getId());
		}
		if (WRITE_FILES) {
			XmlIo.toXML(killedList, "killed-mutations.xml");
			XmlIo.toXML(survivedList, "survived-mutations.xml");
			XmlIo.toXML(killedIds, "killed-ids.xml");
			XmlIo.toXML(survivedIds, "survived-ids.xml");
		}
		sb.append(formatLine("Total mutations:  ", total));
		sb.append(formatLine("Touched mutations: ", touched, AnalyzeUtil
				.formatPercent(touched, total)));
		sb.append(formatLine("Not touched mutations: ", notTouched, AnalyzeUtil
				.formatPercent(notTouched, total)));
		sb.append(formatLine("Killed mutations: ", killed, AnalyzeUtil
				.formatPercent(killed, total)));
		sb.append(formatLine("Survived mutations: ", survived, AnalyzeUtil
				.formatPercent(survived, total)));
		// sb.append("IDs:\n");
		// for (Long survivedId : survivedIds) {
		// sb.append(survivedId).append(", ");
		// }
		sb.append(formatLine("Mutation score: ", AnalyzeUtil.formatPercent(
				killed, total)));
		sb.append(formatLine(
				"Mutation score for mutations that were covered: ", AnalyzeUtil
						.formatPercent(killed, touched)));
		return sb.toString();
	}

	private static String formatLine(String message, int number, String percent) {
		return String.format("%-55s %10d (%6s)\n", message, number, percent);
	}

	private String formatLine(String message, int number) {
		return String.format("%-55s %10d \n", message, number);
	}

	private String formatLine(String message, String number) {
		return String.format("%-55s %10s \n", message, number);
	}

}
