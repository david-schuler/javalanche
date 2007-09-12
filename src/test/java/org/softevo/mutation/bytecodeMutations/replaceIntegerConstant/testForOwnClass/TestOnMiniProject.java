package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testForOwnClass;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.bytecodeMutations.ByteCodeTestUtils;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testForOwnClass.ricProject.RicClass;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testForOwnClass.ricProject.RicClassTest;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.testsuite.SelectiveTestSuite;

public class TestOnMiniProject {


	private static final Class TEST_CLASS = RicClass.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = RicClassTest.class
			.getName();

	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 3);


	private static final int[] linenumbers = { 6, 11, 12, 16, 17 };

	public void generateMutations() {
		MutationPossibilityCollector.generateTestDataInDB(TEST_CLASS_FILENAME);
	}

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames, linenumbers);
		generateMutations();
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(RicClassTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		RicClass ric = new RicClass();
		selectiveTestSuite.run(new TestResult());
	}

}
