/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.hibernate;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

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

	private static Mutation testMutation = new Mutation("testClass",
			"testMethod",
 new Random().nextInt(5000), 0,
			MutationType.RIC_PLUS_1);

	@BeforeClass
	public static void hibernateSave() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		testMutation.setMutationResult(new MutationTestResult());
		session.save(testMutation);
		tx.commit();
		session.close();
	}

	// @AfterClass
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

	// @Test
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
			Mutation m = (Mutation) o;
			System.out.println(m.getId());
			count++;

		}
		Assert.assertTrue("Expected at least one mutation for line"
				+ testMutation.getLineNumber(), count > 0);
		tx.commit();
		session.close();
	}

	// @Test(timeout = 5000)
	public void hibernateQueryByType() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation where mutationtype="
				+ testMutation.getMutationType().ordinal());
		query.setMaxResults(100);
		List results = query.list();
		for (Object o : results) {

			if (o instanceof Mutation) {
				Mutation m = (Mutation) o;

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

	// @Test
	// public void testQueryBoolean() {
	// Session session = HibernateUtil.getSessionFactory().openSession();
	// Transaction tx = session.beginTransaction();
	// String projectPrefix = "triangle";
	// boolean classInit = true;
	// Query query = session
	// .createQuery("SELECT count(*) FROM Mutation WHERE className LIKE '"
	// + projectPrefix + "%' AND classInit=" + classInit);
	// List results = query.list();
	// for (Object object : results) {
	// System.out.println("OBJECT " + object);
	// }
	// tx.commit();
	// session.close();
	// }

}
