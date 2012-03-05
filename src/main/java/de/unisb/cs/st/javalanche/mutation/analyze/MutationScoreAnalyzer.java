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

/**
 * This analyzer class calculates the mutation score of the class/method units
 * of the system under test. The mutation scores are displayed in the HTML
 * report and the command line interface, though additional information is
 * added to an exported CSV file.
 *
 * The CSV file contains the exact number of mutants generated/covered/killed,
 * the total/killed number of mutation types, the tests that were touched and
 * basic information of the code unit.
 *
 * @author Kevin Jalbert <kevin.j.jalbert@gmail.com>
 */
public class MutationScoreAnalyzer implements MutationAnalyzer {

	private static final Logger logger = Logger.getLogger(MutationResultAnalyzer.class);

	private static final boolean WRITE_FILES = false;

	// Set of maps to hold the data relevant to classes
	private Map<String, Integer> classKilledMutations = new HashMap<String, Integer>();
	private Map<String, Integer> classCoveredMutations = new HashMap<String, Integer>();
	private Map<String, Integer> classTotalMutations = new HashMap<String, Integer>();
	private Map<String, Set<String>> classMutantTests = new HashMap<String, Set<String>>();
	private Map<String, MutationTypeCount> classMutationTypes = new HashMap<String, MutationTypeCount>();

	// Set of maps to hold the data relevant to methods
	private Map<String, Integer> methodKilledMutations = new HashMap<String, Integer>();
	private Map<String, Integer> methodCoveredMutations = new HashMap<String, Integer>();
	private Map<String, Integer> methodTotalMutations = new HashMap<String, Integer>();
	private Map<String, Set<String>> methodMutantTests = new HashMap<String, Set<String>>();
	private Map<String, MutationTypeCount> methodMutationTypes = new HashMap<String, MutationTypeCount>();

	// Set of lists to hold the output of the class/method data
	private List<String> classScores = new ArrayList<String>();
	private List<String> methodScores = new ArrayList<String>();

	/**
	 * A data class to aid in the collection of mutation type information. The 
	 * total number of occurrences and kills for a mutation type are here.
	 *
	 * @author Kevin Jalbert <kevin.j.jalbert@gmail.com>
	 */
	public class MutationTypeCount {

		public Map<String, Integer> typesTotal = new HashMap<String, Integer>();
		public Map<String, Integer> typesKilled = new HashMap<String, Integer>();

		/**
		 * Constructor that creates maps to hold the total/killed mutation types 
		 */
		public MutationTypeCount(){
			for (Mutation.MutationType mutationType : Mutation.MutationType.values()) {
				typesTotal.put(mutationType.toString(), 0);
				typesKilled.put(mutationType.toString(), 0);
			}
		}

		/**
		 * Increment the passed mutation type by one for the total count.
		 *
		 * @param mutationType the mutation type to increment
		 */
		public void incTypeTotal(String mutationType) {
			Integer count = typesTotal.get(mutationType);
			typesTotal.put(mutationType, count + 1);
		}

		/**
		 * Increment the passed mutation type by one for the killed count.
		 *
		 * @param mutationType the mutation type to increment
		 */
		public void incTypeKilled(String mutationType) {
			Integer count = typesKilled.get(mutationType);
			typesKilled.put(mutationType, count + 1);
		}
	}

	/**
	 * Iterates through all the mutations and extracts the important data from
	 * them. The class and method maps are populated with the extracted data 
	 *
	 * @param mutations iterable set of mutations from system under test
	 */
	private void collectClassMethodData(Iterable<Mutation> mutations) {

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
			}	else {
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
			}	else {
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
				mutationTypeCount.incTypeTotal(mutation.getMutationType().toString());
				classMutationTypes.put(mutantClassName, mutationTypeCount);
			}	else {
				classTotalMutations.put(mutantClassName, count + 1);
				classMutationTypes.get(mutantClassName).incTypeTotal(mutation.getMutationType().toString());
			}

