package org.softevo.mutation.properties;

public interface MutationProperties {
	static final String ASPECTJ_DIR = "/scratch/schuler/aspectJ";

	static final String SAMPLE_FILE_NAME = "org.aspectj.weaver.Advice";

	static final String SAMPLE_FILE = ASPECTJ_DIR + "/weaver/bin/"
			+ SAMPLE_FILE_NAME.replace('.', '/') + ".class";

	static final String CLOVER_REPORT_DIR = "/scratch/schuler/aspectJClover/run-all-junit-tests/clover_html";

	static final String TEST_FILE = "/scratch/schuler/mutation-test-config/selected-tests.txt";

	static final String MUTATIONS_TO_APPLY_FILE = "/scratch/schuler/mutation-test-config/mutations-to-apply.xml";
}
