package org.softevo.mutation.results;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.hibernate.HibernateTest;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class QueryManagerTestClass {

	private static String className = "testClass";

	private static int testLineNumber = 123;

	private static MutationType testMutationType = MutationType.RIC_MINUS_1;

	private static Mutation testMutation = new Mutation(className,
			testLineNumber, testMutationType);

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
		new HibernateTest().hibernateDelete();
	}

	@Test
	public void testQueryByValues() {
		Mutation queryMutation = new Mutation(className, testLineNumber,
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
		List<Mutation> list = QueryManager.getAllMutations();
		Assert.assertTrue(list.size() >= 1);
	}

	@Test
	public void testUpdate() {
		Mutation resultMutation = QueryManager.getMutation(testMutation);
		Assert.assertNull(resultMutation.getMutationResult());
		QueryManager.updateMutation(resultMutation, new SingleTestResult(1, 2,
				3));
		Mutation checkMutation = QueryManager.getMutation(testMutation);
		Assert.assertNotNull(checkMutation.getMutationResult());
	}

	@Test public void testTestsForLine(){
		String className = "org.aspectj.ajdt.internal.compiler.lookup.EclipseFactory";
		int lineNumber  = 218;
		QueryManager.getLineNumber(className, lineNumber);
	}
}
