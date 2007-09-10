package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.Jumps;
import org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.TestJump;
import org.softevo.mutation.coverageResults.db.TestCoverageClassResult;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.testsuite.SelectiveTestSuite;

public class NegateJumpsTest {

	private static final String TEST_CLASS_TYPE = Jumps.class.getName();

	private static final String UNITTEST_FOR_TEST_CLASS = TestJump.class
			.getName();

	private static final String TEST_CLASS_FILENAME = "/Users/schuler/workspace2/mutationTest/target/test-classes/org/softevo/mutation/bytecodeMutations/negateJumps/forOwnClass/jumps/Jumps.class";// Jumps.class.getProtectionDomain().getCodeSource().getLocation().toString()

	// +
	// TEST_CLASS_TYPE.replace('.','/')
	// +
	// ".class";

	private static String[] testCaseNames;

	static {
		int numberOfTestCases = 4;
		testCaseNames = new String[numberOfTestCases];
		for (int i = 0; i < numberOfTestCases; i++) {
			testCaseNames[i] = UNITTEST_FOR_TEST_CLASS + ".testMethod"
					+ (i + 1);
		}
	}

	private static final int[] linenumbers = { 6, 14, 16, 25, 34, 37 };

	public void generateMutations() {
		System.out.println(TEST_CLASS_FILENAME);
		generateTestDataInDB(TEST_CLASS_FILENAME);
	}

	public static void generateTestDataInDB(String classFileName) {
		FileTransformer ft = new FileTransformer(new File(classFileName));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new NegateJumpsCollectorTransformer(mpc));
		mpc.toDB();
		// TODO
	}

	 @Before
	public void setup() {
		deleteTestStuff();
		generateMutations();
	}

	@After
	public void tearDown() {
		 deleteTestStuff();
	}

	public void deleteTestStuff() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("from Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", TEST_CLASS_TYPE);
		//int deletedEntities = q.executeUpdate();
		List mutations = q.list();
		for(Object m : mutations){
			((Mutation) m).setMutationResult(null);
		}
		//System.out.println(deletedEntities + " entities where deleted");
		tx.commit();
		session.close();
		// TODO UTIL Method
	}

	@Before
	public void generateCoverageData() {
		List<TestCoverageTestCaseName> names = new ArrayList<TestCoverageTestCaseName>();
		for (String name : testCaseNames) {
			names.add(TestCoverageTestCaseName
					.getTestCoverageTestCaseName(name));
		}
		List<TestCoverageLineResult> lineResult = new ArrayList<TestCoverageLineResult>();
		List<String> testCaseNamesList = Arrays.asList(testCaseNames);
		for (int number : linenumbers) {
			lineResult
					.add(new TestCoverageLineResult(number, testCaseNamesList));
		}
		TestCoverageClassResult classResult = new TestCoverageClassResult(
				TEST_CLASS_TYPE, lineResult);
		try {
			QueryManager.save(classResult);
		} catch (org.hibernate.exception.ConstraintViolationException e) {
			// Already contained in db;
		}
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
