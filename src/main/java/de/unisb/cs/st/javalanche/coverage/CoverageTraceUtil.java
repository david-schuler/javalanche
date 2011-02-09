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
package de.unisb.cs.st.javalanche.coverage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Class that provide methods to read write and compare coverage data. The
 * Format of the coverage data is as follows:
 * 
 * Map<String, Map<String, Map<Integer, Integer>>>
 * 
 * Where the first key is the name of the tests. The second String Key is the
 * name of the method and the Integer key is the line number an its value
 * describes how often this line was executed.
 * 
 * @author David Schuler
 * 
 */
public class CoverageTraceUtil {

	private static final Collection<String> EMPTY_COLLECTION = new ArrayList<String>();
	private static Logger logger = Logger.getLogger(CoverageTraceUtil.class);

	/**
	 * Don not instantiate this class.
	 */
	private CoverageTraceUtil() {
	}

	/**
	 * Returns the methods that have a difference in their trace data. This is
	 * the case when a statement of a method from the same test is executed with
	 * a different frequency (This includes methods executed in only 1 run). A
	 * test that is contained in only one of the arguments is ignored.
	 * 
	 * @param traceData1
	 *            trace data to compare.
	 * @param traceData2
	 *            trace data to compare.
	 * @return the methods that have a difference in their trace data.
	 */
	public static Collection<String> getDifferentMethodsForTests(
			Map<String, Map<String, Map<Integer, Integer>>> traceData1,
			Map<String, Map<String, Map<Integer, Integer>>> traceData2) {
		Set<String> differences = new HashSet<String>();
		Set<String> keySet = traceData1.keySet();
		if (traceData1 == null || traceData2 == null) {
			return EMPTY_COLLECTION;
		}
		for (String testKey : keySet) {
			if (traceData2.containsKey(testKey)) {
				Map<String, Map<Integer, Integer>> testData1 = traceData1
						.get(testKey);
				Map<String, Map<Integer, Integer>> testData2 = traceData2
						.get(testKey);
				int diffPre = differences.size();
				Collection<String> diffMethods = getDifferentMethods(testData1,
						testData2);
				differences.addAll(diffMethods);
				int newDifferences = differences.size() - diffPre;
				if (newDifferences > 0) {
					logger.info("New differences for test" + testKey + "  "
							+ newDifferences);
				}
			}
		}
		return differences;
	}

	/**
	 * Returns the methods that have a difference in their trace data. This is
	 * the case when a statement of a method is executed with a different
	 * frequency (This includes methods executed in only 1 run).
	 * 
	 * @param testData1
	 *            data to compare.
	 * @param testData2
	 *            data to compare.
	 * @return the methods that have a difference in their trace data.
	 */
	public static Collection<String> getDifferentMethods(
			Map<String, Map<Integer, Integer>> testData1,
			Map<String, Map<Integer, Integer>> testData2) {
		Set<String> differences = new HashSet<String>();
		if (testData1 == null && testData2 == null) {
			return differences;
		}
		if (testData1 != null && testData2 == null) {
			differences.addAll(testData1.keySet());
			return differences;
		}
		if (testData1 == null && testData2 != null) {
			differences.addAll(testData2.keySet());
			return differences;
		}
		Set<String> allMethods = getAllMethods(testData1, testData2);
		for (String key : allMethods) {
			boolean difference = false;
			if (testData1.containsKey(key) && testData2.containsKey(key)) {
				Map<Integer, Integer> classData1 = testData1.get(key);
				Map<Integer, Integer> classData2 = testData2.get(key);
				difference = compareLines(classData1, classData2);
			} else {
				difference = true;
			}
			if (difference) {
				differences.add(key);
			}
		}
		return differences;
	}