			// Handle counting the number of mutations per method
			count = methodTotalMutations.get(mutantMethodName);
			if (count == null) {
				methodTotalMutations.put(mutantMethodName, 1);
				methodCoveredMutations.put(mutantMethodName, 0);
				methodKilledMutations.put(mutantMethodName, 0);

				MutationTypeCount mutationTypeCount = new MutationTypeCount();
				mutationTypeCount.incTypeTotal(mutation.getMutationType().toString());
				methodMutationTypes.put(mutantMethodName, mutationTypeCount);
			}	else {
				methodTotalMutations.put(mutantMethodName, count + 1);
				methodMutationTypes.get(mutantMethodName).incTypeTotal(mutation.getMutationType().toString());
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

				classMutationTypes.get(mutantClassName).incTypeKilled(mutation.getMutationType().toString());
				methodMutationTypes.get(mutantMethodName).incTypeKilled(mutation.getMutationType().toString());
			}
		}
	}

	/**
	 * Perform mutation score calculations on the collected class data. The
	 * collected data is added into the global list to be outputted into a CSV.
	 * The data is also appended to the StringBuilder to be outputted to the
	 * display.
	 *
	 * @param sb the StringBuilder to be append all the class data for display
	 */
	private void addClassData(StringBuilder sb) {

		for (String className : classTotalMutations.keySet()) {

			int killed = classKilledMutations.get(className);
			int covered = classCoveredMutations.get(className);
			int total = classTotalMutations.get(className);

			double coveredScore = 0;
			if (killed > covered) {
				coveredScore = 1.0;  // Limits score when more kills then covered exists (weird case)
			} else if (covered > 0) {
				coveredScore = (double) killed/covered;
			}

			double totalScore = 0;
			if (killed > total) {
				totalScore = 1.0;  // Limits score when more kills then total exists (weird case)
			} else if (total > 0) {
				totalScore = (double) killed/total;
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
	}

	/**
	 * Perform mutation score calculations on the collected method data. The
	 * collected data is added into the global list to be outputted into a CSV.
	 * The data is also appended to the StringBuilder to be outputted to the
	 * display.
	 *
	 * @param sb the StringBuilder to be append all the method data for display
	 */
	private void addMethodData(StringBuilder sb) {
		for (String methodName : methodTotalMutations.keySet()) {

			int killed = methodKilledMutations.get(methodName);
			int covered = methodCoveredMutations.get(methodName);
			int total = methodTotalMutations.get(methodName);

			double coveredScore = 0;
			if (killed > covered) {
				coveredScore = 1.0;  // Limits score when more kills then covered exists (weird case)
			} else if (covered > 0) {
				coveredScore = (double) killed/covered;
			}

			double totalScore = 0;
			if (killed > total) {
				totalScore = 1.0;  // Limits score when more kills then total exists (weird case)
			} else if (total > 0) {
				totalScore = (double) killed/total;
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
	}

	/**
	 * The analyze method that is called by the analyzeResults task. This method
	 * calculates the mutation scores as well as additional test/type information
	 * and outputs it to the display and a CSV file.
	 *
	 * @param mutations iterable set of mutations from system under test
	 * @param report the HTML report that is being produced (untouched)
	 * @return the class/method mutation score data in a string format
	 */
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

		// Acquire the CSV list of mutation types (KILLED and TOTAL)
		String mutationTypeString = "";
		for (Mutation.MutationType mutationType : Mutation.MutationType.values()) {
			mutationTypeString += "KILLED_" + mutationType.toString() + ",";
			mutationTypeString += "TOTAL_" + mutationType.toString() + ",";
		}

		collectClassMethodData(mutations);

		// Build up output for class and method data
		StringBuilder sb = new StringBuilder();
		sb.append(formatTitle("----------Class Mutation Score----------"));
		sb.append(formatHeading("Class Name:", "Of Covered", "Of Generated"));
		classScores.add("CLASS_NAME,KILLED_MUTANTS,COVERED_MUTANTS,GENERATED_MUTANTS," +
				"MUTATION_SCORE_OF_COVERED_MUTANTS,MUTATION_SCORE_OF_GENERATED_MUTANTS," + 
				mutationTypeString + "TESTS_TOUCHED");
		addClassData(sb);

		sb.append(formatTitle("----------Method Mutation Score----------"));
		sb.append(formatHeading("Method Name:", "Of Covered", "Of Generated"));
		methodScores.add("CLASS_NAME,METHOD_NAME,KILLED_MUTANTS,COVERED_MUTANTS,GENERATED_MUTANTS," +
				"MUTATION_SCORE_OF_COVERED_MUTANTS,MUTATION_SCORE_OF_GENERATED_MUTANTS," + 
				mutationTypeString + "TESTS_TOUCHED");
		addMethodData(sb);

		// Write collected data to CSV
		try {
			FileUtils.writeLines(new File(ConfigurationLocator.getJavalancheConfiguration().getOutputDir()
					+ "/class-scores.csv"),	classScores);
			FileUtils.writeLines(new File(ConfigurationLocator.getJavalancheConfiguration().getOutputDir()
					+ "/method-scores.csv"), methodScores);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return sb.toString();
	}

	/**
	 * Formats a single line for the class/method mutation score title.
	 *
	 * @param title the current section title (class/method)
	 * @return the formatted string
	 */
	private String formatTitle(String title) {
		return String.format("%55s \n", title);
	}

	/**
	 * Formats a single line for the class/method mutation score headings.
	 *
	 * @param type the class/method name
	 * @param covered the mutation score of the covered mutants
	 * @param generated the mutation score of the generated mutants
	 * @return the formatted string
	 */
	private String formatHeading(String type, String covered, String generated) {
		return String.format("%-53s %12s, %12s\n", type, covered, generated);
	}

	/**
	 * Formats a single line for the class/method mutation score result.
	 *
	 * @param name the class/method name
	 * @param covered the mutation score of the covered mutants
	 * @param generated the mutation score of the generated mutants
	 * @return the formatted string
	 */
	private static String formatLine(String name, String covered, String generated) {
		return String.format("%-53s %12s, %12s\n", name, covered, generated);
	}
}
