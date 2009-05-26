package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit;

import static org.junit.Assert.*;

import java.util.Enumeration;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsCollectorTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.testclasses.SysExit;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.testclasses.SysExitTest;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class RemoveSystemExitTest {

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.testclasses.SysExit";
		ByteCodeTestUtils.deleteMutations(classname);
		ByteCodeTestUtils.generateTestDataInDB(System.getProperty("user.dir")
				+ "/target/classes/" + classname.replace('.', '/') + ".class",
				new NegateJumpsCollectorTransformer(null));
	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = SysExit.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = SysExitTest.class
			.getName();

	// private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
	// .getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 1);

	private static final int[] linenumbers = { 6, 12, 13 };

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
		TestSuite suite = new TestSuite(SysExitTest.class);
		TestResult testResult = new TestResult();
		suite.run(testResult);
		Enumeration<junit.framework.Test> tests = suite.tests();
		while (tests.hasMoreElements()) {
			System.out.println(tests.nextElement());
		}
		assertEquals(testResult.runCount(), suite.countTestCases());
		System.out.println("TEST PASSED");
	}

}
