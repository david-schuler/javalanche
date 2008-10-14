package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses.Arithmetic;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses.ArithmeticTest;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.SelectiveTestSuite;

public class ArithmeticReplaceTest {

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses.Arithmetic";
		ByteCodeTestUtils.doSetup(classname, new ArithmeticReplaceCollectorTransformer(null));
	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = Arithmetic.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	// private static final String TEST_CLASS_NAME =
	// "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses.Arithmetic";

	private static final String UNITTEST_CLASS_TYPE = ArithmeticTest.class
			.getName();

//	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
//			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_TYPE, 5);

	private static final int[] linenumbers = { 6, 10, 14, 19, 24 };

	@Before
	public void setup() {
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		ByteCodeTestUtils.redefineMutations(TEST_CLASS_NAME);
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(ArithmeticTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		Arithmetic a = new Arithmetic();
		selectiveTestSuite.run(new TestResult());
		ByteCodeTestUtils.testResults(TEST_CLASS_NAME);
	}

}
