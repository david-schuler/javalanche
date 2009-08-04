package de.unisb.cs.st.javalanche.mutation.integration;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Ignore;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Class checks the results of the integration test. The integration test is a
 * seperate project that is analyzed and executed by the mutation testsing
 * framework. (see the corresponding build files in the
 * integrationTestMutation).
 * 
 * @author David Schuler
 * 
 */
public class CheckIntegration1ResultsTest {

	private static final String PACKAGE_NAME = "org.integrationtest1.";

	@Test
	public void mavenTest() {

	}

	@Test
	public void checkResultsAllCoveredAndKilled() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String className = PACKAGE_NAME + "AllCoveredAndKilled";
		List<Mutation> mutations = QueryManager.getMutationsForClass(className,
				session);
		for (Mutation m : mutations) {
			assertTrue(
					"Expect all mutations in " + className + " to be killed", m
							.isKilled());
		}
		assertEquals(12, mutations.size());
		tx.commit();
		session.close();
	}

	@Test
	public void checkResultsAllInOneLine() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String className = PACKAGE_NAME + "AllMutationsInOneLine";
		List<Mutation> mutations = QueryManager.getMutationsForClass(className,
				session);
		for (Mutation m : mutations) {
			assertEquals("Expected other linenumber for mutation", 8, m
					.getLineNumber());
		}
		assertEquals(16, mutations.size());
		tx.commit();
		session.close();
	}

	@Test
	public void checkResultsAllCoveredNotKilled() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String className = PACKAGE_NAME + "AllCoverdNotKilled";
		List<Mutation> mutations = QueryManager.getMutationsForClass(className,
				session);
		for (Mutation m : mutations) {
			assertTrue("Expected mutation to be covered by test", QueryManager
					.isCoveredMutation(m));
			assertFalse("Expect mutation not to be killed", m.isKilled());
			assertEquals(
					"Expected mutation to be covered by test exactly one test",
					1, MutationCoverageFile.getCoverageDataId(m.getId()).size());

		}
		assertEquals(11, mutations.size());
		tx.commit();
		session.close();
	}

	@Test
	public void checkResultsMutationCausesEndlessLoopTest() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String className = PACKAGE_NAME + "MutationCausesEndlessLoop";
		List<Mutation> mutations = QueryManager.getMutationsForClass(className,
				session);
		int killCount = 0;
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				Collection<TestMessage> errors = m.getMutationResult()
						.getErrors();
				for (TestMessage testMessage : errors) {
					boolean contains = testMessage.getMessage().contains(
							"Mutated Thread is still running after timeout.");
					if (contains) {
						killCount++;
					}
				}
			}
		}
		assertEquals("Expected one mutation to be detected by timeout", 1,
				killCount);
		tx.commit();
		session.close();
	}

	public static void main(String[] args) {
		CheckIntegration1ResultsTest c = new CheckIntegration1ResultsTest();
		c.checkResultsAllCoveredAndKilled();
		c.checkResultsAllInOneLine();
		c.checkResultsAllCoveredNotKilled();
		c.checkResultsMutationCausesEndlessLoopTest();
	}
}
