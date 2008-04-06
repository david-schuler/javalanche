package org.softevo.mutation.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MutationProperties {

	private static Logger logger = Logger.getLogger(MutationProperties.class);

	public static final String PROPERTIES_FILE = "mutation.incl.properties";

	public static final Properties PROPERTIES = getProperties();

	private static Properties getProperties() {
		Properties properties = new Properties();
		InputStream is = MutationProperties.class.getClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (properties != null) {
			System.out.println(properties.keySet());
		}
		if (properties == null) {
			logger.warn("Could not read properties file:  " + PROPERTIES_FILE);
		}
		return properties;
	}

	public static final String OUTPUT_DIR = PROPERTIES
			.getProperty("mutation.output.dir");

	public static final String ASPECTJ_DIR = "/scratch/schuler/aspectJ/";

	public static final String CLOVER_REPORT_DIR = OUTPUT_DIR + "clover_html/";

	public static final String MUTATIONS_TO_APPLY_FILE = OUTPUT_DIR
			+ "/mutations-to-apply.xml";

	public static final String CLOVER_RESULTS_FILE = OUTPUT_DIR
			+ "/clover-coverage-results.xml";

	// public static final String TESTS_TO_EXECUTE_FILE = OUTPUT_DIR
	// + "/tests-to-execute.txt";

	public static final String MUTATION_RESULT_FILE = OUTPUT_DIR
			+ "/mutation-results.txt";

	public static final String[] TEST_CLASSES_TO_INSTRUMENT = { "org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps" };

	/*
	 * Different run modes.
	 */

	public enum RunMode {
		SCAN("scan"), MUTAION_TEST("mutation"), TEST_TESTSUIT_FIRST("test1"), TEST_TESTSUITE_SECOND(
				"test2");

		private String key;

		RunMode(String key) {
			this.key = key;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

	}

	private static final String RUN_MODE_KEY = "mutation.run.mode";

	public static final RunMode RUN_MODE = getRunMode();

	public static final String RESULT_FILE_KEY = "mutation.result.file";

	public static final String MUTATION_FILE_KEY = "mutation.file";

	public static final String MUTATION_TEST_DEBUG_KEY = "mutation.test.debug";

	public static final boolean DEBUG = true;

	public static final String DEBUG_PORT_KEY = "mutation.debug.port";

	// private static boolean getDebug() {
	// String debugProperty = System.getProperty(MUTATION_TEST_DEBUG_KEY);
	// if (debugProperty != null && !debugProperty.equals("false")) {
	// logger.info("Debugging enabled");
	// return true;
	// }
	// logger.info("Debugging not enabled");
	// return false;
	// }

	public static final String RESULT_DIR = OUTPUT_DIR + "result/";

	/**
	 * Directory the serialized files are stored.
	 */
	public static final String RESULT_OBJECTS_DIR = OUTPUT_DIR + "objects/";

	public static final String MUTATIONS_CLASS_RESULT_XML = "mutations-class-result.xml";

	/**
	 *
	 * The key for the system property that specifies the package prefix of the
	 * project to mutate.
	 *
	 * -dmutation.package.prefix=org.aspectj
	 */
	public static final String PROJECT_PREFIX_KEY = "mutation.package.prefix";

	public static final String PROJECT_PREFIX = getPrefix();

	/**
	 *
	 * The key for the system property that specifies the the testsuite which
	 * should be modified
	 *
	 * -dmutation.test.suite=AllTests
	 */
	public static final String TEST_SUITE_KEY = "mutation.test.suite";

	public static final String TEST_SUITE = getProperty(TEST_SUITE_KEY);

	/**
	 * The key for the system property that specifies if there is coverage
	 * information in the db.
	 *
	 * -dmutation.coverage.information=false
	 *
	 */
	public static final String COVERAG_INFORMATION_KEY = "mutation.coverage.information";

	/**
	 * True if coverage information is available in the db.
	 */
	public static final boolean COVERAGE_INFFORMATION = getCoverage();

	/**
	 * Directory where the processes are executed
	 */
	public static final String EXEC_DIR = ".";

	public static final String TESTCASES_FILE = OUTPUT_DIR + "/testCases.xml";

	public static final String ACTUAL_MUTATION_KEY = "mutation.actual.mutation";

	public static final String NOT_MUTATED = "notMutated";

	public static final boolean OBSERVE_OBJECTS = false; // TODO read from
	// property

	public static final String NUMBER_OF_THREADS_KEY = "mutation.number.of.threads";

	public static final int NUMBER_OF_THREADS = getPropertyOrDefault(
			NUMBER_OF_THREADS_KEY, 2);

	public static final String MAX_TIME_FOR_SUB_PROCESS_KEY = "mutation.max.time.for.subprocess";

	public static final long MAX_TIME_FOR_SUB_PROCESS = getPropertyOrDefault(
			MAX_TIME_FOR_SUB_PROCESS_KEY, 30 * 60 * 1000);

	public static final String SCRIPT_COMMAND_KEY = "mutation.script.command";

	public static final String SCRIPT_COMMAND = getProperty(SCRIPT_COMMAND_KEY);

	public static final String TEST_TESTSUITE_KEY = "mutation.test.testsuite";

	public static final String TEST_TESTSUITE = getProperty(TEST_TESTSUITE_KEY);

	public static final String USE_EXTERNAL_COVERAGE_DATA_KEY = "mutation.external.coverage.data";

	public static final boolean USE_EXTERNAL_COVERAGE_DATA = getPropertyOrDefault(
			USE_EXTERNAL_COVERAGE_DATA_KEY, false);

	private static final int getPropertyOrDefault(String key, int defaultValue) {
		String result = getPropertyOrDefault(key, defaultValue + "");
		return Integer.parseInt(result);
	}

	private static boolean getPropertyOrDefault(String key, boolean b) {
		String property = getProperty(key);
		if (property == null) {
			return b;
		} else {
			String propertyTrimmed = property.trim().toLowerCase();
			if (propertyTrimmed.equals("true") || propertyTrimmed.equals("yes")) {
				return true;
			}
		}
		return false;
	}

	private static RunMode getRunMode() {
		String runModeString = getProperty(RUN_MODE_KEY);
		if (runModeString != null) {
			runModeString = runModeString.toLowerCase();
			for (RunMode runMode : RunMode.values()) {
				if (runMode.getKey().equals(runModeString)) {
					return runMode;
				}
			}
		}
		return RunMode.MUTAION_TEST;
	}

	private static final String getPropertyOrDefault(String key,
			String defaultValue) {
		String result = getProperty(key);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	private static String getProperty(String key) {
		String result = null;
		if (System.getProperty(key) != null) {
			result = System.getProperty(key);
		}
		// no else if property may also be null
		if (result == null && PROPERTIES.containsKey(key)) {
			result = PROPERTIES.getProperty(key);
		}
		return result;
	}

	private static boolean getCoverage() {
		String coverageInformation = System
				.getProperty(COVERAG_INFORMATION_KEY);
		boolean result = false;
		if (coverageInformation != null
				&& coverageInformation.toLowerCase().equals("true")) {
			result = true;
		}
		return result;
	}

	private static String getPrefix() {
		String project_prefix = System.getProperty(PROJECT_PREFIX_KEY);
		if (project_prefix == null) {
			logger.warn("No project prefix found (Property: "
					+ PROJECT_PREFIX_KEY + " not set)");
		}
		return project_prefix;
	}

}
