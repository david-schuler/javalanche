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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.analyze.html.ClassReport;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class InvariantAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(InvariantAnalyzer.class);

	private static final String COLUMN = "Invariant Impact";

	protected static String formatPercent(double d) {
		return null;
	}

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		int total = 0;
		int withResult = 0;
		int violated = 0;
		int violatedNotCaught = 0;
		int killed = 0;
		List<Mutation> violatedNotCaughtList = new ArrayList<Mutation>();
		List<String> csvData = new ArrayList<String>();
		csvData
				.add("ID,DETECTED,DIFFERENT INVARIANT VIOLATIONS,TOTAL INVARIANT VIOLATIONS,MUTATION TYPE,CLASS NAME,METHOD NAME,LINE NUMBER,MUTATION FOR LINE");
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				killed++;
			}
			MutationTestResult mutationResult = mutation.getMutationResult();
			if (mutationResult != null) {
				String[] array = new String[] { "" + mutation.getId(),
						"" + mutation.isKilled(),
						"" + mutationResult.getDifferentViolatedInvariants(),
						"" + mutationResult.getTotalViolations(),
						"" + mutation.getMutationType(),
						"" + mutation.getClassName(), mutation.getMethodName(),
						"" + mutation.getLineNumber(),
						"" + mutation.getMutationForLine() };
				String line = Joiner.on(",").join(array);
				csvData.add(line);
			}
			if (mutationResult != null
					&& mutationResult.getDifferentViolatedInvariants() > 0) {
				violated++;
				if (!mutation.isKilled()) {
					violatedNotCaught++;
					violatedNotCaughtList.add(mutation);
				}
			}
			if (mutationResult != null) {
				withResult++;
				ClassReport classReport = report.getClassReport(mutation
						.getClassName());
				if (classReport != null) {
					classReport.addColumn(COLUMN);
					classReport.putEntry(mutation.getId(), COLUMN, mutation
							.getMutationResult()
							.getDifferentViolatedInvariants()
							+ "");
				} else {
					logger.warn("Found no report for class: "
							+ mutation.getClassName());
				}
			}
			total++;
		}
		Io.writeFile(Joiner.on("\n").join(csvData), new File(
				"invariantResults.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("Total Mutations: " + total);
		sb.append('\n');
		sb.append("Killed mutations: " + killed + "\n");
		sb
				.append(String
						.format(
								"Mutations that violated invariants: %d (%s relative to all mutations / %s relative to mutations that where covered)",
								violated, AnalyzeUtil.formatPercent(violated,
										total), AnalyzeUtil.formatPercent(
										violated, withResult)));
		sb.append('\n');
		sb
				.append(String
						.format(
								"Mutations that violated invariants and were not detected: %d (%s relative to all mutations  / %s relative to mutations that where covered)",
								violatedNotCaught, AnalyzeUtil.formatPercent(
										violatedNotCaught, total), AnalyzeUtil
										.formatPercent(violatedNotCaught,
												withResult)));
		sb.append('\n');
		int violatedCaught = violated - violatedNotCaught;
		sb
				.append(String
						.format(
								"Mutations that violated invariants and were detected:  %d (%s relative to all mutations  / %s relative to mutations that where covered)",
								violatedCaught, AnalyzeUtil.formatPercent(
										violatedCaught, total), AnalyzeUtil
										.formatPercent(violatedCaught,
												withResult)));
		if (true) {
			sb
					.append("\n\nList of mutations that violated invariants and were not caught:\n\n");

			Collections.sort(violatedNotCaughtList, new Comparator<Mutation>() {

				public int compare(Mutation o1, Mutation o2) {
					int i1 = o1.getMutationResult()
							.getDifferentViolatedInvariants();
					int i2 = o2.getMutationResult()
							.getDifferentViolatedInvariants();
					return i1 - i2;
				}

			});
			for (Mutation mutation2 : violatedNotCaughtList) {
				String add = "";
				if (mutation2.getMutationForLine() != 0) {
					add = " (" + mutation2.getMutationForLine() + ")";
				}
				sb.append(String.format("Class: %s Line: %d%s  Type: %s",
						mutation2.getClassName(), mutation2.getLineNumber(),
						add, mutation2.getMutationType().toString()));
				sb.append('\n');
				MutationTestResult mr = mutation2.getMutationResult();
				sb.append(String.format("Violated invariants: %d (Ids: %s)", mr
						.getDifferentViolatedInvariants(), Arrays.toString(mr
						.getViolatedInvariants())));
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
