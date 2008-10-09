package de.st.cs.unisb.javalanche.bytecodeMutations.sysexit;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.st.cs.unisb.javalanche.bytecodeMutations.ByteCodeTestUtils;
import de.st.cs.unisb.javalanche.bytecodeMutations.negateJumps.NegateJumpsCollectorTransformer;
import de.st.cs.unisb.javalanche.bytecodeMutations.sysexit.testclasses.SysExit;
import de.st.cs.unisb.javalanche.bytecodeMutations.sysexit.testclasses.SysExitTest;
import de.st.cs.unisb.javalanche.runtime.testsuites.SelectiveTestSuite;

public class RemoveSystemExitTest {


	static {
		String classname = "de.st.cs.unisb.javalanche.bytecodeMutations.sysexit.testclasses.SysExit";
		ByteCodeTestUtils.deleteMutations(classname);
		ByteCodeTestUtils.generateTestDataInDB(System.getProperty("user.dir")
				+ "/target/classes/" + classname.replace('.', '/')
				+ ".class", new NegateJumpsCollectorTransformer(null));
	}


	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = SysExit.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = SysExitTest.class
			.getName();

//	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
//			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 1);

	private static final int[] linenumbers = { 6, 12, 13};

	@Before
	public void setup() {
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
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(SysExitTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		SysExit loadClass = new SysExit();
		selectiveTestSuite.run(new TestResult());
	}

}
