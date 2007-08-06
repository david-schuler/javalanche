package org.softevo.mutation.hibernate;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class HibernateTest {

	@Test
	public void hibernateSave() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Mutation m = new Mutation("testClass", 21,
				MutationType.REPLACE_INTEGER_CONSTANT);
		session.save(m);

		tx.commit();
		session.close();
	}

	@Test
	public void hibernateQuery() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where line=21");
		List results = query.list();
		for (Object o : results) {
			if (o instanceof Mutation) {
				Mutation mutation = (Mutation) o;
				System.out.println("Queried mutation: " + mutation);
			}
			else{
				throw new RuntimeException("Expected other Type. Was: "+ o.getClass() + " Expected: " + Mutation.class);
			}
		}

		Mutation m = new Mutation("testClass", 21,
				MutationType.REPLACE_INTEGER_CONSTANT);
		session.save(m);

		tx.commit();
		session.close();
	}

	@Test
	public void hibernateQuery2() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where mutation="+ MutationType.REPLACE_INTEGER_CONSTANT.ordinal());
		List results = query.list();
		for (Object o : results) {
			if (o instanceof Mutation) {
				Mutation mutation = (Mutation) o;
				System.out.println("Queried mutation by Type " + mutation);
			}
			else{
				throw new RuntimeException("Expected other Type. Was: "+ o.getClass() + " Expected: " + Mutation.class);
			}
		}

		Mutation m = new Mutation("testClass", 21,
				MutationType.REPLACE_INTEGER_CONSTANT);
		session.save(m);

		tx.commit();
		session.close();
	}


}
