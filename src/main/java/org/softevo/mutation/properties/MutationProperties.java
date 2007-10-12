package org.softevo.mutation.properties;

public interface MutationProperties {

	static final String ASPECTJ_DIR = "/scratch/schuler/aspectJ";

	static final String SAMPLE_FILE_CLASS_NAME = "org.aspectj.weaver.Advice";

	static final String SAMPLE_FILE = ASPECTJ_DIR + "/weaver/bin/"
			+ SAMPLE_FILE_CLASS_NAME.replace('.', '/') + ".class";

	static final String CONFIG_DIR = "/scratch/schuler/mutation-test-config";

	static final String CLOVER_REPORT_DIR = CONFIG_DIR +  "/clover_html";

	static final String TEST_FILE = CONFIG_DIR + "/selected-tests.txt";

	static final String MUTATIONS_TO_APPLY_FILE = CONFIG_DIR
			+ "/mutations-to-apply.xml";

	static final String CLOVER_RESULTS_FILE = CONFIG_DIR
			+ "/clover-coverage-results.xml";

	static final String TESTS_TO_EXECUTE_FILE = CONFIG_DIR
			+ "/tests-to-execute.txt";

	static final String MUTATION_RESULT_FILE = CONFIG_DIR
			+ "/mutation-results.txt";

	static final String[] TEST_CLASSES_TO_INSTRUMENT = { "org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps" };

	static final String SCAN_FOR_MUTATIONS = "scan";
}
