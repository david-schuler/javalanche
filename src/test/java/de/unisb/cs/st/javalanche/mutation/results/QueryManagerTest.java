package de.unisb.cs.st.javalanche.mutation.results;

import static org.junit.Assert.*;

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
import de.unisb.cs.st.javalanche.mutation.hibernate.HibernateTest;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationJunitTestListener;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;

public class QueryManagerTest {

	private static String className = "testClass";

	private static int testLineNumber = 123;

	private static MutationType testMutationType = MutationType.RIC_MINUS_1;

	private static Mutation testMutation = new Mutation(className,
			testLineNumber, 0, testMutationType, false);

	@BeforeClass
	public static void setUpClass() {
		TestUtil.getMutationsForClazzOnClasspath(TestProperties.ADVICE_CLAZZ);
	}

	@Before
	public void setUp() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(testMutation);
		tx.commit();
		session.close();

	}

	@After
	public void tearDown() {
		HibernateTest.hibernateDelete();
	}

	@Test
	public void testQueryByValues() {
		Mutation queryMutation = new Mutation(className, testLineNumber, 0,
				testMutationType, false);
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
		QueryManager.updateMutation(resultMutation, new MutationTestResult());
		Mutation checkMutation = QueryManager.getMutation(testMutation);
		Assert.assertNotNull(checkMutation.getMutationResult());
	}

	@Test
	public void testhasMutationForClass() {
		boolean hasClass = QueryManager
				.hasMutationsforClass(TestProperties.SAMPLE_FILE_CLASS_NAME);
		Assert.assertTrue(String.format("Expected class %s in db",
				TestProperties.SAMPLE_FILE_CLASS_NAME), hasClass);
	}

	@Ignore("TODO Generate Coverage data for artificialialy instead of using aspectj data")
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
		assertTrue(coverCount > 20);
	}

	@Test
	public void testSaveTestMessage() {
		long pre = QueryManager
				.getResultFromCountQuery("SELECT COUNT(*) FROM TestName");
		TestName tm = new TestName("Test", "test.test", 100);
		QueryManager.save(tm);
		long post = QueryManager
				.getResultFromCountQuery("SELECT COUNT(*) FROM TestName");
		System.out.print(pre + "   " + post);
		QueryManager.delete(tm);
		assertTrue(post > pre);
		long postDelete = QueryManager
				.getResultFromCountQuery("SELECT COUNT(*) FROM TestName");
		assertEquals(pre, postDelete);

	}
}
