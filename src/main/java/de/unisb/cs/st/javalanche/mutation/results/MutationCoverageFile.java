package de.unisb.cs.st.javalanche.mutation.results;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class MutationCoverageFile {

	private static Logger logger = Logger.getLogger(MutationCoverageFile.class);

	private static final File COVERAGE_DIR = new File(
			MutationProperties.OUTPUT_DIR + "/coverage-data/");

	private static BiMap<String, Integer> idMap;

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
			}
			SerializeIo.serializeToFile(ids, new File(COVERAGE_DIR, ""
					+ entry.getKey()));
		}
		logger.info("Saving Ids of Covered Mutations "
				+ coveredMutations.size());
		SerializeIo.serializeToFile(coveredMutations, COVERED_FILE);

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
		BiMap<String, Integer> result = new HashBiMap<String, Integer>();
		for (Set<String> tests : values) {
			for (String test : tests) {
				if (!result.containsKey(test)) {
					result.put(test, key++);
				}
			}
		}
		return result;
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
}
