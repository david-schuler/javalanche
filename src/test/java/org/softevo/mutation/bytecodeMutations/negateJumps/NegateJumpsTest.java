package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps;
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.TestJump;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.testsuite.SelectiveTestSuite;

public class NegateJumpsTest {

	private static final Class TEST_CLASS = Jumps.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	// private static final String TEST_CLASS_NAME =
	// "org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps";

	private static final String UNITTEST_CLASS_NAME = TestJump.class.getName();

	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 4);

	private static final int[] linenumbers = { 6, 14, 16, 25, 34, 37 };

	private static final String OUTPUT_FILE = "negate-jumps-mutation-ids.txt";

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateTestDataInDB(TEST_CLASS_FILENAME,
				new NegateJumpsCollectorTransformer(null));
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
	}

	@After
	public void tearDown() {
		// ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		// ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		writeTestFile();
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(TestJump.class);
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
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				Assert.assertEquals(1, singleTestResult.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected results from mutations", nonNulls > 5);
	}

	@SuppressWarnings("unchecked")
	private void writeTestFile() {
		List<Long> ids = new ArrayList<Long>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", TEST_CLASS_NAME);
		List<Mutation> mList = query.list();
		for (Mutation m : mList) {
			ids.add(m.getId());
		}
		tx.commit();
		session.close();
		StringBuilder sb = new StringBuilder();
		for (Long l : ids) {
			sb.append(l + "\n");
		}
		File file = new File(OUTPUT_FILE);
		Io.writeFile(sb.toString(), file);
		System.setProperty("mutation.file", file.getAbsolutePath());
		MutationForRun.getInstance().reinit();
	}

}
