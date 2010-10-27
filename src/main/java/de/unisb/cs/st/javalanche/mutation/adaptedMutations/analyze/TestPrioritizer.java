package de.unisb.cs.st.javalanche.mutation.adaptedMutations.analyze;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.prioritization.Prioritizer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestPrioritizer {

	private static Logger logger = Logger.getLogger(TestPrioritizer.class);

	private static final String EVALUATION_DATA_FILE_KEY = "evalutation.file";
	// "/scratch/schuler/projects/project-extractor/evaluation-data.csv";
	private static final String REVISION_KEY = "revision";
	private static final String MUTATION_FILE_KEY = "mutation.file.list";

	public static void main(String[] args) throws IOException {
		// analyzeTests();
		doExperiment();
	}

	private static void analyzeTests() throws IOException {
		Map<String, Set<Long>> detectedByTestCase = DetectedByTestAnalyzer
				.getDetectedByTestCaseMap();
		List<TestName> testsForProject = QueryManager.getTestsForProject();
		Set<String> tests = new HashSet<String>();
		for (TestName testName : testsForProject) {
			tests.add(testName.getName());
		}

		String fileName = Util.getPropertyOrFail(EVALUATION_DATA_FILE_KEY);
		File evaluationFile = new File(fileName);
		int expectedRevision = Integer.parseInt(Util
				.getPropertyOrFail(REVISION_KEY));
		Map<String, Integer> detectingTestCases = getDetectingTestCases(
				evaluationFile, expectedRevision);
		Set<String> keySet = detectingTestCases.keySet();
		logger.info("Tests size" + tests.size());
		for (String key : keySet) {
			if (!tests.contains(key)) {
				logger.warn("Test not contained in db " + key);
			}
		}
		Set<Long> coveredMutations = MutationCoverageFile.getCoveredMutations();
		Set<String> allTests = new HashSet<String>();
		Multimap<String, Long> testMap = HashMultimap.create();
		for (Long id : coveredMutations) {
			Set<String> testsForId = MutationCoverageFile.getCoverageDataId(id);
			allTests.addAll(testsForId);
			for (String testName : testsForId) {
				testMap.put(testName, id);
			}
		}
		Multimap<String, Long> detectingTests = HashMultimap.create();
		Session session = HibernateUtil.openSession();
		// Session session =
		// HibernateServerUtil.getSessionFactory(Server.KUBRICK).openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE className LIKE 'org.jaxen%'");
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();

		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				List<TestMessage> all = new ArrayList<TestMessage>();
				Collection<TestMessage> errors = mutationResult.getErrors();
				Collection<TestMessage> failures = mutationResult.getFailures();
				all.addAll(errors);
				all.addAll(failures);
				for (TestMessage testMessage : all) {
					String testCaseName = testMessage.getTestCaseName();
					detectingTests.put(testCaseName, mutation.getId());
				}
			}
		}
		tx.commit();
		session.close();

		logger.info("Test that cover mutations " + allTests.size());
		for (String key : keySet) {
			if (!testMap.containsKey(key)) {
				logger.warn("Test is not covering any mutation: " + key);
			}
			if (key.contains("XPathReader")) {
				logger.info("Ids for " + key + " - " + testMap.get(key).size());
			}
		}
		logger.info("Test that detect mutations " + allTests.size());
		for (String key : testMap.keySet()) {
			if (!detectingTests.containsKey(key)) {
				logger.warn("Test is not detecting any mutation: " + key);
			}
			// if (key.contains("XPathReader")) {
			int covered = testMap.get(key).size();
			int detected = detectingTests.get(key).size();
			logger.info("Test: " + key + " covers " + covered + " and detects "
					+ detected + " Mutations (" + ((double) detected) / covered
					+ "%)");
			// }
		}
	}

	private static void doExperiment() throws IOException {
		List<File> mutationFiles = getMutationFiles();
		List<String> result = new ArrayList<String>();
		result.add("file_name,percent,bugs,bugs_percent");
		int expectedRevision = Integer.parseInt(Util
				.getPropertyOrFail(REVISION_KEY));
		String fileName = Util.getPropertyOrFail(EVALUATION_DATA_FILE_KEY);
		File evaluationFile = new File(fileName);
		Map<String, Set<Long>> detectedByTestCase = DetectedByTestAnalyzer
				.getDetectedByTestCaseMap();
		handleMap(detectedByTestCase);

		Map<String, Integer> detectingTestCase = getDetectingTestCases(
				evaluationFile, expectedRevision);
		// System.out.println("TestPrioritizer.doExperiment()");
		// System.out.println(Joiner.on('\n').join(detectedByTestCase.keySet()));
		// System.out.println(Joiner.on('\n').join(detectingTestCase.keySet()));
		// if (true) {
		// throw new RuntimeException();
		// }
		for (File mutationFile : mutationFiles) {
			logger.info("Analyzing file: " + mutationFile);
			result.addAll(evaluatePrioritization(detectingTestCase,
					expectedRevision, mutationFile, detectedByTestCase));
			logger.info("Finished analyzing file: " + mutationFile);
		}
		FileUtils.writeLines(new File("evaluationresults.csv"), result);
	}

	private static void handleMap(Map<String, Set<Long>> map) {
		Set<String> keys = new HashSet(map.keySet());
		for (String key : keys) {
			if (!key.startsWith("org.jaxen")) {
				String newKey = getNewKey(key);
				Set<Long> remove = map.remove(key);
				map.put(newKey, remove);
			}
		}
	}

	public static String getNewKey(String key) {
		int i = key.indexOf("jaxen");
		String result = "org." + key.substring(i);
		return result;
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

	private static List<String> evaluatePrioritization(
			Map<String, Integer> detectingTestCase, int expectedRevision,
			File mutationFile, Map<String, Set<Long>> detectedByTestCase)
			throws IOException {

		logger.info("Test that detect mutations " + detectedByTestCase.size()
				+ " tests");
		List<Long> mutations = getMutations(mutationFile);
		logger.info("Analyzing for " + mutations.size() + " mutations");
		List<String> allTests = getAllTests();
		logger.info("Got " + allTests.size() + " tests");
		List<String> prioritization = getPrioritization(detectedByTestCase,
				mutations, allTests);
		logger.info("Prioritization size " + prioritization.size() + " tests");

		return evaluate(mutationFile, prioritization, detectingTestCase);
	}

	private static List<String> getAllTests() {
		List<TestName> testsForProject = QueryManager.getTestsForProject();
		List<String> allTests = new ArrayList<String>();
		for (TestName testName : testsForProject) {
			String s = testName.getName();
			if (!s.startsWith("org.jaxen")) {
				s = getNewKey(s);
			}
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
		List<String> prioritize = Prioritizer.prioritize(data,
				Prioritizer.Mode.REPEAT_AFTER_SATISFIED);
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
