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
import java.util.HashSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class MutationScoreAnalyzer implements MutationAnalyzer {

	private static final Logger logger = Logger
			.getLogger(MutationResultAnalyzer.class);

	private static final boolean WRITE_FILES = false;

	public class MutationTypeCount {

		Map<String, Integer> typesTotal = new HashMap<String, Integer>();
		Map<String, Integer> typesKilled = new HashMap<String, Integer>();

		public MutationTypeCount(){
			for (Mutation.MutationType mutationType : Mutation.MutationType.values()) {
				typesTotal.put(mutationType.toString(), 0);
				typesKilled.put(mutationType.toString(), 0);
			}
		}

		public void addTypeTotal(String mutationType){
			Integer count = typesTotal.get(mutationType);
			typesTotal.put(mutationType, count + 1);
		}

		public void addTypeKilled(String mutationType){
			Integer count = typesKilled.get(mutationType);
			typesKilled.put(mutationType, count + 1);
		}
	}

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

		Map<String, Integer> classKilledMutations = new HashMap<String, Integer>();
		Map<String, Integer> classCoveredMutations = new HashMap<String, Integer>();
		Map<String, Integer> classTotalMutations = new HashMap<String, Integer>();
		Map<String, MutationTypeCount> classMutationTypes = new HashMap<String, MutationTypeCount>();

		Map<String, Integer> methodKilledMutations = new HashMap<String, Integer>();
		Map<String, Integer> methodCoveredMutations = new HashMap<String, Integer>();
		Map<String, Integer> methodTotalMutations = new HashMap<String, Integer>();
		Map<String, MutationTypeCount> methodMutationTypes = new HashMap<String, MutationTypeCount>();

		Map<String, Set<String>> classMutantTests = new HashMap<String, Set<String>>();
		Map<String, Set<String>> methodMutantTests = new HashMap<String, Set<String>>();

		List<String> classScores = new ArrayList<String>();
		List<String> methodScores = new ArrayList<String>();
		List<String> coveredTests = new ArrayList<String>();

		// Acquire the CSV list of mutation types (KILLED and TOTAL)
		String mutationTypeString = "";
		for (Mutation.MutationType mutationType : Mutation.MutationType.values()) {
			mutationTypeString += "KILLED_" + mutationType.toString() + ",";
			mutationTypeString += "TOTAL_" + mutationType.toString() + ",";
		}

		for (Mutation mutation : mutations) {

			if (mutation == null) {
				throw new RuntimeException("Null fetched from db");
			}

			MutationTestResult mutationResult = mutation.getMutationResult();

			Integer count = 0;
			boolean mutationTouched = mutationResult != null && mutationResult.isTouched();
			String mutantClassName = mutation.getClassName();
			String mutantMethodName = mutantClassName + "." + mutation.getMethodName();

			// Handle keeping track of unique tests per method
			Set<String> tests = methodMutantTests.get(mutantMethodName);
			if (tests == null) {
				tests = new HashSet<String>();
			}
			else {
				if (mutationResult != null) {
					for (TestMessage testMessage : mutationResult.getAllTestMessages()) {
						tests.add(testMessage.getTestCaseName());
					}
				}
			}
			methodMutantTests.put(mutantMethodName, tests);

			// Handle keeping track of unique tests per class
			tests = classMutantTests.get(mutantClassName);
			if (tests == null) {
				tests = new HashSet<String>();
			}
			else {
				if (mutationResult != null) {
					for (TestMessage testMessage : mutationResult.getAllTestMessages()) {
						tests.add(testMessage.getTestCaseName());
					}
				}
			}
			classMutantTests.put(mutantClassName, tests);

			// Handle counting the total mutants
			count = classTotalMutations.get(mutantClassName);
			if (count == null) {
				classTotalMutations.put(mutantClassName, 1);
				classCoveredMutations.put(mutantClassName, 0);
				classKilledMutations.put(mutantClassName, 0);

				MutationTypeCount mutationTypeCount = new MutationTypeCount();
				mutationTypeCount.addTypeTotal(mutation.getMutationType().toString());
				classMutationTypes.put(mutantClassName, mutationTypeCount);
			}
			else {
				classTotalMutations.put(mutantClassName, count + 1);
				classMutationTypes.get(mutantClassName).addTypeTotal(mutation.getMutationType().toString());
			}

			// Handle counting the number of mutations per method
			count = methodTotalMutations.get(mutantMethodName);
			if (count == null) {
				methodTotalMutations.put(mutantMethodName, 1);
				methodCoveredMutations.put(mutantMethodName, 0);
				methodKilledMutations.put(mutantMethodName, 0);

				MutationTypeCount mutationTypeCount = new MutationTypeCount();
				mutationTypeCount.addTypeTotal(mutation.getMutationType().toString());
				methodMutationTypes.put(mutantMethodName, mutationTypeCount);
			}
			else {
				methodTotalMutations.put(mutantMethodName, count + 1);
				methodMutationTypes.get(mutantMethodName).addTypeTotal(mutation.getMutationType().toString());
			}

			// Handle counting covered mutants
			if (mutationTouched) {
				count = classCoveredMutations.get(mutantClassName);
				classCoveredMutations.put(mutantClassName, count + 1);

				count = methodCoveredMutations.get(mutantMethodName);
				methodCoveredMutations.put(mutantMethodName, count + 1);
			}

			// Handle counting the killed mutants
			if (mutation.isDetected()) {

				count = classKilledMutations.get(mutantClassName);
				classKilledMutations.put(mutantClassName, count + 1);

				count = methodKilledMutations.get(mutantMethodName);
				methodKilledMutations.put(mutantMethodName, count + 1);

				classMutationTypes.get(mutantClassName).addTypeKilled(mutation.getMutationType().toString());
				methodMutationTypes.get(mutantMethodName).addTypeKilled(mutation.getMutationType().toString());
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(formatTitle("----------Class Mutation Score----------"));
		sb.append(formatHeading("Class Name:", "Of Covered", "Of Generated"));
		classScores.add("CLASS_NAME,KILLED_MUTANTS,COVERED_MUTANTS,GENERATED_MUTANTS,MUTATION_SCORE_OF_COVERED_MUTANTS,MUTATION_SCORE_OF_GENERATED_MUTANTS," + mutationTypeString + "TESTS_TOUCHED");
		for (String className : classTotalMutations.keySet()) {

			int killed = classKilledMutations.get(className);
			int covered = classCoveredMutations.get(className);
			int total = classTotalMutations.get(className);

			double coveredScore = 0;
			if (covered > 0) {
				coveredScore = (double) killed/covered;
			}

			double totalScore = 0;
			if (total > 0) {
				totalScore = (double) killed/total;
			}

			// Handle weird situation where the number killed > covered
			if (killed > covered) {
				coveredScore = 1.0;
			}

			// Handle weird situation where the number killed > total
			if (killed > total) {
				totalScore = 1.0;
			}

			String tests = "";
			for (String test : classMutantTests.get(className)) {
				tests += test + " ";
			}
			tests.trim();

			String typeCounts = "";
			MutationTypeCount unitTypes = classMutationTypes.get(className);
			for (String type : unitTypes.typesKilled.keySet()) {
				typeCounts += unitTypes.typesKilled.get(type).toString() + ",";
				typeCounts += unitTypes.typesTotal.get(type).toString() + ",";
			}

			classScores.add(
				className + "," +
				killed + "," +
				covered + "," +
				total + "," +
				coveredScore + "," +
				totalScore + "," +
				typeCounts + tests
			);

			sb.append(formatLine(className + ": ",
				AnalyzeUtil.formatPercent(killed, covered),
				AnalyzeUtil.formatPercent(killed, total))
			);
		}

		sb.append(formatTitle("----------Method Mutation Score----------"));
		sb.append(formatHeading("Method Name:", "Of Covered", "Of Generated"));
		methodScores.add("CLASS_NAME,METHOD_NAME,KILLED_MUTANTS,COVERED_MUTANTS,GENERATED_MUTANTS,MUTATION_SCORE_OF_COVERED_MUTANTS,MUTATION_SCORE_OF_GENERATED_MUTANTS," + mutationTypeString + "TESTS_TOUCHED");
		for (String methodName : methodTotalMutations.keySet()) {

			int killed = methodKilledMutations.get(methodName);
			int covered = methodCoveredMutations.get(methodName);
			int total = methodTotalMutations.get(methodName);

			double coveredScore = 0;
			if (covered > 0) {
				coveredScore = (double) killed/covered;
			}

			double totalScore = 0;
			if (total > 0) {
				totalScore = (double) killed/total;
			}

			// Handle weird situation where the number killed > covered
			if (killed > covered) {
				coveredScore = 1.0;
			}

			// Handle weird situation where the number killed > total
			if (killed > total) {
				totalScore = 1.0;
			}

			String tests = "";
			for (String test : methodMutantTests.get(methodName)) {
				tests += test + " ";
			}
			tests.trim();

			String typeCounts = "";
			MutationTypeCount unitTypes =  methodMutationTypes.get(methodName);
			for (String type : unitTypes.typesKilled.keySet()) {
				typeCounts += unitTypes.typesKilled.get(type).toString() + ",";
				typeCounts += unitTypes.typesTotal.get(type).toString() + ",";
			}

			String className = methodName.substring(0, methodName.lastIndexOf('.'));
			methodScores.add(
				className + "," +
				methodName + "," +
				killed + "," +
				covered + "," +
				total + "," +
				coveredScore + "," +
				totalScore + "," +
				typeCounts + tests
			);

			sb.append(formatLine(methodName.substring(0, methodName.lastIndexOf('(')) + ": ",
				AnalyzeUtil.formatPercent(killed, covered),
				AnalyzeUtil.formatPercent(killed, total))
			);
		}

		try {
			FileUtils.writeLines(new File(ConfigurationLocator
					.getJavalancheConfiguration().getOutputDir()
					+ "/class-scores.csv"),
					classScores);
			FileUtils.writeLines(new File(ConfigurationLocator
					.getJavalancheConfiguration().getOutputDir()
					+ "/method-scores.csv"),
					methodScores);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	private static String formatLine(String message, String percent, String percent1) {
		return String.format("%-56s %8s, %8s\n", message, percent, percent1);
	}

	private String formatTitle(String message) {
		return String.format("%-60s \n", message);
	}

	private String formatHeading(String message, String message1, String message2) {
		return String.format("%-50s %14s, %14s\n", message, message1, message2);
	}
}
