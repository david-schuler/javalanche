package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Join;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class PrioritizationAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger
			.getLogger(PrioritizationAnalyzer.class);

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Multimap<String, Mutation> mm = new HashMultimap<String, Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				Collection<TestMessage> errors = mutationResult.getErrors();
				Collection<TestMessage> failures = mutationResult.getFailures();
				Set<TestMessage> all = new HashSet<TestMessage>(errors);
				all.addAll(failures);
				for (TestMessage testMessage : all) {
					String testCaseName = testMessage.getTestCaseName();
					mm.put(testCaseName, mutation);
				}
			}
		}
		List<String> prioritization = prioritize(mm);
		String dirName = System.getProperty("prioritization.dir");
		if (dirName != null) {
			File dir = new File(dirName);
			if (dir.exists()) {
				XmlIo.toXML(prioritization, new File(dir,
						"mutationPrioritization.xml"));
			} else {
				throw new RuntimeException("File does not exist " + dir);
			}
		} else {
			throw new RuntimeException("Property not set: prioritization.dir");
		}
		return Join.join("\n", prioritization);
	}

	private List<String> prioritize(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> workingMap = copy(mm);
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
		List<String> prioritization = new ArrayList<String>();
		boolean copy = false;
		while (tests.size() > 0) {
			int max = -1;
			String testName = null;
			for (String test : tests) {
				Collection<Mutation> collection = workingMap.get(test);
				if (collection.size() > max) {
					max = collection.size();
					testName = test;
				}
			}
			if (testName != null) {
				Collection<Mutation> mutationsToRemove = new ArrayList<Mutation>(
						workingMap.get(testName));
				prioritization.add(testName + " - " + max + " "
						+ mutationsToRemove.size());
				workingMap.removeAll(testName);
				tests.remove(testName);
				for (String test : tests) {
					Collection<Mutation> testMutations = workingMap.get(test);
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

	private Set<String> getTestsFromProperty() {
		String testMethods = System.getProperty("test.methods");
		String[] split = testMethods.split(":");
		System.out.println("PrioritizationAnalyzer.getTestsFromProperty() "
				+ split.length);
		Set<String> result = new HashSet<String>(Arrays.asList(split));
		return result;
	}

	private Multimap<String, Mutation> copy(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> result = new HashMultimap<String, Mutation>();
		result.putAll(mm);
		return result;
	}

}
