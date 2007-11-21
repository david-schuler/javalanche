package org.softevo.mutation.bytecodeMutations.sysexit;

import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.bytecodeMutations.ByteCodeTestUtils;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstantsTest;
import org.softevo.mutation.bytecodeMutations.sysexit.testclasses.SysExit;
import org.softevo.mutation.bytecodeMutations.sysexit.testclasses.SysExitTest;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.runtime.SelectiveTestSuite;

public class TestRemoveSystemExit {

	static {
		ByteCodeTestUtils.redefineMutations("org.softevo.mutation.bytecodeMutations.sysexit.testclasses.SysExit");
//		MutationManager.setApplyAllMutation(true);
	}

	private static final Class TEST_CLASS = SysExit.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = SysExitTest.class
			.getName();

	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 1);

	private static final int[] linenumbers = { 6, 12, 13};

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
		MutationPossibilityCollector.generateTestDataInDB(TEST_CLASS_FILENAME);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(IntegerConstantsTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		SysExit loadClass = new SysExit();
		selectiveTestSuite.run(new TestResult());
		// testResults(TEST_CLASS_NAME);
	}

	/**
	 * Tests if exactly one testMethod failed because of the mutation.
	 *
	 * @param testClassName
	 *            The class that test the mutated class.
	 */
	@SuppressWarnings("unchecked")
	private static void testResults(String testClassName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		int nonNulls = 0;
		for (Mutation m : mList) {
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				if (m.getLineNumber() != 16 && m.getLineNumber() != 18) {
					Assert.assertEquals("Mutation: " + m, 1, singleTestResult
							.getNumberOfErrors()
							+ singleTestResult.getNumberOfFailures());
				}
			} else {
				System.out.println(m);
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected failing tests because of mutations",
				nonNulls >= mList.size());
	}

}
