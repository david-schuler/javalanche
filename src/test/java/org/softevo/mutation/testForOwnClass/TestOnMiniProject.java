package org.softevo.mutation.testForOwnClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.coverageResults.db.TestCoverageClassResult;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.testForOwnClass.ricProject.RicClass;
import org.softevo.mutation.testForOwnClass.ricProject.RicClassTest;
import org.softevo.mutation.testsuite.SelectiveTestSuite;

public class TestOnMiniProject {

	private static final String TEST_CLASS_FILENAME = "/Users/schuler/workspace2/mutationTest2/target/test-classes/org/softevo/mutation/testForOwnClass/ricProject/RicClass.class";

	private static final String TEST_CLASS_TYPE = "org.softevo.mutation.testForOwnClass.ricProject.RicClass";

	private static final String UNITTEST_FOR_TEST_CLASS= "org.softevo.mutation.testForOwnClass.ricProject.RicClassTest";


	private static final String[] testCaseNames = {
		UNITTEST_FOR_TEST_CLASS + ".testMethod1",
		UNITTEST_FOR_TEST_CLASS + ".testMethod2",
		UNITTEST_FOR_TEST_CLASS + ".testMethod3" };

	private static final int[] linenumbers = { 6, 11, 12, 16, 17 };

	public void generateMutations() {
		MutationPossibilityCollector.generateTestDataInDB(TEST_CLASS_FILENAME);
	}

	@Before
	public void setup() {
		deleteTestStuff();
		generateMutations();
	}

	public void deleteTestStuff() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("delete Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", TEST_CLASS_TYPE);
		int deletedEntities = q.executeUpdate();
		System.out.println(deletedEntities + " entities where deleted");
		tx.commit();
		session.close();

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
				"ric.RicClass", lineResult);
		try{
		QueryManager.save(classResult);
		}
		catch(org.hibernate.exception.ConstraintViolationException e){
			// Already contained in db;
		}
	}

	@Test
	public void runTests() {
		SelectiveTestSuite selectiveTestSuite = new SelectiveTestSuite();
		TestSuite suite = new TestSuite(RicClassTest.class);
		selectiveTestSuite.addTest(suite);
		RicClass ric = new RicClass();
		selectiveTestSuite.run(new TestResult());
	}

}
