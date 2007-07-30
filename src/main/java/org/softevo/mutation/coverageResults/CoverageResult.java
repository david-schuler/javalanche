package org.softevo.mutation.coverageResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CoverageResult {

	String className;

	Map<Integer, List<String>> lineData = new TreeMap<Integer, List<String>>();

	public CoverageResult(String className) {
		super();
		this.className = className;
	}

	public void addTestCase(String name, List<Integer> lines) {
		for (Integer i : lines) {
			List<String> testsExercisedThisLine = lineData.get(i);
			if (testsExercisedThisLine == null) {
				testsExercisedThisLine = new ArrayList<String>();
				lineData.put(i,testsExercisedThisLine);
			}
			testsExercisedThisLine.add(name);
		}
	}

	public List<String> getTestCasesForLine(Integer line){
		List<String> testList = lineData.get(line);
		return Collections.unmodifiableList(testList);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Class:" + className);
		sb.append('\n');
		for (Map.Entry<Integer, List<String>> entry : lineData.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue().toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
