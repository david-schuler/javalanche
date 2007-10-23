package org.softevo.mutation.properties;

import org.apache.log4j.Logger;

public class MutationProperties {

	private static Logger logger = Logger.getLogger(MutationProperties.class);

	public static final String ASPECTJ_DIR = "/scratch/schuler/aspectJ";

	public static final String SAMPLE_FILE_CLASS_NAME = "org.aspectj.weaver.Advice";

	public static final String SAMPLE_FILE = ASPECTJ_DIR + "/weaver/bin/"
			+ SAMPLE_FILE_CLASS_NAME.replace('.', '/') + ".class";

	public static final String CONFIG_DIR = "/scratch/schuler/mutation-test-config";

	public static final String CLOVER_REPORT_DIR = CONFIG_DIR + "/clover_html";

	public static final String TEST_FILE = CONFIG_DIR + "/selected-tests.txt";

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

	public static final boolean DEBUG = getDebug();

	private static boolean getDebug() {
		String debugProperty = System.getProperty(MUTATION_TEST_DEBUG_KEY);
		if (debugProperty != null && !debugProperty.equals("false")) {
			logger.info("Debugging enabled");
			return true;
		}
		logger.info("Debugging not enabled");
		return false;
	}
}
