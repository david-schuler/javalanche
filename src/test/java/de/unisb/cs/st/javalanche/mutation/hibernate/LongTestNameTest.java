package de.unisb.cs.st.javalanche.mutation.hibernate;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

/**
 * Test that long names can be stored in database.
 *
 * @author David Schuler
 *
 */
public class LongTestNameTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testTestName() {
		String base = "0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			sb.append(base);
		}
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		TestName t = new TestName(sb.toString());
		session.save(t);
		tx.commit();
		session.close();

		Session session2 = HibernateUtil.getSessionFactory().openSession();
		Transaction tx2 = session2.beginTransaction();
		Query query = session2
				.createQuery("from TestName WHERE name LIKE '0123456789%'");
		List<TestName> list = query.list();
		assertEquals(100 *10, list.get(0).getName().length());
		tx2.commit();
		session2.close();

	}


}
