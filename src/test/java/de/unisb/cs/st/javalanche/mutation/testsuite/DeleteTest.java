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
		Mutation m = new Mutation("Test", 99, 0, MutationType.NO_MUTATION,
				false);

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
