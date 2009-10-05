/*
 * Copyright (C) 2009 Saarland University
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public class CoverageTraceUtil {

	private static final Collection<String> EMPTY_COLLECTION = new ArrayList<String>();
	private static Logger logger = Logger.getLogger(CoverageTraceUtil.class);

	public static Collection<String> getDifferentMethodsForTests(
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
				int diffPre = differences.size();
				for (String key : allMethods) {
					if (!InstrumentExclude.shouldExcludeLines(key)) {
						boolean difference = false;
						if (data1.containsKey(key) && data2.containsKey(key)) {
							Map<Integer, Integer> classData1 = data1.get(key);
							Map<Integer, Integer> classData2 = data2.get(key);
							difference = compareLines(classData1, classData2);
						} else {
							difference = true;
						}
						if (difference) {
							differences.add(key);
						}
					}
				}
				int newDifferences = differences.size() - diffPre;
				if (newDifferences > 0) {
					logger.info("New differences for test" + testKey + "  "
							+ newDifferences);
				}
			}
		}
		return differences;
	}

	public static Collection<String> getDifferentMethods(
			Map<String, Map<Integer, Integer>> data1,
			Map<String, Map<Integer, Integer>> data2) {
		Set<String> differences = new HashSet<String>();
		if (data1 == null && data2 == null) {
			return differences;
		}
		if (data1 != null && data2 == null) {
			differences.addAll(data1.keySet());
			return differences;
		}
		if (data1 == null && data2 != null) {
			differences.addAll(data2.keySet());
			return differences;
		}
		Set<String> allMethods = getAllMethods(data1, data2);
		for (String key : allMethods) {
			boolean difference = false;
			if (data1.containsKey(key) && data2.containsKey(key)) {
				Map<Integer, Integer> classData1 = data1.get(key);
				Map<Integer, Integer> classData2 = data2.get(key);
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
	 * @param lineData1
	 * @param lineData2
	 * @return True, if there is any line difference.
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

	private static Map<String, Map<String, Map<String, Map<Integer, Integer>>>> lineCache = new HashMap<String, Map<String, Map<String, Map<Integer, Integer>>>>();
	private static Map<String, Map<String, Map<String, Map<Integer, Integer>>>> dataCache = new HashMap<String, Map<String, Map<String, Map<Integer, Integer>>>>();

	public static Map<String, Map<String, Map<Integer, Integer>>> loadLineCoverageTraceCached(
			String id) {
		if ("0".equals(id)) {
			if (lineCache.containsKey(id)) {
				return lineCache.get(id);
			}
			Map<String, Map<String, Map<Integer, Integer>>> result = CoverageAnalyzer
					.loadLineCoverageTrace(id);
			lineCache.put(id, result);
			return result;
		}
		return CoverageAnalyzer.loadLineCoverageTrace(id);

	}

	public static Map<String, Map<String, Map<Integer, Integer>>> loadDataCoverageTraceCached(
			String id) {
		if ("0".equals(id)) {
			if (dataCache.containsKey(id)) {
				return dataCache.get(id);
			}
			Map<String, Map<String, Map<Integer, Integer>>> result = CoverageAnalyzer
					.loadDataCoverageTrace(id);
			dataCache.put(id, result);
			return result;
		}
		return CoverageAnalyzer.loadDataCoverageTrace(id);
	}

	public static String getFullMethodName(
			Map<String, Map<String, Map<Integer, Integer>>> coverageData,
			String className, int lineNumber) {
		Collection<Map<String, Map<Integer, Integer>>> values = coverageData
				.values();
		for (Map<String, Map<Integer, Integer>> map : values) {
			Set<String> keySet = map.keySet();
			for (String string : keySet) {
				String clazz = string.substring(0, string.indexOf('@'));
				if (clazz.equals(className)) {
					Map<Integer, Integer> lines = map.get(string);
					if (lines.containsKey(lineNumber)) {
//						int start = string.indexOf('@') + 1;
						return string;
					}
				}
			}
		}
		return "";
	}
}
