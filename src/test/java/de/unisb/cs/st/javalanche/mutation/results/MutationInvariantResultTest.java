package de.unisb.cs.st.javalanche.mutation.results;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationInvariantResultTest {

	@Test
	public void testStoreInvariantSet() {
		Set<Integer> set = new HashSet<Integer>();
		Random r = new Random();
		for (int i = 0; i < 20; i++) {
			set.add(r.nextInt());
		}
		InvariantSet invariantSet = new InvariantSet();
		invariantSet.setInvariants(set);
		QueryManager.save(invariantSet);
		Long id = invariantSet.getId();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		InvariantSet invariantSetDb = QueryManager.getObjectById(id, InvariantSet.class, session);
		assertNotNull(invariantSetDb);
		assertEquals(20, invariantSetDb.getInvariants().size());
		tx.commit();
		session.close();
		QueryManager.delete(invariantSet);
	}


	@Test
	public void testStoreInvariantAddResult() {
		Set<Integer> list = new HashSet<Integer>();
		Random r = new Random();
		for (int i = 0; i < 20; i++) {
			list.add(r.nextInt());
		}
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		map.put("key", list);
		InvariantAddResult invariantAddResult = new InvariantAddResult(map);
		assertEquals(1, invariantAddResult.getViolationsPerTest().size());
		assertEquals(20, invariantAddResult.getViolationsPerTest().get("key").getInvariants().size());
		QueryManager.save(invariantAddResult);
		Long id = invariantAddResult.getId();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		InvariantAddResult iaddDb = QueryManager.getObjectById(id, InvariantAddResult.class,session);
		assertEquals(1, iaddDb.getViolationsPerTest().size());
		assertEquals(20, iaddDb.getViolationsPerTest().get("key").getInvariants().size());
		tx.commit();
		session.close();
		QueryManager.delete(invariantAddResult);
	}

	@Test
	public void testStoreMutationTestResult() {
		Set<Integer> list = new HashSet<Integer>();
		Random r = new Random();
		for (int i = 0; i < 20; i++) {
			list.add(r.nextInt());
		}
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		map.put("key", list);
		InvariantAddResult invariantAddResult = new InvariantAddResult(map);
		MutationTestResult mutationTestResult = new MutationTestResult();
		mutationTestResult.addResults(invariantAddResult);
		assertEquals(1, invariantAddResult.getViolationsPerTest().size());
		assertEquals(20, invariantAddResult.getViolationsPerTest().get("key").getInvariants().size());
		QueryManager.save(mutationTestResult);
		Long id = mutationTestResult.getId();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		MutationTestResult resultFromDb = QueryManager.getObjectById(id, MutationTestResult.class,session);
		assertEquals(1, ((InvariantAddResult) resultFromDb.getAddResults().get(0)).getViolationsPerTest().size());
		assertEquals(20,((InvariantAddResult)  resultFromDb.getAddResults().get(0)).getViolationsPerTest().get("key").getInvariants().size());
		tx.commit();
		session.close();
		QueryManager.delete(mutationTestResult);
	}


}
