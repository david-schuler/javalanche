/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.testsuite;

import java.util.Collection;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class DeleteTest {


	@Test
	public void testDelete() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Mutation m = new Mutation("Test", "testMethod", 99, 0,
				MutationType.NO_MUTATION);

		MutationTestResult mutationTestResult = new MutationTestResult();
		m.setMutationResult(mutationTestResult);
		session.save(m);
		tx.commit();
		session.close();

		Long resultID = mutationTestResult.getId();

		Collection<TestMessage> testMessageIDs = mutationTestResult
				.getAllTestMessages();
		deleteMutation(m);

		checkForResultID(resultID);

		checkForTestMessageIds(testMessageIDs);
	}

	private void checkForTestMessageIds(Collection<TestMessage> testMessageIDs) {
		for (TestMessage tm : testMessageIDs) {
			Long id = tm.getId();
			Session session = HibernateUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			Query q = session.createQuery("FROM TestMessage T WHERE id=:id");
			q.setLong("id", id);
			int resultSize = q.list().size();
			tx.commit();
			session.close();
			Assert.assertEquals("Expecting TestMessage to be deleted", 0,
					resultSize);

		}

	}

	private void checkForResultID(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session
				.createQuery("FROM MutationTestResult as mtr where id=:r_id");
		q.setLong("r_id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert
				.assertEquals("Expected TestMessage to be deleted", 0,
						resultSize);

		checkLinkTablesPassing(resultID);
		checkLinkTablesFailing(resultID);
		checkLinkTablesErrors(resultID);

	}

	private void checkLinkTablesErrors(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session
				.createSQLQuery(
						"SELECT * FROM MutationTestResult_Errors S WHERE MutationTestResult_id=:id")
				.addEntity(MutationTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0, resultSize);
	}

	private void checkLinkTablesFailing(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session
				.createSQLQuery(
						"	SELECT * FROM MutationTestResult_TestMessage S WHERE MutationTestResult_id=:id")
				.addEntity(MutationTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0, resultSize);
	}

	private void checkLinkTablesPassing(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session
				.createSQLQuery(
						"SELECT * FROM MutationTestResult_Passing S WHERE MutationTestResult_id=:id")
				.addEntity(MutationTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0, resultSize);
	}

	private void deleteMutation(Mutation m) {
		Session session2 = HibernateUtil.getSessionFactory().openSession();
		Transaction tx2 = session2.beginTransaction();
		session2.delete(m);
		tx2.commit();
		session2.close();
	}
}
