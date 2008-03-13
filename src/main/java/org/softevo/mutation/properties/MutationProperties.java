package org.softevo.mutation.properties;

import org.apache.log4j.Logger;
import org.softevo.mutation.run.threaded.ThreadPool;

public class MutationProperties {

	private static Logger logger = Logger.getLogger(MutationProperties.class);

	public static final String ASPECTJ_DIR = "/scratch/schuler/aspectJ/";

	public static final String SAMPLE_FILE_CLASS_NAME = "org.aspectj.weaver.Advice";

	public static final String SAMPLE_FILE = ASPECTJ_DIR + "/weaver/bin/"
			+ SAMPLE_FILE_CLASS_NAME.replace('.', '/') + ".class";

	public static final String CONFIG_DIR = "/scratch/schuler/mutation-test-config/";

	public static final String CLOVER_REPORT_DIR = CONFIG_DIR + "clover_html/";

	public static final String MUTATIONS_TO_APPLY_FILE = CONFIG_DIR
			+ "/mutations-to-apply.xml";

	public static final String CLOVER_RESULTS_FILE = CONFIG_DIR
			+ "/clover-coverage-results.xml";

	public static final String TESTS_TO_EXECUTE_FILE = CONFIG_DIR
			+ "/tests-to-execute.txt";

	public static final String MUTATION_RESULT_FILE = CONFIG_DIR
			+ "/mutation-results.txt";

	public static final String[] TEST_CLASSES_TO_INSTRUMENT = { "org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps" };

	public static final String SCAN_FOR_MUTATIONS = "scan";

	public static final String RESULT_FILE_KEY = "mutation.result.file";

	public static final String MUTATION_FILE_KEY = "mutation.file";

	public static final String MUTATION_TEST_DEBUG_KEY = "mutation.test.debug";

	public static final boolean DEBUG = true;

	public static final String DEBUG_PORT_KEY = "mutation.debug.port";

//	private static boolean getDebug() {
//		String debugProperty = System.getProperty(MUTATION_TEST_DEBUG_KEY);
//		if (debugProperty != null && !debugProperty.equals("false")) {
//			logger.info("Debugging enabled");
//			return true;
//		}
//		logger.info("Debugging not enabled");
//		return false;
//	}

	public static final String RESULT_DIR = CONFIG_DIR + "result/";

	/**
	 * Directory the serialized files are stored.
	 */
	public static final String RESULT_OBJECTS_DIR = CONFIG_DIR + "objects/";

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

	public static final String SCRIPT_COMMAND_KEY = "mutation.script.command";

	/**
	 * Directory where the processes are executed
	 */
	public static final String EXEC_DIR = ".";

	public static final String TESTCASES_FILE = CONFIG_DIR + "/testCases.xml";

	public static final String ACTUAL_MUTATION_KEY = "mutation.actual.mutaiton";

	public static final String NOT_MUTATED = "notMutated";

	// "src/scripts/";

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
			ThreadPool.logger.warn("No project prefix found (Property: "
					+ PROJECT_PREFIX_KEY + " not set)");
		}
		return project_prefix;
	}

}
