package org.softevo.mutation.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class HibernateTest {

	private Mutation testMutaion = new Mutation("testClass", 21, 0,
			MutationType.RIC_PLUS_1);;

	@Before
	public void hibernateSave() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(testMutaion);
		tx.commit();
		session.close();
	}

	@After
	public void hibernateDelete() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where className=:name");
		query.setString("name", testMutaion.getClassName());
		List l = query.list();
		for (Object object : l) {
			session.delete(object);
		}
		tx.commit();
		session.close();
	}

	@Test
	public void hibernateQueryByLine() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where lineNumber="
				+ testMutaion.getLineNumber());
		query.setMaxResults(20);
		List results = query.list();
		int count = 0;
		for (Object o : results) {
			if (o instanceof Mutation) {
				count++;
			} else {
				throw new RuntimeException("Expected other Type. Was: "
						+ o.getClass() + " Expected: " + Mutation.class);
			}
		}
		Assert.assertTrue("Expected at least one mutation for line"
				+ testMutaion.getLineNumber(), count > 0);
		Mutation m = new Mutation("testClass", 21, 0, MutationType.RIC_PLUS_1);
		session.save(m);
		tx.commit();
		session.close();
	}

	@Test
	public void hibernateQueryByType() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where mutationtype="
				+ testMutaion.getMutationType().ordinal());
		List results = query.list();
		query.setMaxResults(100);
		for (Object o : results) {
			if (o instanceof Mutation) {
			} else {
				throw new RuntimeException("Expected other Type. Was: "
						+ o.getClass() + " Expected: " + Mutation.class);
			}
		}
		Assert.assertTrue("expected at least one result for mutationtype "
				+ testMutaion.getMutationType().toString(), results.size() > 0);
		tx.commit();
		session.close();
	}

	@Test(timeout = 8000)
	public void showMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation ");
		query.setMaxResults(50);
		List results = query.list();
		Assert.assertTrue(results.size() > 30);
		tx.commit();
		session.close();
	}

}
