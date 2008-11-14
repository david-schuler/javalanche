package de.unisb.cs.st.javalanche.mutation.hibernate;

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
	}

}
