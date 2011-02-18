/*
 * Copyright (C) 2011 Saarland University
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

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * @author Bernhard Gruen
 * @author David Schuler
 * 
 */
public class CoverageMutationListener implements MutationTestListener {

	static Logger logger = Logger.getLogger(CoverageMutationListener.class);

	private static boolean isPermuted = false;
	private Set<String> seenTests = new HashSet<String>();

	private static Long mutation_id = new Long(-1);
	private String testName = null;

	private boolean saveFiles = false;

	private static int instanceCount = 0;

	// private static Map<String, Map<Integer, Integer>> classMap = new
	// ConcurrentHashMap<String, Map<Integer, Integer>>();

	public static AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>> lineMapRef = new AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>>(
			new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());

	// private static Map<String, Map<Integer, Integer>> valueMap = new
	// ConcurrentHashMap<String, Map<Integer, Integer>>();

	public static AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>> valueMapRef = new AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>>(
			new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());

	private static Map<String, Long> profilerMap = new ConcurrentHashMap<String, Long>();

	// private static HashMap<String, Integer> idMap = new HashMap<String,
	// Integer>();
	// private static int idMapMasterSize = 0;

	public static ConcurrentMap<String, ConcurrentMap<Integer, Integer>> getLineCoverageMap() {
		return lineMapRef.get();
	}

	public static ConcurrentMap<String, ConcurrentMap<Integer, Integer>> getValueMap() {
		return valueMapRef.get();
	}

	public static Long getMutationId() {
		return mutation_id;
	}

	public static String getMutationIdFileName() {
		if (mutation_id < 0) {
			return CoverageProperties.PERMUTED_PREFIX + Math.abs(mutation_id);
		} else {
			return mutation_id.toString();
		}
	}

	/*
	 * public static HashMap<String, Integer> getIdMap() { return idMap; }
	 */

	public CoverageMutationListener() {
		logger.info("Tracing lines: " + CoverageProperties.TRACE_LINES);
		logger.info("Tracing data: " + CoverageProperties.TRACE_RETURNS);
		File dir = new File(CoverageProperties.TRACE_RESULT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(CoverageProperties.TRACE_RESULT_DATA_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(CoverageProperties.TRACE_RESULT_LINE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		if (ConfigurationLocator.getJavalancheConfiguration().getRunMode() == RunMode.CREATE_COVERAGE_MULT) {
			isPermuted = true;
		}
	}

	private void createMutationDir() {
		File dir = new File(CoverageProperties.TRACE_RESULT_DATA_DIR
				+ getMutationIdFileName());
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(CoverageProperties.TRACE_RESULT_LINE_DIR
				+ getMutationIdFileName());
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void start() {
		mutation_id = new Long(0);
		lineMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = true;
	}

	public void end() {
		writeProfilingData();
		InstrumentExclude.save();
		lineMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = false;
	}

	public void testStart(String testName) {
		this.testName = testName;
		if (isPermuted) {
			if (seenTests.contains(testName)) {
				logger.info("New Permutation Detected for Test: " + testName);
				seenTests.clear();
				long i = 1;
				File lineDir = new File(
						CoverageProperties.TRACE_RESULT_LINE_DIR
								+ CoverageProperties.PERMUTED_PREFIX + i);
				File dataDir = new File(
						CoverageProperties.TRACE_RESULT_DATA_DIR
								+ CoverageProperties.PERMUTED_PREFIX + i);
				while (dataDir.exists() || lineDir.exists()) {
					i++;
					dataDir = new File(CoverageProperties.TRACE_RESULT_DATA_DIR
							+ CoverageProperties.PERMUTED_PREFIX + i);
					lineDir = new File(CoverageProperties.TRACE_RESULT_LINE_DIR
							+ CoverageProperties.PERMUTED_PREFIX + i);
				}
				mutation_id = -i;
			}
			seenTests.add(testName);
		}
		lineMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = true;
	}

	public void testEnd(String testName) {
		createMutationDir();
		Tracer.getInstance().deactivateTrace();
		if (CoverageProperties.TRACE_LINES) {
			logger.info("Writing line traces");
			ConcurrentMap<String, ConcurrentMap<Integer, Integer>> classMap = lineMapRef
					.get();
			lineMapRef
					.set(new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());
			CoverageTraceUtil.writeTrace(classMap, testName,
					getLineCoverageFileName(testName));
			lineMapRef.get().clear();
		}
		if (CoverageProperties.TRACE_RETURNS) {
			logger.info("Writing data traces");
			ConcurrentMap<String, ConcurrentMap<Integer, Integer>> valueMap = valueMapRef
					.get();
			valueMapRef
					.set(new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());
			CoverageTraceUtil.writeTrace(valueMap, testName,
					getDataCoverageFileName(testName));
			valueMapRef.get().clear();
		}

		Tracer.getInstance().activateTrace();

		saveFiles = false;
	}

	public void mutationStart(Mutation mutation) {
		mutation_id = mutation.getId();
		lineMapRef.get().clear();
		valueMapRef.get().clear();
		createMutationDir();
		saveFiles = true;
	}

	public void mutationEnd(Mutation mutation) {
		// serializeIdMap(mutation_id);
		lineMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = false;
	}

	private void writeProfilingData() {
		if (mutation_id != 0) {
			return;
		}
		XmlIo.toXML(profilerMap, CoverageProperties.TRACE_PROFILER_FILE);
	}

	static String sanitize(String name) {
		String result = name.replace(' ', '_');
		result = result.replace('/', '-');
		return result;
	}

	private static String getDataCoverageFileName(String testName) {
		String sanitizedName = sanitize(testName);
		String fileName = CoverageProperties.TRACE_RESULT_DATA_DIR
				+ getMutationIdFileName() + "/" + sanitizedName + ".gz";
		return fileName;
	}

	static String getLineCoverageFileName(String testName) {
		String sanitizedName = sanitize(testName);
		String fileName = CoverageProperties.TRACE_RESULT_LINE_DIR
				+ getMutationIdFileName() + "/" + sanitizedName + ".gz";
		return fileName;
	}
}
