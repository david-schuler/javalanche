package de.unisb.cs.st.javalanche.mutation;

import java.util.Random;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class LongArrayTest {


	@Test
	public void testLongArray(){
		MutationTestResult testResult = new MutationTestResult();
		int[] array = new int[100];
		Random r= new Random();
		for (int i = 0; i < array.length; i++) {
			array[i] = r.nextInt();
		}
		save(testResult, array);
		System.out.println(testResult);
		delete(testResult);



	}

	private void delete(MutationTestResult testResult) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.delete(testResult);
		tx.commit();
		session.close();
		}

	private void save(MutationTestResult testResult, int[] array) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		testResult.setViolatedInvariants(array);
		session.save(testResult);
		tx.commit();
		session.close();
	}
}
