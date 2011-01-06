/*
* Copyright (C) 2010 Saarland University
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

import static de.unisb.cs.st.javalanche.coverage.CoverageAnalyzer.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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

	static Logger logger = Logger.getLogger(CompareTraces.class);
	private static boolean zeroAsBase = true;

	private enum Mode {
		LINE, DATA
	};

	public static void comparePermuted() {
		File dir = new File(CoverageProperties.TRACE_RESULT_LINE_DIR);
		logger.info("Total methods: " + getAllMethods());
		String[] files = dir.list(new PermutatedFilter());

		String base = files[new Random().nextInt(files.length)];
		if (zeroAsBase) {
			base = "0";
		}
		HashSet<String> diffComplete = new HashSet<String>();
		int count = 0;
		int excludePre = InstrumentExclude.numberOfExlusions();
		for (String file : files) {
			Set<String> differences = calculateDifferences(EnumSet.of(
					Mode.LINE, Mode.DATA), base, file);
			count++;
			diffComplete.addAll(differences);
		}
		// System.out.println("Methods that have differences in at least one run:"
		// + diffComplete.size());
		System.out.println("Added exclusions for this run: "
				+ (InstrumentExclude.numberOfExlusions() - excludePre));
		System.out.println(" Total methods excluded: "
				+ InstrumentExclude.numberOfExlusions());
		InstrumentExclude.save();
		XmlIo.toXML(diffComplete, CoverageProperties.TRACE_DIFFERENCES_FILE);
	}

	private static int getAllMethods() {
		Map<String, Map<String, Map<Integer, Integer>>> trace1 = CoverageTraceUtil
				.loadLineCoverageTrace("0");
		Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
				.loadDataCoverageTrace("0");
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
					.loadLineCoverageTrace(id1);
			Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
					.loadLineCoverageTrace(id2);
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
					.loadDataCoverageTrace(id1);
			Map<String, Map<String, Map<Integer, Integer>>> trace2 = CoverageTraceUtil
					.loadDataCoverageTrace(id2);
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

	private static Set<String> compare(
			Map<String, Map<String, Map<Integer, Integer>>> trace1,
			Map<String, Map<String, Map<Integer, Integer>>> trace2) {
		Set<String> diff1 = CoverageTraceUtil.getDifferentMethodsForTests2(trace1, trace2);
		Set<String> diff2 = CoverageTraceUtil.getDifferentMethodsForTests2(trace2, trace1);
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
