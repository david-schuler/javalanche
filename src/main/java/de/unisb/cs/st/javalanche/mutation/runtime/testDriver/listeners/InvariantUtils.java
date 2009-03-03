package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.SerializeIo;

public class InvariantUtils {

	static final File DIR = new File("invariant-files");

	private static File mappingFile = null;

	private static Map<String, Integer> testNumbers = null;

	private static Map<String, Set<Integer>> invariantsPerTest = null;

	private static Map<String, File> files = null;

	private static Map<String, Set<Integer>> testInvariantViolations = null;

	static File getMappingFile() {
		if (mappingFile == null) {
			File[] listFiles = DIR.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith("mapping.txt")) {
						return true;
					}
					return false;
				}
			});
			assert (listFiles.length == 1);
			mappingFile = listFiles[0];
		}
		return mappingFile;
	}

	public static Map<String, Set<Integer>> getTestInvariantMap() {
		if (invariantsPerTest == null) {
			invariantsPerTest = new HashMap<String, Set<Integer>>();
			Set<Entry<String, File>> entrySet = getInvariantFilesForTests()
					.entrySet();
			for (Entry<String, File> entry : entrySet) {
				String testName = entry.getKey();
				File file = entry.getValue();
				if (file.exists()) {
					Set<Integer> ids = SerializeIo.get(file);
					invariantsPerTest.put(testName, ids);
				}
			}
		}
		return invariantsPerTest;
	}

	public static Map<String, File> getInvariantFilesForTests() {
		if (files == null) {
			files = new HashMap<String, File>();
			Set<Entry<String, Integer>> entrySet = getTestNumbers().entrySet();
			for (Entry<String, Integer> entry : entrySet) {
				int number = entry.getValue();
				String testName = entry.getKey();
				File file = new File(DIR, "invariant-ids-" + number + ".ser");
				files.put(testName, file);
			}
		}
		return files;
	}

	private static Map<String, Integer> getTestNumbers() {
		if (testNumbers == null) {
			testNumbers = new HashMap<String, Integer>();
			File mappingFile = getMappingFile();
			List<String> mappingLines = Io.getLinesFromFile(mappingFile);
			for (String line : mappingLines) {
				int number = InvariantUtils.getNumber(line);
				String testName = InvariantUtils.getTestName(line);
				testNumbers.put(testName, number);
			}
		}
		return testNumbers;
	}

	static int getNumber(String line) {
		int end = line.indexOf(',');
		String number = line.substring(end - 4, end);
		return Integer.parseInt(number);
	}

	static String getTestName(String line) {
		int index = line.indexOf(',');
		int index2 = line.indexOf('(');
		String test = line.substring(index + 1, index2);
		String className = line.substring(index2 + 1, line.length() - 1);
		return className + "." + test;
	}

	public static File getInvariantFile(String testName) {
		return getInvariantFilesForTests().get(testName);
	}

	public static Map<String, Set<Integer>> getAllUnmutatedViolations() {
		if (testInvariantViolations == null) {
			testInvariantViolations = new HashMap<String, Set<Integer>>();
			Set<String> tests = getTestNumbers().keySet();
			for (String testName : tests) {
				File file = getViolationsFile(testName);
				if (file.exists()) {
					Set<Integer> setForTest = SerializeIo.get(file);
					testInvariantViolations.put(testName, setForTest);
				} else {
					throw new RuntimeException("File for test " + testName
							+ " does not exist " + file);
				}
			}
		}
		return testInvariantViolations;
	}

	public static Set<Integer> getUnmutatedViolations(String testName) {
		return getAllUnmutatedViolations().get(testName);
	}

	public static File getViolationsFile(String testName) {
		return new File(DIR, "violations-" + testName);
	}

	public static Set<Integer> getAllViolated() {
		Set<Integer> result = new HashSet<Integer>();
		Collection<Set<Integer>> values = getAllUnmutatedViolations().values();
		for (Set<Integer> set : values) {
			result.addAll(set);
		}
		return result;
	}
}
