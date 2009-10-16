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
package de.unisb.cs.st.javalanche.coverage.experiment;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.coverage.CoverageTraceUtil;
import de.unisb.cs.st.javalanche.coverage.distance.DistanceGraph;
import de.unisb.cs.st.javalanche.coverage.distance.MethodDescription;
import de.unisb.cs.st.javalanche.coverage.distance.MethodDistances;
import de.unisb.cs.st.javalanche.mutation.analyze.ManualAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

// 	 -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.coverage.experiment.ManualResultsAnalyzer
public class ManualResultsAnalyzer implements MutationAnalyzer {

	public static final Comparator<Mutation> MUTATION_COMP = new Comparator<Mutation>() {

		public int compare(Mutation o1, Mutation o2) {
			int comp = o1.getClassName().compareTo(o2.getClassName());
			if (comp != 0) {
				return comp;
			}
			comp = o1.getLineNumber() - o2.getLineNumber();
			if (comp != 0) {
				return comp;
			}
			comp = o1.getMutationForLine() - o2.getMutationForLine();
			if (comp != 0) {
				return comp;
			}
			return 0;
		}

	};

	private static Logger logger = Logger
			.getLogger(ManualResultsAnalyzer.class);

	private Map<String, Map<String, Map<Integer, Integer>>> originalLineData;
	private Map<String, Map<String, Map<Integer, Integer>>> originalReturnData;

	private int lineLimit = 0;

	private int returnLimit = 0;

	private MethodDistances md;

	private static Map<String, Map<String, Map<Integer, Integer>>> lineCache;

	public ManualResultsAnalyzer() {
		g = DistanceGraph.getDefault();
	}

