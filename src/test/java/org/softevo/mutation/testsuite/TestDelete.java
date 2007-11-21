package org.softevo.mutation.testsuite;

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
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.TestMessage;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.runtime.MutationTestListener;


public class TestDelete {

	private static final String PASS_TEST = "passTest";

	@Test
	public void testDelete() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Mutation m = new Mutation("Test", 99, 0, MutationType.NO_MUTATION);
		MutationTestListener mutationTestListener = new MutationTestListener();
		TestMessage passingTestMessage = new TestMessage(PASS_TEST,
				"test passed");
		TestMessage failingTestMessage = new TestMessage(PASS_TEST,
				"test failed");
		TestMessage errorTestMessage = new TestMessage(PASS_TEST, "test error");
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
		SingleTestResult singleTestResult = new SingleTestResult(testResult,
				mutationTestListener, set);
		m.setMutationResult(singleTestResult);
		session.save(m);
		tx.commit();
		session.close();

		System.out.printf("Single Test Result id: %d \n", singleTestResult
				.getId());
		Long resultID = singleTestResult.getId();

		List<Long> testMessageIDs = new ArrayList<Long>();
		for (TestMessage testMessage : singleTestResult.getPassing()) {
			testMessageIDs.add(testMessage.getId());
		}
		for (TestMessage testMessage : singleTestResult.getErrors()) {
			testMessageIDs.add(testMessage.getId());
		}
		for (TestMessage testMessage : singleTestResult.getFailures()) {
			testMessageIDs.add(testMessage.getId());
		}


		deleteMutation(m);

		checkForResultID(resultID);

		checkForTestMessageIds(testMessageIDs);
	}

	private void checkForTestMessageIds(List<Long> testMessageIDs) {
		for(Long id:testMessageIDs){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createSQLQuery(
				"SELECT * FROM TestMessage T WHERE id=:id").addEntity(
				TestMessage.class);
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
		Query q = session.createSQLQuery(
				"SELECT * FROM SingleTestResult S WHERE id=:id").addEntity(
				SingleTestResult.class);
		q.setLong("id", resultID);
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
		Query q = session.createSQLQuery(
				"SELECT * FROM SingleTestResult_Errors S WHERE singletestresult_id=:id").addEntity(
				SingleTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0,
				resultSize);
	}

	private void checkLinkTablesFailing(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createSQLQuery(
				"	SELECT * FROM SingleTestResult_TestMessage S WHERE singletestresult_id=:id").addEntity(
				SingleTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0,
				resultSize);
	}

	private void checkLinkTablesPassing(Long resultID) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createSQLQuery(
				"SELECT * FROM SingleTestResult_Passing S WHERE singletestresult_id=:id").addEntity(
				SingleTestResult.class);
		q.setLong("id", resultID);
		int resultSize = q.list().size();
		tx.commit();
		session.close();
		Assert.assertEquals("Expecting result to be deleted", 0,
				resultSize);
	}

	private void deleteMutation(Mutation m) {
		Session session2 = HibernateUtil.getSessionFactory().openSession();
		Transaction tx2 = session2.beginTransaction();
		session2.delete(m);
		tx2.commit();
		session2.close();
	}
}