	/**
	 * @return true, if there is any line difference.
	 */
	private static boolean compareLines(Map<Integer, Integer> lineData1,
			Map<Integer, Integer> lineData2) {
		Set<Integer> allLines = new HashSet<Integer>();
		allLines.addAll(lineData1.keySet());
		allLines.addAll(lineData2.keySet());
		for (Integer line : allLines) {
			if (lineData1.containsKey(line) && lineData2.containsKey(line)
					&& lineData1.get(line).equals(lineData2.get(line))) {
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return all method names from the given trace data.
	 */
	private static Set<String> getAllMethods(
			Map<String, Map<Integer, Integer>> data1,
			Map<String, Map<Integer, Integer>> data2) {
		Set<String> allTests = new HashSet<String>();
		allTests.addAll(data1.keySet());
		allTests.addAll(data2.keySet());
		return allTests;
	}

	public static Collection<String> getDifferentReturnMethodsForTests(
			Map<String, Map<String, Map<Integer, Integer>>> data1T,
			Map<String, Map<String, Map<Integer, Integer>>> data2T) {
		Set<String> differences = new HashSet<String>();
		Set<String> keySet = data1T.keySet();
		if (data1T == null || data2T == null) {
			return EMPTY_COLLECTION;
		}
		for (String testKey : keySet) {
			if (data2T.containsKey(testKey)) {
				Map<String, Map<Integer, Integer>> data1 = data1T.get(testKey);
				Map<String, Map<Integer, Integer>> data2 = data2T.get(testKey);
				Set<String> allMethods = getAllMethods(data1, data2);
				for (String key : allMethods) {
					boolean difference = false;
					if (data1.containsKey(key) && data2.containsKey(key)) {
						Map<Integer, Integer> classData1 = data1.get(key);
						Map<Integer, Integer> classData2 = data2.get(key);
						difference = compareReturns(classData1, classData2);
					} else {
						difference = true;
					}
					if (difference) {
						differences.add(key);
					}
				}
			}
		}
		return differences;
	}

	private static boolean compareReturns(Map<Integer, Integer> returnData1,
			Map<Integer, Integer> returnData2) {
		Set<Integer> allReturns = new HashSet<Integer>();
		allReturns.addAll(returnData1.keySet());
		allReturns.addAll(returnData2.keySet());
		for (Integer returnHash : allReturns) {
			if (returnData1.containsKey(returnHash)
					&& returnData2.containsKey(returnHash)) {
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convenience method to load all coverage traces from a directory. See
	 * method loadTracesFromDirectory.
	 * 
	 * @param dir
	 *            name of the directory (is the mutation id).
	 * @return A map containing the coverage traces.
	 */
	public static Map<String, Map<String, Map<Integer, Integer>>> loadLineCoverageTrace(
			String dir) {
		return CoverageTraceUtil.loadTracesFromDirectory(new File(
				CoverageProperties.TRACE_RESULT_LINE_DIR + "/" + dir));
	}

	/**
	 * Convenience method to load all data traces from a directory. See method
	 * loadTracesFromDirectory.
	 * 
	 * @param dir
	 *            name of the directory (is the mutation id).
	 * @return A map containing the data traces.
	 */
	public static Map<String, Map<String, Map<Integer, Integer>>> loadDataCoverageTrace(
			String dir) {
		return CoverageTraceUtil.loadTracesFromDirectory(new File(
				CoverageProperties.TRACE_RESULT_DATA_DIR + "/" + dir));
	}

	/**
	 * Methods writes the coverage results to given file.
	 * 
	 * @param classMap
	 *            the map to write.
	 * @param testName
	 *            the name of the test
	 * @param fileName
	 *            the name of the file to write
	 */
	public static void writeTrace(
			Map<String, ? extends Map<Integer, Integer>> classMap,
			String testName, String fileName) {
		int numClasses = classMap.size();
		int countClasses = 0;
		ObjectOutputStream oos = null;
		String exceptionMessage = "Could not write " + fileName;
		try {
			oos = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(fileName)));
			oos.writeInt(numClasses);
			for (String s : classMap.keySet()) {
				countClasses++;
				oos.writeUTF(s);
				Map<Integer, Integer> lineMap = classMap.get(s);
				int lineMapSize = lineMap.size();
				oos.writeInt(lineMapSize);
				int countLines = 0;
				for (Entry<Integer, Integer> entry : lineMap.entrySet()) {
					countLines++;
					oos.writeInt(entry.getKey().intValue());
					oos.writeInt(entry.getValue().intValue());
				}
				if (countLines != lineMapSize) {
					logger.warn("Different sizes" + lineMapSize + "  "
							+ countLines + " " + lineMap);
					oos.flush();
					oos.close();
					writeTrace(classMap, testName, fileName);
				}
			}
		} catch (IOException e) {
			CoverageMutationListener.logger.warn(exceptionMessage, e);
			throw new RuntimeException(exceptionMessage, e);
		} finally {
			if (oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
					logger.warn(exceptionMessage, e);
					throw new RuntimeException(exceptionMessage, e);
				}
			}
		}
		if (countClasses != numClasses) {
			logger.warn("Different number of total classes (Writing again) "
					+ countClasses + " " + numClasses + " " + classMap);
			writeTrace(classMap, testName, fileName);
		}
	}

	/**
	 * Load trace all trace files (coverage or data) from a given directory.
	 * 
	 * Returns a map whose first key is the test name and the value is a map as
	 * returned by the loadTrace method.
	 * 
	 * 
	 * @param dir
	 *            the directory to load the traces from.
	 * @return a map whose first key is the test name and the value is a map as
	 *         returned by the loadTrace method.
	 * 
	 */
	public static Map<String, Map<String, Map<Integer, Integer>>> loadTracesFromDirectory(
			File dir) {
		logger.debug("Loading from " + dir);
		if (!dir.exists()) {
			CoverageAnalyzer.logger
					.warn("No files for mutation. Directory does not exist: "
							+ dir);
			return null;
		}
		File[] tests = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith("gz");
			}
		});
		Map<String, Map<String, Map<Integer, Integer>>> result = new HashMap<String, Map<String, Map<Integer, Integer>>>();
		for (File f : tests) {
			Map<String, Map<Integer, Integer>> classMap = loadTrace(f);
			String key = CoverageTraceUtil.stripGz(f.getName());
			result.put(key, classMap);
		}
		return result;
	}

