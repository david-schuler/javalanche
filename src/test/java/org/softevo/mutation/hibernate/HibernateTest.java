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

@SuppressWarnings("unchecked")
// Because of lists returned by hibernate
public class HibernateTest {

	private Mutation testMutaion = new Mutation("testClass", 21, 0,
			MutationType.RIC_PLUS_1,false);;

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
		Mutation m = new Mutation("testClass", 21, 0, MutationType.RIC_PLUS_1,false);
		session.save(m);
		tx.commit();
		session.close();
	}

	@Test(timeout = 5000)
	public void hibernateQueryByType() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where mutationtype="
				+ testMutaion.getMutationType().ordinal());
		query.setMaxResults(100);
		List results = query.list();
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

}
