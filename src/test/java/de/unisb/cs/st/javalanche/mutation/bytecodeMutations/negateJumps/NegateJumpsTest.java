package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

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
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.Jumps;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.JumpsTest;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class NegateJumpsTest {

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.Jumps";
		ByteCodeTestUtils.doSetup(classname,
				new NegateJumpsCollectorTransformer(null));

	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = Jumps.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = JumpsTest.class.getName();

//	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
//			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 5);

	private static final int[] linenumbers = { 6, 14, 16, 25, 34, 37 };

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
		System.setProperty(MutationProperties.RESULT_FILE_KEY,
				"target/unittestResults.xml");
		ByteCodeTestUtils.redefineMutations(TEST_CLASS_NAME);
		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
		TestSuite suite = new TestSuite(JumpsTest.class);
		selectiveTestSuite.addTest(suite);
		System.out.println(TEST_CLASS.hashCode());
		@SuppressWarnings("unused")
		Jumps jumps = new Jumps(); // ensure that class is loaded
		selectiveTestSuite.run(new TestResult());
		testResults();
	}

	@SuppressWarnings("unchecked")
	private void testResults() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", TEST_CLASS_NAME);
		List<Mutation> mList = query.list();
		int nonNulls = 0;
		for (Mutation m : mList) {
			System.out.println(m);
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null
					&& m.getMutationType() != MutationType.NO_MUTATION) {
				nonNulls++;
				Assert.assertTrue(2 >= singleTestResult.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());
				Assert.assertTrue("Expected at least one error for mutation:  " + m, 1 <= singleTestResult.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());

				Assert.assertTrue(singleTestResult.isTouched());
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected results from mutations", nonNulls > 5);
	}

}