package org.softevo.mutation.coverageResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class CoverageResult {

	private static Logger logger = Logger.getLogger(CoverageResult.class);

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
				lineData.put(i, testsExercisedThisLine);
			}
			testsExercisedThisLine.add(name);
		}
	}

	public List<String> getTestCasesForLine(Integer line) {
		if(line ==null){
			throw new IllegalArgumentException("linenumber is null");
		}
		if (lineData.containsKey(line)) {
			List<String> testList = lineData.get(line);
			return Collections.unmodifiableList(testList);

		} else {
			logger.info("no coverage results for line: " + line);
			return new ArrayList<String>();
		}

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
