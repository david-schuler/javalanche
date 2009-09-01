package de.unisb.cs.st.javalanche.coverage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoverageTraceUtil {

	
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
		// // logger.info("Data1: " + data1);
		// // logger.info("Data2: " + data2);
		// allClasses.addAll(data1.keySet());
		// allClasses.addAll(data2.keySet());
		// Multimap<String, String> methods = new HashMultimap<String,
		// String>();
		// for (String className : allClasses) {
		// Map<String, Map<Integer, Integer>> map1 = data1.get(className);
		// if (map1 != null) {
		// for (String method : map1.keySet()) {
		// methods.put(className, method);
		// }
		// }
		// Map<String, Map<Integer, Integer>> map2 = data2.get(className);
		// if (map2 != null) {
		// for (String method : map2.keySet()) {
		// methods.put(className, method);
		// }
		// }
		// }
		return allTests;
	}
}
