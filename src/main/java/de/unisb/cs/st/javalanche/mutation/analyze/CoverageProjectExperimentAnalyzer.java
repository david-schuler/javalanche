package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.ArrayList;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.coverage.CoverageTraceUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class CoverageProjectExperimentAnalyzer implements MutationAnalyzer {

	int differences = 0;
	int noDifferences = 0;

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Map<String, Map<String, Map<String, Map<Integer, Integer>>>> traces = CoverageTraceUtil
				.loadLineCoverageTraces(new File(".").getAbsolutePath());
		Map<String, Map<String, Map<Integer, Integer>>> unMutated = CoverageTraceUtil
				.loadLineCoverageTrace("0");

		List<Long> detected = new ArrayList<Long>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				detected.add(mutation.getId());
			}
		}
		Set<String> keySet = new TreeSet<String>(traces.keySet());
		List<String> lines = new ArrayList<String>();
		int count = 0;
		for (String key : keySet) {
			long id = getId(key);
			if (id > 0) {
				boolean isDetected = detected.contains(id);
				Map<String, Map<String, Map<Integer, Integer>>> map = traces
						.get(key);
				List<String> entries = getCsvEntries(isDetected, key, map,
						unMutated);
				lines.addAll(entries);
				count++;
			}
		}

		String csv = Joiner.on("\n").join(lines);
		StringBuilder sb = new StringBuilder();
		sb.append("Mutations with coverage differences: " + differences);
		sb.append("\nTotal mutations: " + count);
		sb.append("\nTests with differences: " + lines.size());
		sb.append("\nTests with no difference: " + noDifferences);
		String property = MutationProperties.PROJECT_SOURCE_DIR;
		if (property == null) {
			throw new RuntimeException("Expeceted property to be set "
					+ MutationProperties.PROJECT_SOURCE_DIR_KEY);
		}
		File outDir = new File(property);
		Io.writeFile(csv, new File(outDir, "mutation-coverage-result.csv"));
		return sb.toString();
	}

	private long getId(String key) {
		try {
			long id = Long.parseLong(key);
			return id;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private List<String> getCsvEntries(boolean isDetected, String mutationId,
			Map<String, Map<String, Map<Integer, Integer>>> map,
			Map<String, Map<String, Map<Integer, Integer>>> unMutated) {
		Set<String> keySet = map.keySet();
		StringBuilder sb = new StringBuilder();

		List<String> lines = new ArrayList<String>();
		boolean noDiff = true;
		for (String key : keySet) {
			sb.append(key);
			sb.append(',');
			if (!unMutated.containsKey(key)) {
				throw new RuntimeException(
						"Test not contained n original result " + key);
			}
			Map<String, Map<Integer, Integer>> v1 = map.get(key);
			Map<String, Map<Integer, Integer>> v2 = unMutated.get(key);
			Collection<String> differentMethods = CoverageTraceUtil
					.getDifferentMethods(v1, v2);
			if (differentMethods.size() > 0) {
				String join = Joiner.on(",").join(differentMethods);
				String line = Joiner.on(",").join(
						new Object[] { isDetected,
						mutationId, key, join });
				lines.add(line);
				if (noDiff) {
					noDiff = false;
					differences++;
				}
			} else {
				noDifferences++;
			}
		}
		return lines;
	}

}
