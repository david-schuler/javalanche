package de.unisb.cs.st.javalanche.coverage;

import static de.unisb.cs.st.javalanche.coverage.CoverageAnalyzer.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class CompareTraces {

	private static class PermutatedFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			if (name.startsWith(CoverageProperties.PERMUTED_PREFIX)) {
				return true;
			}
			return false;
		}
	}

	private static Logger logger = Logger.getLogger(CompareTraces.class);

	private enum Mode {
		LINE, DATA
	};

	public static void comparePermuted() {
		File dir = new File(CoverageProperties.TRACE_RESULT_LINE_DIR);
		logger.info("Total methods: " + getAllMethods());
		String[] files = dir.list(new PermutatedFilter());
		HashSet<String> diffComplete = new HashSet<String>();
		int count = 0;
		int excludePre = InstrumentExclude.numberOfExlusions();
		for (String file : files) {
			Set<String> differences = calculateDifferences(EnumSet.of(
					Mode.LINE, Mode.DATA), "0", file);
			count++;
			int preValue = diffComplete.size();
			diffComplete.addAll(differences);
			logger.info("Added " + (diffComplete.size() - preValue)
					+ " methods. Total methods added: " + diffComplete.size()
					+ " Total methods excluded: "
					+ InstrumentExclude.numberOfExlusions());

		}
		 System.out.println("Methods that have differences in at least one run:"
				+ diffComplete.size());
		System.out.println("Added exclusions for this run: "
				+ (InstrumentExclude.numberOfExlusions() - excludePre));
		InstrumentExclude.save();
		XmlIo.toXML(diffComplete, CoverageProperties.TRACE_DIFFERENCES_FILE);
	}

	private static int getAllMethods() {
		Map<String, Map<String, Map<Integer, Integer>>> trace1 = CoverageTraceUtil
				.loadLineCoverageTraceCached("0");
		Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
				.loadDataCoverageTraceCached("0");
		Set<String> methods = new HashSet<String>();
		addMethods(trace1, methods);
		addMethods(trace2, methods);
		return methods.size();
	}

	private static void addMethods(
			Map<String, Map<String, Map<Integer, Integer>>> trace1,
			Set<String> methods) {
		Set<Entry<String, Map<String, Map<Integer, Integer>>>> entrySet = trace1
				.entrySet();
		for (Entry<String, Map<String, Map<Integer, Integer>>> entry : entrySet) {
			Set<String> keySet = entry.getValue().keySet();
			logger.debug(entry.getKey() + " Methods " + keySet.size());
			methods.addAll(keySet);
		}
	}

	private static Set<String> calculateDifferences(Set<Mode> modes,
			String id1, String id2) {
		Set<String> allDifferences = new HashSet<String>();
		System.out.println(id1 + " VS. " + id2 + ": ");
		if (modes.contains(Mode.LINE)) {
			logger.info("Comparing lines");
			Map<String, Map<String, Map<Integer, Integer>>> trace1 = CoverageTraceUtil
					.loadLineCoverageTraceCached(id1);
			Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
					.loadLineCoverageTraceCached(id2);
			// logger.info("Line data 1" + trace1);
			// logger.info("Line data 2" + trace2);
			Set<String> differences = compare(trace1, trace2);
			allDifferences.addAll(differences);
			// logger.info("Differences " + differences);
			for (String string : differences) {
				InstrumentExclude.addExcludeLine(string);
			}
		}
		if (modes.contains(Mode.DATA)) {
			logger.info("Comparing data");
			Map<String, Map<String, Map<Integer, Integer>>> trace1 = CoverageTraceUtil
					.loadDataCoverageTraceCached(id1);
			Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
					.loadDataCoverageTraceCached(id2);
			// logger.info("Return data 1" + trace1);
			// logger.info("Return data 2" + trace2);
			Set<String> differences = compare(trace1, trace2);
			allDifferences.addAll(differences);
			logger.info("Differences " + differences);
			for (String string : differences) {
				InstrumentExclude.addExcludeReturn(string);
			}
		}
		// XmlIo.toXML(differences, CoverageProperties.TRACE_DIFFERENCES_FILE);
		return allDifferences;
	}

	private static Set<String> compareTraces(
			Map<String, Map<String, Map<Integer, Integer>>> map1,
			Map<String, Map<String, Map<Integer, Integer>>> map2) {
		if (map1 == null || map2 == null) {
			throw new IllegalArgumentException("Got null as argument");
		}
		Set<String> differences = new HashSet<String>();
		for (String testName : map1.keySet()) {
			Map<String, Map<Integer, Integer>> testMap1 = map1.get(testName);
			Map<String, Map<Integer, Integer>> testMap2 = map2.get(testName);
			Collection<String> diff = CoverageTraceUtil.getDifferentMethods(
					testMap1, testMap2);
			if (diff.size() > 0) {
				logger.info(diff.size() + " difference for test " + testName);
			}
			differences.addAll(diff);

		}
		return differences;
	}

	private static Set<String> compare(
			Map<String, Map<String, Map<Integer, Integer>>> trace1,
			Map<String, Map<String, Map<Integer, Integer>>> trace2) {
		Set<String> diff1 = compareTraces(trace1, trace2);
		Set<String> diff2 = compareTraces(trace2, trace1);
		Set<String> result = new HashSet<String>(diff1);
		result.addAll(diff2);
		return result;
	}

	public static void main(String[] args) {
		comparePermuted();
		// boolean exit = false;
		// if (args.length < 1) {
		// exit = true;
		// }
		// if (exit) {
		// System.out.println("Error - read help");
		// }
		//
		// StringTokenizer st = new StringTokenizer(args[0]);
		// CompareTraces ct = null;
		//
		// if (!args[0].contains("cmpid") && st.countTokens() >= 3) {
		// ct = new CompareTraces(st.nextToken(), st.nextToken(), st
		// .nextToken());
		// } else if (!args[0].contains("cmpmode") && st.countTokens() >= 1) {
		// ct = new CompareTraces(st.nextToken());
		// } else {
		// ct = new CompareTraces();
		// }

	}

}
