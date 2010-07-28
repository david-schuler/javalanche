package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.coverage.CoverageTraceUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class PrioritizationAnalyzer implements MutationAnalyzer {

	public static final String MUTATION_PRIORITIZATION_FILE_NAME = "mutationPrioritization.xml";

	private static final String COVERAGE_PRIORITIZATION_FILE_NAME = "mutationCoveragePrioritization.xml";

	private static final String COVERAGE_PRIORITIZATION_CSV = "mutationCoveragePrioritization.csv";

	private static Logger logger = Logger
			.getLogger(PrioritizationAnalyzer.class);

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Multimap<String, Mutation> mm = AnalyzeUtil
				.getDetectedByTest(mutations);

		Map<String, Map<String, Map<Integer, Integer>>> originalTraces = CoverageTraceUtil
				.loadLineCoverageTrace("0");

		List<String> coverageCsv = new ArrayList<String>();
		Collection<Mutation> values = mm.values();
		for (Mutation m : mutations) {
			StringBuilder line = new StringBuilder();
			line.append(m.getId());
			line.append(",");
			if (m.getMutationResult() == null) {
				// if (!values.contains(m)) {
				line.append(false);
				coverageCsv.add(line.toString());
				// }
			} else {
				MutationTestResult mutationResult = m.getMutationResult();
				boolean killed = m.isKilled();
				line.append(killed);
				line.append(",");
				String prefix = line.toString();
				List<TestMessage> passing = mutationResult.getPassing();
				List<String> passingLines = handleTestMessages(originalTraces,
						m, false, passing);
				List<String> combinedLines = combineLines(prefix + "pass,",
						passingLines);
				coverageCsv.addAll(combinedLines);
				Collection<TestMessage> failures = mutationResult.getFailures();
				List<String> failingLines = handleTestMessages(originalTraces,
						m, true, failures);
				coverageCsv
						.addAll(combineLines(prefix + "fail,", failingLines));
				Collection<TestMessage> errors = mutationResult.getErrors();
				List<String> errorLines = handleTestMessages(originalTraces, m,
						true, errors);
				coverageCsv.addAll(combineLines(prefix + "error,", errorLines));
			}
		}

		// for (String testName : mm.keySet()) {
		// logger.info("Looking for test: " + testName);
		// Map<String, Map<Integer, Integer>> origTestTrace = originalTraces
		// .get(testName);
		// logger.info("Got " + originalTraces.size() + " method traces");
		// int diffMethods = 0;
		// coverageCsv.append(testName);
		// coverageCsv.append('\n');
		// for (Mutation m : mm.get(testName)) {
		// Map<String, Map<String, Map<Integer, Integer>>> coverageTraces =
		// CoverageTraceUtil
		// .loadLineCoverageTrace(m.getId() + "");
		// Map<String, Map<Integer, Integer>> mutationTestTrace = coverageTraces
		// .get(testName);
		// Collection<String> differentMethods = CoverageTraceUtil
		// .getDifferentMethods(origTestTrace, mutationTestTrace);
		// logger.info("Result for test " + testName + "  " + diffMethods);
		// coverageCsv.append(testName);
		// coverageCsv.append(',');
		// coverageCsv.append(m.getId());
		// coverageCsv.append(',');
		// coverageCsv.append(Join.join(",", differentMethods));
		// coverageCsv.append('\n');
		// }
		// }
		File f = writeFile(Joiner.on("\n").join(coverageCsv),
				COVERAGE_PRIORITIZATION_CSV);
		return "Stored results in " + f.getAbsolutePath();

	}

	private List<String> combineLines(String prefix, List<String> lines) {
		List<String> result = new ArrayList<String>();
		for (String line : lines) {
			result.add(prefix + line);
		}
		return result;
	}

	private List<String> handleTestMessages(
			Map<String, Map<String, Map<Integer, Integer>>> originalTraces,
			Mutation m, boolean killedInTest, Collection<TestMessage> passing) {
		List<String> list = new ArrayList<String>();
		for (TestMessage testMessage : passing) {
			StringBuilder coverageCsv = new StringBuilder();
			String testName = testMessage.getTestCaseName();
			Map<String, Map<String, Map<Integer, Integer>>> coverageTraces = CoverageTraceUtil
					.loadLineCoverageTrace(m.getId() + "");
			Map<String, Map<Integer, Integer>> mutationTestTrace = coverageTraces
					.get(testName);
			Map<String, Map<Integer, Integer>> origTestTrace = originalTraces
					.get(testName);
			Collection<String> differentMethods = CoverageTraceUtil
					.getDifferentMethods(origTestTrace, mutationTestTrace);
			coverageCsv.append(killedInTest);
			coverageCsv.append(',');
			coverageCsv.append(testName);
			coverageCsv.append(',');
			coverageCsv.append(Joiner.on(",").join(differentMethods));
			list.add(coverageCsv.toString());
		}
		return list;
	}

	private File writeFile(String content, String fileName) {
		String dirName = System.getProperty("prioritization.dir");
		if (dirName != null) {
			File dir = new File(dirName);
			if (dir.exists()) {
				File f = new File(dir, fileName);
				Io.writeFile(content, f);
				return f;
			} else {
				throw new RuntimeException("Directory does not exist " + dir);
			}
		} else {
			throw new RuntimeException("Property not set: prioritization.dir");
		}
	}

	private void writePrioritization(List<String> prioritization,
			String fileName) {
		String dirName = System.getProperty("prioritization.dir");
		if (dirName != null) {
			File dir = new File(dirName);
			if (dir.exists()) {
				XmlIo.toXML(prioritization, new File(dir, fileName));
			} else {
				throw new RuntimeException("File does not exist " + dir);
			}
		} else {
			throw new RuntimeException("Property not set: prioritization.dir");
		}
	}

	private List<String> prioritizeCoverage(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> workingMap = copy(mm);
		Set<String> tests = getAllTests(mm);
		List<String> prioritization = new ArrayList<String>();
		boolean copy = false;
		while (tests.size() > 0) {
			int max = -1;
			String testName = null;
			for (String test : tests) {
				// Collection<Mutation> collection = workingMap.get(test);
				int score = getScore(test, workingMap);
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
						+ getScore(testName, mm));
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

	private int getScore(String testName, Multimap<String, Mutation> workingMap) {
		Collection<Mutation> mutations = workingMap.get(testName);
		logger.info(mutations.size() + " mutations for test: " + testName);
		Map<String, Map<String, Map<Integer, Integer>>> originalTraces = CoverageTraceUtil
				.loadLineCoverageTrace("0");
		logger.info("Looking for test: " + testName);
		Map<String, Map<Integer, Integer>> origTestTrace = originalTraces
				.get(testName);
		logger.info("Got " + originalTraces.size() + " method traces");
		int diffMethods = 0;
		for (Mutation m : mutations) {
			Map<String, Map<String, Map<Integer, Integer>>> coverageTraces = CoverageTraceUtil
					.loadLineCoverageTrace(m.getId() + "");
			Map<String, Map<Integer, Integer>> mutationTestTrace = coverageTraces
					.get(testName);
			Collection<String> differentMethods = CoverageTraceUtil
					.getDifferentMethods(origTestTrace, mutationTestTrace);
			diffMethods += differentMethods.size();
			diffMethods += 1;
		}
		// TODO
		logger.info("Result for test " + testName + "  " + diffMethods);
		return diffMethods;
	}

	public static Set<String> getAllTests(Multimap<String, Mutation> mm) {
		Set<String> tests = new HashSet<String>(mm.keySet());
		Set<String> testsFromProperty = getTestsFromProperty();
		int testsPreSize = tests.size();
		tests.addAll(testsFromProperty);
		if (tests.size() > testsPreSize) {
			logger.info("Added " + (tests.size() - testsPreSize)
					+ " tests that do not cover any mutation");
			System.out.println("Added " + (tests.size() - testsPreSize)
					+ " tests that do not cover any mutation");
		}
		return tests;
	}

	private List<String> prioritize(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> workingMap = copy(mm);
		Set<String> tests = getAllTests(mm);
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

	private static Set<String> getTestsFromProperty() {
		String testMethods = System.getProperty("test.methods");
		String[] split = testMethods.split(":");
		System.out.println("PrioritizationAnalyzer.getTestsFromProperty() "
				+ split.length);
		Set<String> result = new HashSet<String>(Arrays.asList(split));
		return result;
	}

	private Multimap<String, Mutation> copy(Multimap<String, Mutation> mm) {
		Multimap<String, Mutation> result = HashMultimap.create();
		result.putAll(mm);
		return result;
	}

}
