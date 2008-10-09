package org.softevo.mutation.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationCoverage;
import org.softevo.mutation.results.TestName;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class CoverageDataTest {

	private static Logger logger = Logger.getLogger(CoverageDataTest.class);

	private static final String TESTCLASS_NAME = "TESTCLASS";

	@Before
	@After
	public void setup(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("delete FROM Mutation WHERE classname=:name");
		query.setString("name", TESTCLASS_NAME);
		query.executeUpdate();
		tx.commit();
		session.close();
	}


	// @Test
	public void testSaveCoverageResult() {
		Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();
		List<String> testNames = getTestNames(150);
		assertEquals(150, testNames.size());
		List<Mutation> mutations = getMutations(1000);
		QueryManager.saveMutations(mutations);
		int i = 1;
		for (Mutation mutation : mutations) {
			Set<String> tests = new HashSet<String>(testNames.subList(0, Math
					.min(i, testNames.size())));
			coverageData.put(mutation.getId(), tests);
			i++;
		}
		QueryManager.saveCoverageResults(coverageData);
		MutationCoverage mc = QueryManager.getMutationCoverageData(mutations
				.get(0).getId());
		assertNotNull(mc);
		assertNotNull(mc.getTestsNames());
		assertEquals(1, mc.getTestsNames().size());
		MutationCoverage mc200 = QueryManager.getMutationCoverageData(mutations
				.get(200).getId());
		assertNotNull(mc200);
		assertNotNull(mc200.getTestsNames());
		assertEquals(150, mc200.getTestsNames().size());

		QueryManager.deleteCoverageResultByMutaiton(mutations);

	}

	@Test
	public void testSaveCoverageResultWithNull() {
		Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();
		List<String> testNames = getTestNames(150);
		assertEquals(150, testNames.size());
		testNames.add(null);
		testNames.add(null);
		testNames.add(null);
		List<Mutation> mutations = getMutations(200);
		QueryManager.saveMutations(mutations);

		int i = 1;
		for (Mutation mutation : mutations) {
			Set<String> tests = new HashSet<String>(testNames.subList(0, Math
					.min(i, testNames.size())));
			coverageData.put(mutation.getId(), tests);
			i++;
		}
		QueryManager.saveCoverageResults(coverageData);

		System.out.println(mutations
				.get(160));
		System.out.println(mutations
				.get(160).getId());
		MutationCoverage mc160 = QueryManager.getMutationCoverageData(mutations
				.get(160).getId());
		MutationCoverage mc161 = QueryManager.getMutationCoverageData(mutations
				.get(161).getId());

		Long nullId = Long.MIN_VALUE;
		for (TestName testName : mc160.getTestsNames()) {
			if (testName.getName() == null) {
				logger.info("Found null. Id: " + testName.getId());
				Long id = testName.getId();
				if (nullId == Long.MIN_VALUE) {
					nullId = id;
				} else {
					assertEquals("IDs for null value do not match", nullId, id);
				}
			}
		}
		for (TestName testName : mc161.getTestsNames()) {
			if (testName.getName() == null) {
				logger.info("Found null. Id: " + testName.getId());
				Long id = testName.getId();
				if (nullId == Long.MIN_VALUE) {
					nullId = id;
				} else {
					assertEquals("IDs for null value do not match", nullId, id);
				}
			}
		}

	}

	private List<Mutation> getMutations(int limit) {
		ArrayList<Mutation> result = new ArrayList<Mutation>();
		for (int i = 0; i < limit; i++) {
			Mutation m = new Mutation(TESTCLASS_NAME, 23, i,
					MutationType.NEGATE_JUMP, false);
			result.add(m);
		}
		return result;
	}

	private List<String> getTestNames(int limit) {
		String baseName = "TESTNAME_";
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < limit; i++) {
			String name = String.format(baseName + "%2d", i);
			names.add(name);
		}
		return names;
	}

	public void deleteCoverage() {

	}

}
