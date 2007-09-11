package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.File;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.ByteCodeTestUtils;
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps;
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.TestJump;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.testsuite.SelectiveTestSuite;

public class NegateJumpsTest {

	private static final Class TEST_CLASS = Jumps.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = TestJump.class
			.getName();

	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 4);

	private static final int[] linenumbers = { 6, 14, 16, 25, 34, 37 };

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateTestDataInDB(TEST_CLASS_FILENAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME,testCaseNames,linenumbers);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(TestJump.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		Jumps jumps = new Jumps();
		selectiveTestSuite.run(new TestResult());
	}

}
