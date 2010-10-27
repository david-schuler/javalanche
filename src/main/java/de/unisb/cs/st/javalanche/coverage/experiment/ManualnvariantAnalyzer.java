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
package de.unisb.cs.st.javalanche.coverage.experiment;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.coverage.CoverageTraceUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

// 	 -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.coverage.experiment.ManualnvariantAnalyzer 
public class ManualnvariantAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger
			.getLogger(ManualnvariantAnalyzer.class);

	public static void main(String[] args) {
		new ManualnvariantAnalyzer().checkManualResults();
	}

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		return checkManualResults();
	}

	private String checkManualResults() {
		Map<Mutation, Boolean> mutationMap = ManualClassifications
				.getManualClassification();
		StringBuilder csv = new StringBuilder();
		int right = 0;
		int wrong = 0;
		Set<Mutation> keySet = new TreeSet<Mutation>(
				ManualResultsAnalyzer.MUTATION_COMP);
		keySet.addAll(mutationMap.keySet());
		Map<String, Map<String, Map<Integer, Integer>>> lineCoverage = CoverageTraceUtil
				.loadLineCoverageTrace("0");

		for (Mutation m : keySet) {
			Mutation dbMutation = QueryManager.getMutationOrNull(m);
			logger.info("Analyzing  " + m.toShortString());
			int invImpact = 0;
			if (dbMutation != null) {
				MutationTestResult result = dbMutation.getMutationResult();
				csv.append(dbMutation.getId());
				csv.append(',');
				csv.append(dbMutation.getClassName());
				csv.append(',');
				csv.append(dbMutation.getLineNumber());
				csv.append(',');
				String mName = ManualResultsAnalyzer.getMethodName(
						lineCoverage, m.getClassName(), m
						.getLineNumber());
				mName = mName.substring(mName.indexOf('@') + 1);
				csv.append(',');
				csv.append(dbMutation.getMutationForLine());
				csv.append(',');
				csv.append(dbMutation.getMutationType());
				csv.append(',');
				csv.append(mutationMap.get(m) ? "Non Equivalent "
						: "Equivalent mutation ");
				if (result != null) {
					csv.append(',');
					invImpact = result.getDifferentViolatedInvariants();
					csv.append(invImpact);
					csv.append(',');
					csv.append(result.getTotalViolations());
					csv.append('\n');
				} else {
					logger.warn("Result null for " + dbMutation);
					csv.append("No result");
				}
				boolean nonEquiv = mutationMap.get(m);

				if (invImpact > 0) {
					if (nonEquiv) {
						right++;
					} else {
						wrong++;
					}
				}
			} else {
				logger.warn("Mutation not found " + m);
			}
		}
		String classification = "Right: " + right + " Wrong: " + wrong;
		System.out.println("\n\n " + classification);
		Io.writeFile(csv.toString(), new File(
				"manual-classification-results.csv"));
		return "";
	}

}
