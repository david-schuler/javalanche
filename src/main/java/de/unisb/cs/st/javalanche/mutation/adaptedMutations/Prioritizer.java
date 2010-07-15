package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Join;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.DetectedByTestAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class Prioritizer {

	private static final String EVALUATION_DATA_FILE_KEY = "evalutation.file";
	// "/scratch/schuler/projects/project-extractor/evaluation-data.csv";
	private static final String REVISION_KEY = "revision";
	private static final String MUTATION_FILE_KEY = "mutation.file.list";

	public static void main(String[] args) throws IOException {
		List<File> mutationFiles = getMutationFiles();
		List<String> result = new ArrayList<String>();
		for (File mutationFile : mutationFiles) {
			String fileName = Util.getPropertyOrFail(EVALUATION_DATA_FILE_KEY);
			File evaluationFile = new File(fileName);
			int expectedRevision = Integer.parseInt(Util
					.getPropertyOrFail(REVISION_KEY));
			result.addAll(evaluatePrioritization(evaluationFile,
					expectedRevision, mutationFile));
		}
		FileUtils.writeLines(new File("evalutationresults.csv"), result);
	}

	private static List<File> getMutationFiles() {
		String fileList = Util.getPropertyOrFail(MUTATION_FILE_KEY);
		String[] split = fileList.split(":");
		List<File> files = new ArrayList<File>();
		for (String string : split) {
			files.add(new File(string));
		}
		return files;
	}

	private static List<String> evaluatePrioritization(File evaluationFile,
			int expectedRevision, File mutationFile) throws IOException {
		File outFile = DetectedByTestAnalyzer.getOutFile();
		Multimap<String, Long> detectedByTestCase = XmlIo.get(outFile);
		List<Long> mutations = getMutations(mutationFile);
		List<String> allTests = getAllTests();
		List<String> prioritization = getPrioritization(detectedByTestCase,
				mutations, allTests);
		Map<String, Integer> detectingTestCase = getDetectingTestCases(
				evaluationFile, expectedRevision);
		return evaluate(evaluationFile, prioritization, detectingTestCase);
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
					int number = Integer.parseInt(strings[postest + 1]);
					result.put(testName, number);
				}
			}
		}
		return result;
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
			Multimap<String, Long> detectedByTestCase, List<Long> mutations,
			List<String> allTests) {
		Multimap<String, Long> copyMap = new HashMultimap<String, Long>();
		Collection<Entry<String, Long>> entries = detectedByTestCase.entries();
		for (Entry<String, Long> entry : entries) {
			if (mutations.contains(entry.getKey())) {
				copyMap.put(entry.getKey(), entry.getValue());
			}
		}
		List<String> result = new ArrayList<String>();
		while (copyMap.size() > 0) {
			Set<String> keySet = copyMap.keySet();
			int max = 0;
			String maxKey = "";
			for (String key : keySet) {
				int v = copyMap.get(key).size();
				if (v > max) {
					max = v;
					maxKey = key;
				}
			}
			if (max == 0) {
				List<String> tmpList = new ArrayList<String>(keySet);
				Collections.shuffle(tmpList);
				result.addAll(tmpList);
				for (String string : tmpList) {
					copyMap.removeAll(string);
				}
			}
			result.add(maxKey);
			copyMap.removeAll(maxKey);
		}
		for (String test : allTests) {
			if (!result.contains(test)) {
				result.add(test);
			}
		}
		return result;
	}

	private static List<String> evaluate(File evaluationFile,
			List<String> prioritization, Map<String, Integer> detectingTestCases) {
		List<String> lines = new ArrayList<String>();
		int size = prioritization.size();
		List<String> top5 = prioritization.subList(0, (int) (size * 0.05));
		List<String> top10 = prioritization.subList(0, (int) (size * 0.1));
		List<String> top25 = prioritization.subList(0, (int) (size * 0.25));
		List<String> top50 = prioritization.subList(0, (int) (size * 0.5));

		int top5Score = getScore(top5, detectingTestCases);
		int top10Score = getScore(top10, detectingTestCases);
		int top25Score = getScore(top25, detectingTestCases);
		int top50Score = getScore(top50, detectingTestCases);
		int top100Score = getScore(prioritization, detectingTestCases);

		lines.add(printScore(evaluationFile, top5Score, 5, detectingTestCases));
		lines
				.add(printScore(evaluationFile, top10Score, 10,
						detectingTestCases));
		lines
				.add(printScore(evaluationFile, top25Score, 25,
						detectingTestCases));
		lines
				.add(printScore(evaluationFile, top50Score, 50,
						detectingTestCases));
		lines.add(printScore(evaluationFile, top100Score, 100,
				detectingTestCases));
		return lines;
	}

	private static String printScore(File evaluationFile, int score,
			int number, Map<String, Integer> detectingTestCases) {
		Object[] o = new Object[] { evaluationFile, number, score,
				(double) score / detectingTestCases.size() };
		String join = Join.join(",", o);
		return join;
	}

	private static int getScore(List<String> tests,
			Map<String, Integer> detectingTestCase) {
		int count = 0;
		for (String test : tests) {
			if (detectingTestCase.containsKey(test)) {
				count++;
			}
		}
		return count;
	}
}
