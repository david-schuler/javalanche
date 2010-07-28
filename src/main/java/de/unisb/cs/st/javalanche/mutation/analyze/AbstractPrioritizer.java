package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class AbstractPrioritizer {

	private static Logger logger = Logger.getLogger(AbstractPrioritizer.class);

	public static interface ScoreCalculator {
		public int getScore(Collection<Mutation> mutations);
	}

	public static List<String> prioritize(Multimap<String, Mutation> mm,
			ScoreCalculator sc) {
		Multimap<String, Mutation> workingMap = copy(mm);
		Set<String> tests = getAllTests(mm);
		List<String> prioritization = new ArrayList<String>();
		boolean copy = false;
		while (tests.size() > 0) {
			int max = -1;
			String testName = null;
			for (String test : tests) {
				Collection<Mutation> mutations = workingMap.get(test);
				int score = sc.getScore(mutations);
				if (score > max) {
					max = score;
					testName = test;
				}
			}
			if (testName != null) {
				Collection<Mutation> mutationsToRemove = new ArrayList<Mutation>(
						workingMap.get(testName));
				prioritization.add(testName + " - " + max + " "
						+ mutationsToRemove.size() + " "
						+ mm.get(testName).size());
				workingMap.removeAll(testName);
				tests.remove(testName);
				for (String test : tests) {
					Collection<Mutation> testMutations = new HashSet<Mutation>(
							workingMap.get(test));
					for (Mutation mutation : testMutations) {
						if (mutationsToRemove.contains(mutation)) {
							workingMap.remove(test, mutation);
						}
					}
				}
				copy = true;
			} else if (testName == null && !copy) {
				copy = true;
				workingMap = copy(mm);
			} else {
				logger
						.warn("Tests covering no mutaitons adding them in random order "
								+ tests);
				List<String> testList = new ArrayList<String>(tests);
				tests.clear();
				Collections.shuffle(testList);
				prioritization.addAll(testList);
			}
		}
		return prioritization;
	}

	private static Multimap<String, Mutation> copy(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> result = HashMultimap.create();
		result.putAll(mm);
		return result;
	}

	public static Set<String> getAllTests(Multimap<String, Mutation> mm) {
		Set<String> tests = new HashSet<String>(mm.keySet());
		Set<String> testsFromProperty = getTestsFromProperty();
		int testsPreSize = tests.size();
		tests.addAll(testsFromProperty);
		if (tests.size() > testsPreSize) {
			logger.info("Added " + (tests.size() - testsPreSize)
					+ " tests that were do not cover any mutation");
			System.out.println("Added " + (tests.size() - testsPreSize)
					+ " tests that do not cover any mutation");
		}
		return tests;
	}

	private static Set<String> getTestsFromProperty() {
		String testMethods = System.getProperty("test.methods");
		String[] split = testMethods.split(":");
		System.out.println("PrioritizationAnalyzer.getTestsFromProperty() "
				+ split.length);
		Set<String> result = new HashSet<String>(Arrays.asList(split));
		return result;
	}

}
