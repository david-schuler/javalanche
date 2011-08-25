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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class MutationResultAnalyzer implements MutationAnalyzer {

	private static final Logger logger = Logger
			.getLogger(MutationResultAnalyzer.class);

	private static final boolean WRITE_FILES = false;

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		int touched = 0;
		int notTouched = 0;
		List<Mutation> killedList = new ArrayList<Mutation>();
		List<Mutation> survivedList = new ArrayList<Mutation>();
		List<Mutation> survivedTouchedList = new ArrayList<Mutation>();
		List<Long> notCovered = new ArrayList<Long>();
		int covered = 0;
		for (Mutation mutation : mutations) {
			if (mutation == null) {
				throw new RuntimeException("Null fetched from db");
			}
			MutationTestResult mutationResult = mutation.getMutationResult();
			boolean mutationTouched = mutationResult != null
					&& mutationResult.isTouched();
			if (mutation.isDetected()) {
				killedList.add(mutation);
			} else {
				survivedList.add(mutation);
				if (mutationTouched) {
					survivedTouchedList.add(mutation);
				}
			}
			if (mutationTouched) {
				touched++;
			} else {
				notTouched++;
			}
			boolean isCovered = MutationCoverageFile
					.isCovered(mutation.getId());
			boolean isDerived = mutation.getBaseMutationId() != null;
			String info = mutation.getMutationType() + " Derived " + isDerived
					+ "  " + mutation.toShortString() + " "
					+ mutation.getAddInfo() + " "
					+ mutation.getOperatorAddInfo();
			if (isCovered != mutationTouched) {
				String message;
				if (isCovered) {
					message = "Mutation is considered to be covered by tests, but was actually not executed during mutation testing.";
				} else {
					message = "Mutation was not considered to be covered by tests, but was actually executed during mutation testing.";
				}
				logger.warn(message + "  " + info);
				notCovered.add(mutation.getId());
			}
			if (isCovered) {
				covered++;
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
		try {
			FileUtils.writeLines(new File(ConfigurationLocator
					.getJavalancheConfiguration().getOutputDir()
					+ "/not-covered-during-mutation-testing-id.txt"),
					notCovered);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (WRITE_FILES) {
			XmlIo.toXML(killedList, "killed-mutations.xml");
			XmlIo.toXML(survivedList, "survived-mutations.xml");
			XmlIo.toXML(killedIds, "killed-ids.xml");
			XmlIo.toXML(survivedIds, "survived-ids.xml");
		}
		sb.append(formatLine("Total mutations:  ", total));
		sb.append(formatLine("Covered mutations (in scan step): ", covered,
				AnalyzeUtil.formatPercent(covered, total)));
		sb.append(formatLine(
				"Covered mutations (actually executed during mutation testing): ",
				touched, AnalyzeUtil.formatPercent(touched, total)));
		sb.append(formatLine("Not covered mutations: ", notTouched,
				AnalyzeUtil.formatPercent(notTouched, total)));
		sb.append(formatLine("Killed mutations: ", killed,
				AnalyzeUtil.formatPercent(killed, total)));
		sb.append(formatLine("Surviving mutations: ", survived,
				AnalyzeUtil.formatPercent(survived, total)));
		// sb.append("IDs:\n");
		// for (Long survivedId : survivedIds) {
		// sb.append(survivedId).append(", ");
		// }
		sb.append(formatLine("Mutation score: ",
				AnalyzeUtil.formatPercent(killed, total)));
		sb.append(formatLine(
				"Mutation score for mutations that were covered: ",
				AnalyzeUtil.formatPercent(killed, touched)));
		if (notCovered.size() > 0) {
			sb.append(formatLine(
					"Mutations that where expected to be covered but actually not covered: ",
					notCovered.size(), notCovered.toString()));
		}
		return sb.toString();
	}

	private static String formatLine(String message, int number, String percent) {
		return String.format("%-60s %10d (%6s)\n", message, number, percent);
	}

	private String formatLine(String message, int number) {
		return String.format("%-60s %10d \n", message, number);
	}

	private String formatLine(String message, String number) {
		return String.format("%-60s %10s \n", message, number);
	}

}
