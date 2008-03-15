package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

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
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstants;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstantsTest;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.runtime.SelectiveTestSuite;

public class TestOnMiniProject {

	static {
		String classname = "org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstants";
		ByteCodeTestUtils.deleteMutations(classname);
		ByteCodeTestUtils.generateTestDataInDB(System.getProperty("user.dir")
				+ "/target/test-classes/" + classname.replace('.', '/')
				+ ".class", new RicCollectorTransformer(null));

	}

	private static final Class TEST_CLASS = IntegerConstants.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = IntegerConstantsTest.class
			.getName();

	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 3);

	private static final int[] linenumbers = { 6, 11, 12, 16, 17, 18 };

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
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
		ByteCodeTestUtils.redefineMutations(TEST_CLASS_NAME);
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(IntegerConstantsTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		IntegerConstants ric = new IntegerConstants();
		selectiveTestSuite.run(new TestResult());
		testResults(TEST_CLASS_NAME);
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
