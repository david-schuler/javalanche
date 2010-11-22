package de.unisb.cs.st.javalanche.mutation.adaptedMutations.analyze;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import au.com.bytecode.opencsv.CSVReader;
import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.prioritization.Prioritizer;
import de.unisb.cs.st.ds.util.prioritization.Prioritizer.Mode;

public class AnalyzeFiles {

	public static void main(String[] args) throws IOException {
		Map<String, List<String>> evaluationMap = parseEvalutationFile();
		String revision = Util.getPropertyOrFail("revision");
		Map<String, Set<Long>> detectedByTestCaseMap = DetectedByTestAnalyzer
				.getDetectedByTestCaseMap();
		List<String> mutations = getMutationFile();
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		for (String key : detectedByTestCaseMap.keySet()) {
			Set<Long> set = detectedByTestCaseMap.get(key);
			for (Long l : set) {
				String ls = l.toString();
				if (mutations.contains(ls)) {
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
		List<String> prioritize = Prioritizer.prioritize(data,
				Mode.REPEAT_AFTER_SATISFIED);

	}

	private static List<String> getMutationFile() throws IOException {
		String fileName = Util.getPropertyOrFail("mutation.file");
		List<String> lines = FileUtils.readLines(new File(fileName));
		return lines;
	}

	private static Map<String, List<String>> parseEvalutationFile()
			throws IOException {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		String evaluationFileName = Util.getPropertyOrFail("evaluation.file");
		File f = new File(evaluationFileName);
		CSVReader r = new CSVReader(new FileReader(f));
		List<String[]> readAll = r.readAll();
		for (String[] strings : readAll) {
			String id = strings[0];
			List<String> tests = new ArrayList<String>();
			for (int i = 1; i < strings.length; i++) {
				String testEntry = strings[i];
				int index = testEntry.lastIndexOf('-');
				String testName = testEntry.substring(0, index);
				tests.add(testName);
			}
			result.put(id, tests);
		}
		return result;
	}
}