	public static void main(String[] args) {
		new ManualResultsAnalyzer().checkManualResults();
	}

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		return checkManualResults();
	}

	private String checkManualResults() {
		Map<Mutation, Boolean> mutationMap = ManualClassifications
				.getManualClassification();
		StringBuilder result = new StringBuilder();
		int right = 0;
		int wrong = 0;
		Set<Mutation> keySet = new TreeSet<Mutation>(MUTATION_COMP);
		// System.out.println("MAP SIZE " + mutationMap.size());
		keySet.addAll(mutationMap.keySet());
		getMethodNames(mutationMap);
		Map<String, Map<String, Map<Integer, Integer>>> lineCoverage = getOriginalLineCoverage();
		StringBuilder csv = new StringBuilder();

		for (Mutation m : keySet) {

			Mutation mutationFromDb = QueryManager.getMutationOrNull(m);
			logger
					.info("Analyzing  "
							+ m.toShortString()
							+ (mutationFromDb != null ? " Found in db"
									: " Not found "));
			// if (md == null) {
			// logger.warn("Not analyzing distance");
			// }

			if (mutationFromDb != null) {
				Long id = mutationFromDb.getId();
				String fullMethodName = getMethodName(lineCoverage, m
						.getClassName(), m.getLineNumber());
				String methodName = fullMethodName.substring(fullMethodName
						.indexOf('@') + 1);

				Collection<String> lineImpactMethods = getLineImpact(id);
				lineImpactMethods.remove(fullMethodName);
				int lineImpact = lineImpactMethods.size();
				Collection<String> returnImpactMethods = getReturnImpact(id);
				returnImpactMethods.remove(fullMethodName);
				int returnImpact = returnImpactMethods.size();

				int lineDistance = g == null ? 0 : getMaxDistance(
						mutationFromDb, lineImpactMethods);

				int returnDistance = g == null ? 0 : getMaxDistance(
						mutationFromDb, returnImpactMethods);

				Set<String> allImpactMethods = new HashSet<String>();
				allImpactMethods.addAll(lineImpactMethods);
				allImpactMethods.addAll(returnImpactMethods);
				int allImpactDistance = g == null ? 0 : getMaxDistance(
						mutationFromDb, allImpactMethods);

				csv.append(mutationFromDb.getId());
				csv.append(',');
				csv.append(mutationFromDb.getClassName());
				csv.append(',');
				csv.append(mutationFromDb.getLineNumber());
				csv.append(',');
				csv.append(methodName);
				csv.append(',');
				csv.append(mutationFromDb.getMutationForLine());
				csv.append(',');
				csv.append(mutationFromDb.getMutationType());
				csv.append(',');
				csv.append(mutationMap.get(m) ? "Non Equivalent "
						: "Equivalent mutation ");
				csv.append(',');
				csv.append(lineImpact);
				csv.append(',');
				csv.append(returnImpact);
				csv.append(',');
				csv.append(allImpactMethods.size());
				csv.append(',');
				csv.append(lineDistance);
				csv.append(',');
				csv.append(returnDistance);
				csv.append(',');
				csv.append(allImpactDistance);
				csv.append(',');
				csv.append('"');
				csv.append(lineImpactMethods);
				csv.append('"');
				csv.append(',');
				csv.append('"');
				csv.append(returnImpactMethods);
				csv.append('"');
				csv.append('\n');

				StringBuilder sb = new StringBuilder();
				sb.append(mutationMap.get(m) ? "Non Equivalent "
						: "Equivalent mutation ");
				sb.append(mutationFromDb.getId());
				sb.append("  ");
				sb.append(m);
				sb.append("\nLine Impact: ");
				sb.append(lineImpact);
				sb.append("   ");
				sb.append(lineImpactMethods);
				sb.append("\nReturn Impact: ");
				sb.append(returnImpact);
				sb.append("   ");
				sb.append(returnImpactMethods);
				sb.append("\n");
				result.append(sb);
				logger.info(sb.toString());

				boolean nonEquiv = mutationMap.get(m);

				if (returnImpact > returnLimit && lineImpact > lineLimit) {
					if (nonEquiv) {
						right++;
					} else {
						wrong++;
					}
				}

			} else {
				logger.warn("Mutation not found " + m);
			}
		}
		System.out.println("\n\n\n\n\n" + result);
		String classification = "Right: " + right + " Wrong: " + wrong;
		System.out.println("\n\n " + classification);
		Io.writeFile(csv.toString(), new File(
				"manual-classification-results.csv"));
		return result.toString();
	}

	private int getMaxDistanceOld(Mutation m, Collection<String> impactMethods) {
		Map<String, Map<String, Map<Integer, Integer>>> lineCoverage = getOriginalLineCoverage();
		String mutationMethodName = getMethodName(lineCoverage, m
				.getClassName(), m.getLineNumber());

		MethodDescription mutationMethodDesc = md
				.getMetodDesc(mutationMethodName);
		if (md == null) {
			logger.warn("Mutation method not found " + m.toShortString());
		}

		int maxDist = Integer.MIN_VALUE;
		int coundDEBUG = 0;
		for (String impactMethod : impactMethods) {
			coundDEBUG++;
			if (coundDEBUG > 20) {
				break;
			}
			MethodDescription impactMethodDesc = md.getMetodDesc(impactMethod);
			if (impactMethodDesc == null) {
				logger.warn("No Method desc for " + impactMethod);
			} else if (md != null) {
				int distance = md.getDistance(mutationMethodDesc,
						impactMethodDesc);
				if (distance > maxDist) {
					maxDist = distance;
				}
			} else {
				logger.warn("Impact method not found  " + impactMethod);
			}
		}
		logger.info("Max Distance for " + mutationMethodName + "  " + maxDist);
		if (maxDist < 0) {
			maxDist = 0;
		}
		return maxDist;
	}

	private static Map<String, Map<String, Map<Integer, Integer>>> getOriginalLineCoverage() {
		if (lineCache == null) {
			lineCache = CoverageTraceUtil.loadLineCoverageTrace("0");
		}
		return lineCache;
	}

	private int getMaxDistance(Mutation m, Collection<String> impactMethods) {
		Map<String, Map<String, Map<Integer, Integer>>> lineCoverage = getOriginalLineCoverage();
		String mutationMethodName = getMethodName(lineCoverage, m
				.getClassName(), m.getLineNumber());

		MethodDescription mutationMethodDesc = g
				.getMetodDesc(mutationMethodName);
		if (mutationMethodDesc == null) {
			logger.warn("Mutation method not found " + m.toShortString());
		}

		int maxDist = Integer.MIN_VALUE;
		int coundDEBUG = 0;
		for (String impactMethod : impactMethods) {
			coundDEBUG++;
			if (coundDEBUG > 20) {
				break;
			}
			MethodDescription impactMethodDesc = g.getMetodDesc(impactMethod);
			if (impactMethodDesc == null) {
				logger.warn("No Method desc for " + impactMethod);
			} else {
				int distance = g.getDistance(mutationMethodDesc,
						impactMethodDesc);
				if (distance > maxDist) {
					maxDist = distance;
				}
			}
		}
		logger.info("Max Distance for " + mutationMethodName + "  " + maxDist);
		if (maxDist < 0) {
			maxDist = 0;
		}
		return maxDist;
	}

	private static final Map<String, String> defaults = new HashMap<String, String>();

	private DistanceGraph g;
	static {
		defaults.put("de.susebox.java.lang.ThrowableMessageFormatter106",
				"getMessage");
		defaults.put("de.susebox.java.lang.ThrowableMessageFormatter105",
				"getMessage");
		defaults.put("xorg.apache.commons.lang.RandomStringUtils273", "random");
		defaults
				.put("net.sourceforge.barbecue.output.SizingOutput46", "<init>");
		defaults.put("xcom.thoughtworks.xstream.io.xml.PrettyPrintWriter276",
				"writeText");
		defaults.put("org.joda.time.DateTimeUtils347", "getDateFormatSymbols");
		defaults.put("org.joda.time.tz.CachedDateTimeZone148", "getInfo");
	}

	static String getMethodName(
			Map<String, Map<String, Map<Integer, Integer>>> lineCoverage,
			String className, int lineNumber) {
		String methodName;
		String key = className + lineNumber;
		logger.info(key);
		if (defaults.containsKey(key)) {
			methodName = className + '@' + defaults.get(key);
		} else {
			methodName = ManualAnalyzer.getFullMethodName(lineCoverage,
					className, lineNumber);
		}
		if (methodName.length() < 1) {
			logger.warn(" No method for " + key);
		}
		return methodName;
	}

	static void getMethodNames(Map<Mutation, Boolean> mutationMap) {

		Map<String, Map<String, Map<Integer, Integer>>> lineCoverage = getOriginalLineCoverage();

		for (Mutation m : mutationMap.keySet()) {
			String methodName;
			methodName = getMethodName(lineCoverage, m.getClassName(), m
					.getLineNumber());
			System.out.println("Method name for mutation " + m.toShortString()
					+ "   " + methodName);

		}
	}

	private Collection<String> getReturnImpact(Long id) {
		if (originalReturnData == null) {
			originalReturnData = CoverageTraceUtil.loadDataCoverageTrace("0");
		}
		Map<String, Map<String, Map<Integer, Integer>>> dataCoverageData = CoverageTraceUtil
				.loadDataCoverageTrace(id + "");
		Collection<String> diffMethods = CoverageTraceUtil
				.getDifferentReturnMethodsForTests(originalReturnData,
						dataCoverageData);
		return diffMethods;
	}

	private Collection<String> getLineImpact(Long id) {
		if (originalLineData == null) {
			originalLineData = CoverageTraceUtil.loadLineCoverageTrace("0");
		}
		Map<String, Map<String, Map<Integer, Integer>>> lineCoverageData = CoverageTraceUtil
				.loadLineCoverageTrace(id + "");
		Collection<String> diffMethods = CoverageTraceUtil
				.getDifferentMethodsForTests(originalLineData, lineCoverageData);
		return diffMethods;
	}
}
