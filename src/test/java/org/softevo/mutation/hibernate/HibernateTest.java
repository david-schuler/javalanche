package org.softevo.mutation.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class HibernateTest {

	private Mutation testMutaion = new Mutation("testClass", 21,0,
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
		List results = query.list();
		for (Object o : results) {
			if (o instanceof Mutation) {
				Mutation mutation = (Mutation) o;
				System.out.println("Queried mutation: " + mutation);
			} else {
				throw new RuntimeException("Expected other Type. Was: "
						+ o.getClass() + " Expected: " + Mutation.class);
			}
		}

		Mutation m = new Mutation("testClass", 21,0, MutationType.RIC_PLUS_1);
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
		for (Object o : results) {
			if (o instanceof Mutation) {
				Mutation mutation = (Mutation) o;
				System.out.println("Queried mutation by Type " + mutation);
			} else {
				throw new RuntimeException("Expected other Type. Was: "
						+ o.getClass() + " Expected: " + Mutation.class);
			}
		}
		tx.commit();
		session.close();
	}

	@Test
	public void showMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation ");
		List results = query.list();
		for (Object o : results) {
			System.out.println(o);
		}
		tx.commit();
		session.close();
	}

}
