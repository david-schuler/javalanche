package de.unisb.cs.st.javalanche.mutation.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestResult;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.MutationJunitTestListener;

public class DeleteTest {

	private static final String PASS_TEST = "passTest";

	@Test
	public void testDelete() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Mutation m = new Mutation("Test", 99, 0, MutationType.NO_MUTATION,
				false);
		MutationJunitTestListener mutationTestListener = new MutationJunitTestListener();
		TestMessage passingTestMessage = new TestMessage(PASS_TEST,
				"test passed", 0);
		TestMessage failingTestMessage = new TestMessage(PASS_TEST,
				"test failed",0);
		TestMessage errorTestMessage = new TestMessage(PASS_TEST, "test error",0);
		List<TestMessage> passing = Arrays
				.asList(new TestMessage[] { passingTestMessage });
		List<TestMessage> failing = Arrays
				.asList(new TestMessage[] { failingTestMessage });
		List<TestMessage> error = Arrays
				.asList(new TestMessage[] { errorTestMessage });
		mutationTestListener.setPassingMessages(passing);
		mutationTestListener.setFailureMessages(failing);
		mutationTestListener.setErrorMessages(error);
		TestResult testResult = new TestResult();
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(new String[] { PASS_TEST }));
		MutationTestResult mutationTestResult = new MutationTestResult(
				testResult, mutationTestListener, set);
		m.setMutationResult(mutationTestResult);
		session.save(m);
		tx.commit();
		session.close();

		System.out.printf("Single Test Result id: %d \n", mutationTestResult
				.getId());
		Long resultID = mutationTestResult.getId();

		List<Long> testMessageIDs = new ArrayList<Long>();
		for (TestMessage testMessage : mutationTestResult.getPassing()) {
			testMessageIDs.add(testMessage.getId());
		}
		for (TestMessage testMessage : mutationTestResult.getErrors()) {
			testMessageIDs.add(testMessage.getId());
		}
		for (TestMessage testMessage : mutationTestResult.getFailures()) {
			testMessageIDs.add(testMessage.getId());
		}

		deleteMutation(m);

		checkForResultID(resultID);

		checkForTestMessageIds(testMessageIDs);
	}

	private void checkForTestMessageIds(List<Long> testMessageIDs) {
		for (Long id : testMessageIDs) {
			Session session = HibernateUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			Query q = session
					.createQuery("FROM TestMessage T WHERE id=:id");
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
		Query q = session.createQuery(
				"FROM MutationTestResult as mtr where id=:r_id");
		q.setLong("r_id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting TestMessage to be deleted", 0,
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
