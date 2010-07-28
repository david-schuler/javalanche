package de.unisb.cs.st.javalanche.mutation.adaptedMutations.analyze;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.prioritization.Prioritizer;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestPrioritizer {

	private static Logger logger = Logger.getLogger(TestPrioritizer.class);

	private static final String EVALUATION_DATA_FILE_KEY = "evalutation.file";
	// "/scratch/schuler/projects/project-extractor/evaluation-data.csv";
	private static final String REVISION_KEY = "revision";
	private static final String MUTATION_FILE_KEY = "mutation.file.list";

	public static void main(String[] args) throws IOException {
		List<File> mutationFiles = getMutationFiles();
		List<String> result = new ArrayList<String>();
		int expectedRevision = Integer.parseInt(Util
				.getPropertyOrFail(REVISION_KEY));
		String fileName = Util.getPropertyOrFail(EVALUATION_DATA_FILE_KEY);
		File evaluationFile = new File(fileName);
		Map<String, Set<Long>> detectedByTestCase = DetectedByTestAnalyzer
				.getDetectedByTestCaseMap();
		for (File mutationFile : mutationFiles) {
			logger.info("Analyzing file: " + mutationFile);
			result.addAll(evaluatePrioritization(evaluationFile,
					expectedRevision, mutationFile, detectedByTestCase));
			logger.info("Finished analyzing file: " + mutationFile);
		}
		FileUtils.writeLines(new File("evaluationresults.csv"), result);
	}

	private static List<File> getMutationFiles() {
		String fileName = Util.getPropertyOrFail(MUTATION_FILE_KEY);
		File f = new File(fileName);
		List<File> files = new ArrayList<File>();
		// String[] split = fileName.split(":");
		if (f.exists()) {
			try {
				List<String> readLines;
				readLines = FileUtils.readLines(f);
				for (String string : readLines) {
					files.add(new File(string));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("File specified with property "
					+ MUTATION_FILE_KEY + " does not exist: " + f);
		}
		return files;
	}

	private static List<String> evaluatePrioritization(File evaluationFile,
			int expectedRevision, File mutationFile,
			Map<String, Set<Long>> detectedByTestCase) throws IOException {

		logger.info("Test that detect mutations " + detectedByTestCase.size()
				+ " tests");
		List<Long> mutations = getMutations(mutationFile);
		logger.info("Analyzing for " + mutations.size() + " mutations");
		List<String> allTests = getAllTests();
		logger.info("Got " + allTests.size() + " tests");
		List<String> prioritization = getPrioritization(detectedByTestCase,
				mutations, allTests);
		logger.info("Prioritization size " + prioritization.size() + " tests");
		Map<String, Integer> detectingTestCase = getDetectingTestCases(
				evaluationFile, expectedRevision);

		return evaluate(mutationFile, prioritization, detectingTestCase);
	}

	private static List<String> getAllTests() {
		List<TestName> testsForProject = QueryManager.getTestsForProject();
		List<String> allTests = new ArrayList<String>();
		for (TestName testName : testsForProject) {
			String s = testName.getName();
			allTests.add(s);
		}
		Collections.shuffle(allTests);
		return allTests;
	}

	private static Map<String, Integer> getDetectingTestCases(File f,
			int expectedRev) throws IOException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		CSVReader csvReader = new CSVReader(new FileReader(f));
		List<String[]> csvData = csvReader.readAll();
		for (int i = 1; i < csvData.size(); i++) {
			String[] strings = csvData.get(i);
			int rev = Integer.parseInt(strings[0]);
			if (rev == expectedRev) {
				int tests = Integer.parseInt(strings[2]);
				for (int j = 0; j < tests; j++) {
					int postest = 3 + j * 2;
					String testName = strings[postest];
					testName = sanitizeTestCaseName(testName);
					int number = Integer.parseInt(strings[postest + 1]);
					result.put(testName, number);
				}
			}
		}
		return result;
	}

	private static String sanitizeTestCaseName(String testName) {
		testName = testName.trim();
		int indexOf = testName.indexOf(' ');
		if (indexOf > 0) {
			testName = testName.substring(0, indexOf);
		}
		return testName;
	}

	private static List<Long> getMutations(File mutationFile)
			throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(mutationFile));
		List<String[]> csvData = csvReader.readAll();
		List<Long> results = new ArrayList<Long>();
		for (int i = 0; i < csvData.size(); i++) {
			String[] strings = csvData.get(i);
			long id = Long.parseLong(strings[0]);
			results.add(id);
		}
		return results;
	}

	private static List<String> getPrioritization(
			Map<String, Set<Long>> detectedByTestCase, List<Long> mutations,
			List<String> allTests) {
		logger.info("Analyzing " + detectedByTestCase.size() + "tests");
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		for (String key : detectedByTestCase.keySet()) {
			Set<Long> set = detectedByTestCase.get(key);
			for (Long l : set) {
				String ls = l.toString();
				if (mutations.contains(l)) {
					List<String> list;
					if (data.get(key) == null) {
						list = new ArrayList<String>();
						data.put(key, list);
					} else {
						list = data.get(key);
					}
					list.add(ls);
				}
			}
		}
		Set<String> allTestAdd = new HashSet<String>(allTests);
		allTestAdd.addAll(detectedByTestCase.keySet());
		for (String key : allTestAdd) {
			List<String> list;
			if (!data.containsKey(key)) {
				list = new ArrayList<String>();
				data.put(key, list);
			} else {
				list = data.get(key);
			}
			list.add("-2l");
		}
		logger.info("Prioritization for " + data.size() + " entries");
		List<String> prioritize = Prioritizer.prioritize(data);
		return prioritize;
	}

	private static List<String> evaluate(File mutationFile,
			List<String> prioritization, Map<String, Integer> detectingTestCases) {
		List<String> lines = new ArrayList<String>();
		if (prioritization.size() != detectingTestCases.size()) {
			Set<String> prioritizationCopy = new HashSet<String>(prioritization);
			Set<String> testCasesCopy = new HashSet<String>(detectingTestCases
					.keySet());
			prioritizationCopy.removeAll(detectingTestCases.keySet());
			testCasesCopy.removeAll(prioritization);
			logger.warn("Only in prioritization: " + prioritizationCopy.size()
					+ "   " + prioritizationCopy);
			logger.warn("Only in testCases: " + testCasesCopy.size() + "   "
					+ testCasesCopy);
		}
		int size = prioritization.size();
		int sizes[] = new int[] { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
		for (int i : sizes) {
			double percent = ((double) i) / 100.;
			int listSize = (int) (size * percent);
			List<String> topX = prioritization.subList(0, listSize);
			int score = getScore(topX, detectingTestCases);
			lines.add(printScore(mutationFile, score, i, detectingTestCases));
		}
		return lines;
	}

	private static String printScore(File evaluationFile, int score,
			int number, Map<String, Integer> detectingTestCases) {
		Object[] o = new Object[] { evaluationFile, number, score,
				(double) score / detectingTestCases.size() };
		String join = Joiner.on(",").join(o);
		return join;
	}

	private static int getScore(List<String> tests,
			Map<String, Integer> detectingTestCases) {
		int count = 0;
		for (String test : tests) {
			if (detectingTestCases.containsKey(test)) {
				count++;
			}
		}
		return count;
	}
}
