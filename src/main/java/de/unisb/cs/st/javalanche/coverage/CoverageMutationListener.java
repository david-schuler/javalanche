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
package de.unisb.cs.st.javalanche.coverage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * @author Bernhard Gruen
 * @author David Schuler
 * 
 */
public class CoverageMutationListener implements MutationTestListener {

	private static Logger logger = Logger
			.getLogger(CoverageMutationListener.class);

	private static boolean isPermuted = false;
	private static HashSet<String> seenTests = new HashSet<String>();

	private static Long mutation_id = new Long(-1);
	private String testName = null;

	private boolean saveFiles = false;

	// private static Map<String, Map<Integer, Integer>> classMap = new
	// ConcurrentHashMap<String, Map<Integer, Integer>>();

	public static AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>> classMapRef = new AtomicReference<ConcurrentMap<String, ConcurrentMap<Integer, Integer>>>(
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
		return classMapRef.get();
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

		if (MutationProperties.RUN_MODE == RunMode.TEST_PERMUTED
				|| MutationProperties.RUN_MODE == RunMode.CREATE_COVERAGE) {
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
		classMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = true;
	}

	public void end() {
		writeProfilingData();
		InstrumentExclude.save();
		classMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = false;
	}

	public void testStart(String testName) {
		this.testName = testName;
		if (isPermuted) {
			if (seenTests.contains(testName)) {
				seenTests.clear();
				long i = 1;
				File dir = new File(CoverageProperties.TRACE_RESULT_DATA_DIR
						+ CoverageProperties.PERMUTED_PREFIX + i);
				while (dir.exists()) {
					i++;
					dir = new File(CoverageProperties.TRACE_RESULT_DATA_DIR
							+ CoverageProperties.PERMUTED_PREFIX + i);
				}
				mutation_id = -i;
			}
			seenTests.add(testName);
		}
		classMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = true;
	}

	public void testEnd(String testName) {
		createMutationDir();

		Tracer.getInstance().deactivateTrace();
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> classMap = classMapRef
				.get();
		classMapRef
				.set(new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());
		serializeHashMap(classMap, getLineCoverageFileName());
		classMapRef.get().clear();
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> valueMap = valueMapRef
				.get();
		valueMapRef
				.set(new ConcurrentHashMap<String, ConcurrentMap<Integer, Integer>>());
		serializeHashMap(valueMap, getDataCoverageFileName());
		valueMapRef.get().clear();
		Tracer.getInstance().activateTrace();

		saveFiles = false;
	}

	public void mutationStart(Mutation mutation) {
		mutation_id = mutation.getId();
		classMapRef.get().clear();
		valueMapRef.get().clear();
		createMutationDir();
		saveFiles = true;
	}

	public void mutationEnd(Mutation mutation) {
		// serializeIdMap(mutation_id);
		classMapRef.get().clear();
		valueMapRef.get().clear();
		saveFiles = false;
	}

	private void writeProfilingData() {
		if (mutation_id != 0) {
			return;
		}
		XmlIo.toXML(profilerMap, CoverageProperties.TRACE_PROFILER_FILE);
	}

	public void serializeHashMap(
			ConcurrentMap<String, ConcurrentMap<Integer, Integer>> classMap,
			String fileName) {
		int numClasses = classMap.size();
		if (numClasses == 0 && CoverageProperties.TRACE_LINES) {
			logger.info("Empty coverage map for test " + testName);
		}
		
		int countClasses = 0;
		ObjectOutputStream oos = null;
		String exceptionMessage = "Could not write " + fileName;
		try {
			oos = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(fileName)));
			oos.writeInt(numClasses);
			for (String s : classMap.keySet()) {
				countClasses++;
				oos.writeUTF(s);
				Map<Integer, Integer> lineMap = classMap.get(s);
				int lineMapSize = lineMap.size();
				oos.writeInt(lineMapSize);
				int countLines = 0;
				for (Entry<Integer, Integer> entry : lineMap.entrySet()) {
					countLines++;
					oos.writeInt(entry.getKey().intValue());
					oos.writeInt(entry.getValue().intValue());
				}
				if (countLines != lineMapSize) {
					logger.warn("Different sizes" + lineMapSize + "  "
							+ countLines + " " + lineMap);
					oos.flush();
					oos.close();
					serializeHashMap(classMap, fileName);
				}
			}
		} catch (IOException e) {
			logger.warn(exceptionMessage, e);
			throw new RuntimeException(exceptionMessage, e);
		} finally {
			if (oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
					logger.warn(exceptionMessage, e);
					throw new RuntimeException(exceptionMessage, e);
				}
			}
		}
		if (countClasses != numClasses) {
			logger.warn("Different number of total classes (Writing again) "
					+ countClasses + " " + numClasses + " " + classMap);
			serializeHashMap(classMap, fileName);
		}
	}

	private String getLineCoverageFileName() {
		String sanitizedName = sanitize(testName);
		String fileName = CoverageProperties.TRACE_RESULT_LINE_DIR
				+ getMutationIdFileName() + "/" + sanitizedName + ".gz";
		return fileName;
	}

	private static String sanitize(String name) {
		String result = name.replace(' ', '_');
		result = result.replace('/', '-');
		return result;
	}

	private void XserializeValueMap(
			Map<String, Map<Integer, Integer>> valueMapCopy) {
		if (!saveFiles) {
			logger.warn("Double Call to serializeValueMap");
			return;
		}

		if (valueMapCopy.size() == 0 && CoverageProperties.TRACE_RETURNS) {
			logger.info("Empty value map for test " + testName);
		}
		ObjectOutputStream oos = null;

		String fileName = getDataCoverageFileName();
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(fileName))));
			Map<Integer, Integer> lineMap = new HashMap<Integer, Integer>();

			Set<String> ks = valueMapCopy.keySet();
			Iterator<String> it = ks.iterator();
			String s;
			Set<Integer> ks2;
			Iterator<Integer> it2;
			Integer i;

			oos.writeInt(valueMapCopy.size());
			// System.out.print(testName+":");
			while (it.hasNext()) {
				s = it.next();
				oos.writeUTF(s);
				lineMap = valueMapCopy.get(s);
				ks2 = lineMap.keySet();
				it2 = ks2.iterator();
				oos.writeInt(lineMap.size());
				// System.out.println(s);
				while (it2.hasNext()) {
					i = it2.next();
					oos.writeInt(i);
					oos.writeInt(lineMap.get(i));
					// System.out.println(i);
				}
			}
		} catch (Exception e) {
			String message = "Exception while serializing. " + testName;
			logger.warn(message, e);
			// e.printStackTrace();
			throw new RuntimeException(testName, e);

		} finally {
			if (oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
					logger.warn(e);
					throw new RuntimeException("Could not write " + fileName, e);
				}
			}
		}
	}

	private String getDataCoverageFileName() {
		String sanitizedName = sanitize(testName);
		String fileName = CoverageProperties.TRACE_RESULT_DATA_DIR
				+ getMutationIdFileName() + "/" + sanitizedName + ".gz";
		return fileName;
	}
}
