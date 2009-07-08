package de.unisb.cs.st.javalanche.mutation.testDetector;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class TestInfo {

	private static Logger logger = Logger.getLogger(TestInfo.class);

	private static Map<String, Integer> testNames;

	public static boolean isTest(String classNameWithDots) {
		if (testNames == null) {
			initTestNames();
		}
		String checkString = classNameWithDots;
		if (classNameWithDots.contains("$")) {
			checkString = classNameWithDots.substring(0, classNameWithDots
					.indexOf("$"));
		}
		logger.debug("Checking test: " + classNameWithDots + " Contained "
				+ testNames.containsKey(checkString));
		return testNames.containsKey(checkString);
	}

	private static void initTestNames() {
		File file = MutationProperties.TEST_MAP_FILE;
		if (file.exists()) {
			testNames = XmlIo.get(file);
		} else {
			throw new RuntimeException("Test name file does not exists " + file
					+ "\nConsider creating it with $> ant scanForTests");
		}
		if (testNames == null || testNames.isEmpty()) {
			throw new RuntimeException("Could not read map correctly. Map: "
					+ testNames);
		}
	}
}
