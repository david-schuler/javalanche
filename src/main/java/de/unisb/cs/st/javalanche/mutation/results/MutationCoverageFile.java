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
package de.unisb.cs.st.javalanche.mutation.results;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class MutationCoverageFile {

	private static Logger logger = Logger.getLogger(MutationCoverageFile.class);

	private static final File COVERAGE_DIR = new File(
			MutationProperties.OUTPUT_DIR + "/coverage-data/");

	private static BiMap<String, Integer> idMap;

	private static Multimap<Long, Long> baseMutations = HashMultimap.create();

	private static final File FILE_MAP = new File(COVERAGE_DIR, "file-map.ser");

	private static final Set<String> EMPTY_SET = new HashSet<String>();

	private static final File COVERED_FILE = new File(COVERAGE_DIR,
			"covered-mutations.ser");

	private static Set<Long> coveredMutations = null;

	public static void saveCoverageData(Map<Long, Set<String>> coverageData) {
		COVERAGE_DIR.mkdirs();
		coveredMutations = new HashSet<Long>();
		BiMap<String, Integer> allTests = getAllTests(coverageData.values());
		SerializeIo.serializeToFile(allTests, FILE_MAP);

		Set<Entry<Long, Set<String>>> entrySet = coverageData.entrySet();
		for (Entry<Long, Set<String>> entry : entrySet) {
			Set<Integer> ids = new HashSet<Integer>();
			for (String testName : entry.getValue()) {
				if (testName != null) {
					ids.add(allTests.get(testName));
				}
			}
			if (ids.size() > 0) {
				coveredMutations.add(entry.getKey());
				Collection<Long> collection = baseMutations.get(entry.getKey());
				if (collection.size() > 0) {
					coveredMutations.addAll(collection);
				}
			}
			SerializeIo.serializeToFile(ids,
					new File(COVERAGE_DIR, "" + entry.getKey()));
		}
		logger.info("Saving Ids of Covered Mutations "
				+ coveredMutations.size());
		SerializeIo.serializeToFile(coveredMutations, COVERED_FILE);
	}

	public static Set<String> getCoverageData(Mutation m) {
		if (m.getBaseMutationId() != null) {
			return getCoverageDataId(m.getBaseMutationId());
		}
		return getCoverageDataId(m.getId());

	}

	public static Set<String> getCoverageDataId(long id) {
		if (idMap == null) {
			idMap = SerializeIo.get(FILE_MAP);
		}
		File f = new File(COVERAGE_DIR, "" + id);
		if (!f.exists()) {
			return EMPTY_SET;
		}
		BiMap<Integer, String> inverse = idMap.inverse();
		Set<Integer> ids = SerializeIo.get(f);
		Set<String> result = new HashSet<String>();
		for (Integer i : ids) {
			String string = inverse.get(i);
			if (string == null) {
				throw new RuntimeException("Got null for " + i + "\n" + idMap);
			}
			result.add(string);
		}
		return result;
	}

	private static BiMap<String, Integer> getAllTests(
			Collection<Set<String>> values) {
		int key = 0;
		BiMap<String, Integer> result = HashBiMap.create();
		for (Set<String> tests : values) {
			for (String test : tests) {
				if (!result.containsKey(test)) {
					result.put(test, key++);
				}
			}
		}
		return result;
	}

	public static void reset() {
		idMap = null;
		coveredMutations = null;
	}

	public static void copyCoverageData(long srcId, long destId) {
		File src = new File(COVERAGE_DIR, "" + srcId);
		File dest = new File(COVERAGE_DIR, "" + destId);
		try {
			FileUtils.copyFile(src, dest);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getNumberOfCoveredMutations() {
		// File[] files = COVERAGE_DIR.listFiles();
		// long count = 0;
		// File tmp;
		// long emptyFileSize = 0;
		// try {
		// tmp = File.createTempFile("empty", "ser");
		// tmp.deleteOnExit();
		// SerializeIo.serializeToFile(EMPTY_SET, tmp);
		// emptyFileSize = tmp.length();
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
		// for (File f : files) {
		// if (f.length() != emptyFileSize) {
		// count++;
		// }
		// }
		// return count;
		return getCoveredMutations() != null ? getCoveredMutations().size()
				: 0l;
	}

	public static Set<Long> getCoveredMutations() {
		if (coveredMutations == null) {
			if (COVERED_FILE.exists()) {
				coveredMutations = SerializeIo.get(COVERED_FILE);
			}
		}
		return coveredMutations;
	}

	public static void addCoveredMutations(Set<Long> add) {
		Set<Long> result = new HashSet<Long>(add);
		Set<Long> coveredMutations = getCoveredMutations();
		result.addAll(coveredMutations);
		SerializeIo.serializeToFile(result, COVERED_FILE);
		reset();
	}

	public static void deleteCoverageData() {
		COVERAGE_DIR.delete();
	}

	public static boolean isCovered(long id) {
		Set<Long> covered = getCoveredMutations();
		return covered != null && getCoveredMutations().contains(id);
	}

	public static void addDerivedMutation(long baseMutation,
			long derivedMutation) {
		baseMutations.put(baseMutation, derivedMutation);
		if (isCovered(baseMutation)) {
			coveredMutations.add(derivedMutation);
		}
	}

	public static void update() {
		SerializeIo.serializeToFile(coveredMutations, COVERED_FILE);
	}
}
