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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.analyze.AnalyzeUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

/**
 * @author David Schuler
 * 
 */
public class CoverageSplitSuiteAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger
			.getLogger(CoverageSplitSuiteAnalyzer.class);

	private Map<String, Map<String, Map<Integer, Integer>>> originalLineData;

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Set<String> allTests = new HashSet<String>();
		for (Mutation m : mutations) {
			if (m.getMutationResult() != null) {
				MutationTestResult result = m.getMutationResult();
				Collection<TestMessage> allTestMessages = result
						.getAllTestMessages();
				for (TestMessage testMessage : allTestMessages) {
					allTests.add(testMessage.getTestCaseName());
				}
			}
		}
		Set<Set<String>> splits = computeSplits(allTests);
		Map<Long, Double> result = new HashMap<Long, Double>();
		StringBuilder sb = new StringBuilder();
		int allDetectedBySplitCount = 0;
		int allFullImpactDetected = 0;
		int allFullImpactNotDetected = 0;
		int allFullNoImpactDetected = 0;
		int allFullNoImpactNotDetected = 0;

		for (Set<String> split : splits) {
			int detectedBySplitCount = 0;
			int fullImpactDetected = 0;
			int fullImpactNotDetected = 0;
			int fullNoImpactDetected = 0;
			int fullNoImpactNotDetected = 0;

			for (Mutation m : mutations) {
				if (m.getMutationResult() != null) {
					boolean detectedBySplit = detectedBySplit(split, m);
					if (detectedBySplit) {
						detectedBySplitCount++;
					} else {
						boolean detectedByFull = m.isKilled();
						boolean hasImpact = hasImpact(split, m);
						logger.info("Mutation: " + m);
						logger.info("Impact " + hasImpact);
						if (detectedByFull && hasImpact) {
							fullImpactDetected++;
						} else if (!detectedByFull && hasImpact) {
							fullImpactNotDetected++;
						} else if (detectedByFull && !hasImpact) {
							fullNoImpactDetected++;
						} else if (!detectedByFull && !hasImpact) {
							fullNoImpactNotDetected++;
						}
					}
				}
			}
			sb.append("Split: " + split + " \n");
			sb
					.append("Mutations detected only by remaing split with impact: "
							+ fullImpactDetected
							+ " ("
							+ AnalyzeUtil.formatPercent(fullImpactDetected,
									fullImpactDetected + fullImpactNotDetected)
							+ ")\n");

			sb
					.append("Mutations detected only by remaing split without impact: "
							+ fullNoImpactDetected
							+ " ("
							+ AnalyzeUtil.formatPercent(fullNoImpactDetected,
									fullNoImpactDetected
											+ fullNoImpactNotDetected) + ")\n");

			allDetectedBySplitCount += detectedBySplitCount;
			allFullImpactDetected += fullImpactDetected;
			allFullImpactNotDetected += fullImpactNotDetected;
			allFullNoImpactDetected += fullNoImpactDetected;
			allFullNoImpactNotDetected += fullNoImpactNotDetected;

		}
		sb.append("\nSummary:\n");

		sb
				.append(String
						.format(
								"Mutations detected only by remaing split. Total:  %d On Average: %.2f Percent of Impact mutations detected by full TS: %s\n",
								allFullImpactDetected + allFullNoImpactDetected,

								((allFullImpactDetected + allFullNoImpactDetected) * 1.)
										/ splits.size(),
								AnalyzeUtil.formatPercent(allFullImpactDetected
										+ allFullNoImpactDetected,
										allFullImpactDetected
												+ allFullImpactNotDetected
												+ allFullNoImpactDetected
												+ allFullNoImpactNotDetected)));

		sb
				.append(String
						.format(
								"Mutations detected only by remaing split with impact. Total:  %d On Average: %.2f Percent of Impact mutations detected by full TS: %s \n",
								allFullImpactDetected,
								(allFullImpactDetected * 1.) / splits.size(),
								AnalyzeUtil.formatPercent(
										allFullImpactDetected,
										allFullImpactDetected
												+ allFullImpactNotDetected)));

		sb
				.append(String
						.format(
								"Mutations detected only by remaing split without impact. Total:  %d On Average: %.2f Percent of Non impact mutations detected by full TS: %s\n ",
								allFullNoImpactDetected,
								(allFullNoImpactDetected * 1.) / splits.size(),
								AnalyzeUtil.formatPercent(
										allFullNoImpactDetected,
										allFullNoImpactDetected
												+ allFullNoImpactNotDetected)));

		return sb.toString();
	}

	private boolean detectedBySplit(Set<String> split, Mutation m) {
		MutationTestResult result = m.getMutationResult();
		Collection<TestMessage> failures = result.getFailures();
		for (TestMessage tm : failures) {
			if (split.contains(tm.getTestCaseName())) {
				return true;
			}
		}
		Collection<TestMessage> errors = result.getErrors();
		for (TestMessage tm : errors) {
			if (split.contains(tm.getTestCaseName())) {
				return true;
			}
		}
		return false;
	}

	private boolean hasImpact(Set<String> split, Mutation m) {
		if (originalLineData == null) {
			originalLineData = CoverageTraceUtil.loadLineCoverageTrace("0");
		}
		Map<String, Map<String, Map<Integer, Integer>>> mutationLineCoverageData = CoverageTraceUtil
				.loadLineCoverageTrace(m.getId() + "");
		split = reduceSplit(split, mutationLineCoverageData);
		Map<String, Map<Integer, Integer>> originalData = getSplitData(
				originalLineData, split);
		Map<String, Map<Integer, Integer>> mutationData = getSplitData(
				mutationLineCoverageData, split);
		logger.info("Original Data " + originalData);
		logger.info("Mutation Data " + mutationData);
		int differentMethods = CoverageTraceUtil.getDifferentMethods(
				originalData, mutationData).size();
		return differentMethods > 0;
	}

	private Set<String> reduceSplit(
			Set<String> split,
			Map<String, Map<String, Map<Integer, Integer>>> mutationLineCoverageData) {
		Set<String> result = new HashSet<String>();
		for (String test : mutationLineCoverageData.keySet()) {
			if (mutationLineCoverageData.containsKey(test)) {
				result.add(test);
			}
		}
		return result;
	}

	private Map<String, Map<Integer, Integer>> getSplitData(
			Map<String, Map<String, Map<Integer, Integer>>> lineCoverageData,
			Set<String> split) {
		Map<String, Map<Integer, Integer>> result = new HashMap<String, Map<Integer, Integer>>();
		for (String testName : split) {
			if (lineCoverageData.containsKey(testName)) {
				Map<String, Map<Integer, Integer>> map = lineCoverageData
						.get(testName);
				addData(result, map);
			}
		}
		return result;
	}

	private void addData(Map<String, Map<Integer, Integer>> result,
			Map<String, Map<Integer, Integer>> map) {
		for (String key : map.keySet()) {
			if (result.containsKey(key)) {
				addDataLines(result.get(key), map.get(key));
			} else {
				result.put(key, getCopy(map.get(key)));
			}
		}

	}

	private Map<Integer, Integer> getCopy(Map<Integer, Integer> map) {
		Map<Integer, Integer> copy = new HashMap<Integer, Integer>();
		Set<Entry<Integer, Integer>> entrySet = map.entrySet();
		for (Entry<Integer, Integer> entry : entrySet) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	private void addDataMethod(Map<String, Map<Integer, Integer>> result,
			Map<String, Map<Integer, Integer>> map) {
		for (String key : map.keySet()) {
			if (map.containsKey(key)) {
				addDataLines(result.get(key), map.get(key));
			} else {
				result.put(key, map.get(key));
			}
		}

	}

	private void addDataLines(Map<Integer, Integer> result,
			Map<Integer, Integer> map) {
		for (Integer key : map.keySet()) {
			int value = 0;
			if (result.containsKey(key)) {
				value = result.get(key);
			}
			value += map.get(key);
			result.put(key, value);
		}
	}


	private static void makeSplits(List<String> all, int index,
			Set<String> split, int targetSize, Set<Set<String>> result) {
		if (split.size() >= targetSize) {
			result.add(split);
			return;
		}
		if (index >= all.size()) {
			return;
		}
		Set<String> newSplit = new HashSet<String>(split);
		newSplit.add(all.get(index));
		makeSplits(all, index + 1, newSplit, targetSize, result);
		makeSplits(all, index + 1, split, targetSize, result);
	}

	private Set<Set<String>> computeSplits(Set<String> allTests) {
		int size = allTests.size();
		List<String> allTestList = new ArrayList<String>(allTests);
		Set<String> split = new HashSet<String>();
		int splitSize = size / 2;
		Set<Set<String>> result = new HashSet<Set<String>>();
		logger.info("Size of split " + splitSize + " allTests size"
				+ allTests.size());
		makeSplits(allTestList, 0, new HashSet<String>(), splitSize, result);
		logger.info("Splits:  " + result);
		// result.add(split);
		// result.add(split2);
		return result;
	}

	private void calculateSplits(Set<String> allTests, Mutation m) {

	}

}
