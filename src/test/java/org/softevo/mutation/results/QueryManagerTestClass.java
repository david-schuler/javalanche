package org.softevo.mutation.results;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestResult;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.softevo.mutation.hibernate.HibernateTest;
import org.softevo.mutation.properties.TestProperties;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.runtime.MutationTestListener;
import org.softevo.mutation.testutil.TestUtil;

public class QueryManagerTestClass {

	private static String className = "testClass";

	private static int testLineNumber = 123;

	private static MutationType testMutationType = MutationType.RIC_MINUS_1;

	private static Mutation testMutation = new Mutation(className,
			testLineNumber, 0, testMutationType);

	@BeforeClass
	public static void setUpClass(){
		TestUtil.getMutationsForClazzOnClasspath(TestProperties.ADVICE_CLAZZ);
	}


	@Before
	public void setUp() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(testMutation);
		tx.commit();
		session.close();
		// MutationPossibilityCollector
		// .generateTestDataInDB(MutationProperties.SAMPLE_FILE);
	}

	@After
	public void tearDown() {
		new HibernateTest().hibernateDelete();
	}

	@Test
	public void testQueryByValues() {
		Mutation queryMutation = new Mutation(className, testLineNumber, 0,
				testMutationType);
		Mutation resultMutation = QueryManager.getMutation(queryMutation);
		Assert.assertTrue(resultMutation != null);
		Assert.assertTrue(resultMutation.getId() != null);
	}

	@Test
	public void testQuery() {
		Mutation resultMutation = QueryManager.getMutation(testMutation);
		Assert.assertTrue(resultMutation != null);
		Assert.assertTrue(resultMutation.getId() != null);
	}

	@Test
	public void testGetAllMutations() {
		List<Mutation> list = QueryManager.getMutations(40);
		Assert.assertTrue(list.size() >= 1);
	}

	@Test
	public void testUpdate() {
		Mutation resultMutation = QueryManager.getMutation(testMutation);
		Assert.assertNull(resultMutation.getMutationResult());
		QueryManager.updateMutation(resultMutation, new SingleTestResult(
				new TestResult(), new MutationTestListener(), null));
		Mutation checkMutation = QueryManager.getMutation(testMutation);
		Assert.assertNotNull(checkMutation.getMutationResult());
	}

	@Test
	public void testTestsForLine() {
		String className = "org.aspectj.ajdt.internal.compiler.lookup.EclipseFactory";
		int lineNumber = 218;
		QueryManager.getTestCases(className, lineNumber);
	}

	@Ignore("TODO Generate Coverage data for artificial coverage data instead of using aspectj data")
	@Test
	public void testGetTestCases() {
		List<Mutation> mutationList = QueryManager
				.getAllMutationsForClass(TestProperties.SAMPLE_FILE_CLASS_NAME);
		int totalTests = 0;
		for (Mutation mutation : mutationList) {
			String[] testcases = QueryManager.getTestCases(mutation);
			if (testcases != null) {
				totalTests += testcases.length;
			}
		}
		Assert.assertTrue(totalTests > 20);
	}

	@Test
	public void testGetAllMutationsForTestCases() {
		QueryManager
				.getAllMutationsForTestCases(Arrays
						.asList(new String[] {
								"org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.TestJump.testMethod4",
								"org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps.TestJump.testMethod3" }));
	}

	@Test
	public void testhasMutationForClass() {
		boolean hasClass = QueryManager
				.hasMutationsforClass(TestProperties.SAMPLE_FILE_CLASS_NAME);
		Assert.assertTrue(String.format("Expected class %s in db",
				TestProperties.SAMPLE_FILE_CLASS_NAME), hasClass);
	}

	@Ignore("TODO Generate Coverage data for artificial coverage data instead of using aspectj data")
	@Test
	public void testIsCoveredMutation() {
		List<Mutation> mutationList = QueryManager
				.getAllMutationsForClass(TestProperties.SAMPLE_FILE_CLASS_NAME);
		int coverCount = 0;
		for (Mutation mutation : mutationList) {
			if (QueryManager.isCoveredMutation(mutation)) {
				coverCount++;
			}
		}
		Assert.assertTrue(coverCount > 20);
	}
}
