package de.unisb.cs.st.javalanche.mutation.hibernate;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

@SuppressWarnings("unchecked")
// Because of lists returned by hibernate
public class HibernateTest {

	private static Mutation testMutation = new Mutation("testClass", 21, 0,
			MutationType.RIC_PLUS_1, false);

	@BeforeClass
	public static void hibernateSave() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		testMutation.setMutationResult(new MutationTestResult());
		session.save(testMutation);
		tx.commit();
		session.close();
	}

	@AfterClass
	public static void hibernateDelete() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where className=:name");
		query.setString("name", testMutation.getClassName());
		List l = query.list();
		for (Object object : l) {
			session.delete(object);
		}
		tx.commit();
		session.close();
	}

	@Test
	public void testReatach() {
		assertEquals(0, testMutation.getMutationForLine());
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.update(testMutation);
		testMutation.setMutationForLine(10);
		tx.commit();
		session.close();
		testMutation.setMutationForLine(0);
		Mutation mutation = QueryManager.getMutation(testMutation);
		assertNotSame(testMutation, mutation);
		assertEquals(10, mutation.getMutationForLine());

	}

	@Test
	public void hibernateQueryByLine() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where lineNumber="
				+ testMutation.getLineNumber());
		query.setMaxResults(20);
		List results = query.list();
		int count = 0;
		for (Object o : results) {
			Assert.assertTrue(o instanceof Mutation);
			count++;

		}
		Assert.assertTrue("Expected at least one mutation for line"
				+ testMutation.getLineNumber(), count > 0);
		tx.commit();
		session.close();
	}

	@Test(timeout = 5000)
	public void hibernateQueryByType() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where mutationtype="
				+ testMutation.getMutationType().ordinal());
		query.setMaxResults(100);
		List results = query.list();
		for (Object o : results) {
			if (o instanceof Mutation) {
			} else {
				throw new RuntimeException("Expected other Type. Was: "
						+ o.getClass() + " Expected: " + Mutation.class);
			}
		}
		Assert
				.assertTrue("expected at least one result for mutationtype "
						+ testMutation.getMutationType().toString(), results
						.size() > 0);
		tx.commit();
		session.close();
	}

}