	/**
	 * Loads a trace from the given file. Returns a map with key method names as
	 * keys and an int to int map as value. The key has the from
	 * className@methodName.
	 * 
	 * @param file
	 *            to read from
	 * 
	 * @return a map with key method names as keys and an int to int map as
	 *         value.
	 */
	public static Map<String, Map<Integer, Integer>> loadTrace(File file) {
		Map<String, Map<Integer, Integer>> classMap = new HashMap<String, Map<Integer, Integer>>();
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(file))));

			int numClasses = ois.readInt();
			for (int i = 0; i < numClasses; i++) {
				String className = ois.readUTF();
				int numLines = ois.readInt();
				Map<Integer, Integer> lineMap = new HashMap<Integer, Integer>();
				for (int j = 0; j < numLines; j++) {
					lineMap.put(ois.readInt(), ois.readInt());
				}
				classMap.put(className, lineMap);
			}
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classMap;
	}

	static String stripGz(String test) {
		String key = test;
		if (test.endsWith(".gz")) {
			key = test.substring(0, test.length() - 3);
		}
		return key;
	}

	public static Map<String, Map<String, Map<String, Map<Integer, Integer>>>> loadLineCoverageTraces(
			String baseDir) {
		File dir = new File(baseDir, CoverageProperties.TRACE_RESULT_LINE_DIR);
		File[] list = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return true;
			}
		});

		Map<String, Map<String, Map<String, Map<Integer, Integer>>>> result = new HashMap<String, Map<String, Map<String, Map<Integer, Integer>>>>();
		for (File file : list) {
			String key = file.getName();
			Map<String, Map<String, Map<Integer, Integer>>> coverage = loadLineCoverageTrace(key);
			result.put(key, coverage);
		}
		return result;
	}

	public static Set<String> getDifferentMethodsForTests2(
			Map<String, Map<String, Map<Integer, Integer>>> map1,
			Map<String, Map<String, Map<Integer, Integer>>> map2) {
		if (map1 == null || map2 == null) {
			throw new IllegalArgumentException("Got null as argument");
		}
		Set<String> differences = new HashSet<String>();
		for (String testName : map1.keySet()) {
			Map<String, Map<Integer, Integer>> testMap1 = map1.get(testName);
			Map<String, Map<Integer, Integer>> testMap2 = map2.get(testName);
			Collection<String> diff = getDifferentMethods(
					testMap1, testMap2);
			if (diff.size() > 0) {
				logger.info(diff.size() + " difference for test " + testName);
			}
			differences.addAll(diff);
		}
		return differences;
	}
}
