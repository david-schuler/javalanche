package org.softevo.mutation.results.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.TestMessage;
import org.softevo.mutation.results.Mutation.MutationType;

public class QueryManager {

	private static Logger logger = Logger.getLogger(QueryManager.class);

	public static Mutation getMutation(Mutation mutation) {
		Mutation m = getMutationOrNull(mutation);
		if (m == null) {
			throw new RuntimeException("Mutation not found in DB " + mutation);
		}
		return m;

	}

	public static Mutation getMutationOrNull(Mutation mutation) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Mutation m = null;
		if (mutation.getId() != null) {
			m = (Mutation) session.get(Mutation.class, mutation.getId());
		}
		if (m == null) {
			Query query = session
					.createQuery("from Mutation as m where m.className=:name and m.lineNumber=:number and m.mutationForLine=:mforl and m.mutationType=:type");
			query.setParameter("name", mutation.getClassName());
			query.setParameter("number", mutation.getLineNumber());
			query.setParameter("type", mutation.getMutationType());
			query.setParameter("mforl", mutation.getMutationForLine());
			logger.info(query.getQueryString());
			m = (Mutation) query.uniqueResult();
		}
		tx.commit();
		session.close();
		return m;
	}

	public static void updateMutation(Mutation mutation,
			SingleTestResult mutationTestResult) {
		mutation = getMutation(mutation);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Mutation m2 = (Mutation) session.get(Mutation.class, mutation.getId());
		for (TestMessage tm : mutationTestResult.getErrors()) {
			session.save(tm);
		}
		for (TestMessage tm : mutationTestResult.getFailures()) {
			session.save(tm);
		}
		session.save(mutationTestResult);
		m2.setMutationResult(mutationTestResult);
		// logger.info("ID" + mutation.getId());
		// logger.info(mutationTestResult);
		// session.update(m2);
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public static List<Mutation> getAllMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation order by linenumber");
		List l = query.list();
		List<Mutation> mutations = (List<Mutation>) l;
		tx.commit();
		session.close();
		return mutations;
	}

	@SuppressWarnings("unchecked")
	public static List<Mutation> getAllMutationsForClass(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where classname=:clName");
		query.setString("clName", className);
		List l = query.list();
		List<Mutation> mutations = (List<Mutation>) l;
		tx.commit();
		session.close();
		return mutations;
	}

	public static void saveMutation(Mutation mutation) {
		Mutation mutationInDb = getMutationOrNull(mutation);
		if (mutationInDb != null) {
			logger.info("Mutation already contained - not saving to db: "
					+ mutation);
			assert mutationInDb.equals(mutation);
		} else {
			save(mutation);
		}
	}

	public static void save(Object objectToSave) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(objectToSave);
		tx.commit();
		session.close();
	}

	public static String[] getTestCases(Mutation mutation) {
		return getTestCases(mutation.getClassName(), mutation.getLineNumber());
	}

	public static String[] getTestCases(String className, int lineNumber) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from TestCoverageClassResult as clazz join clazz.lineResults as lineres where clazz.className=:clname and lineres.lineNumber=:lnumber");
		query.setString("clname", className);
		query.setInteger("lnumber", lineNumber);
		query.setFetchSize(10);
		List l = query.list();
		assert l.size() <= 1;

		List<String> retList = null;
		if (l.size() >= 1) {
			Object[] array = (Object[]) l.get(0);
			if (array.length >= 2) {
				TestCoverageLineResult lineResult = (TestCoverageLineResult) array[1];
				List<TestCoverageTestCaseName> testCaseNames = lineResult
						.getTestCases();
				retList = new ArrayList<String>();
				for (TestCoverageTestCaseName name : testCaseNames) {
					retList.add(name.getTestCaseName());
				}
			}
		}
		tx.commit();
		session.close();
		if (retList == null) {
			logger.info("no testcases found for line " + lineNumber
					+ " of class " + className);
			return null;
		}
		logger.info("Found " + retList.size() + " testcases for line "
				+ lineNumber + " of class " + className);
		return retList.toArray(new String[0]);
	}

	public static boolean hasUnmutated(String className, int lineNumber) {
		boolean hasUnmutated = false;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation where classname=:clname and linenumber=:lnumber and mutationtype=:mtype");
		query.setString("clname", className);
		query.setInteger("lnumber", lineNumber);
		query.setInteger("mtype", MutationType.NO_MUTATION.ordinal());
		if (query.list().size() == 1) {
			hasUnmutated = true;
		}
		tx.commit();
		session.close();
		return hasUnmutated;
	}

	public static boolean shouldMutateClass(String className) {
		boolean shouldMutate = false;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Query query = session
				.createQuery("from Mutation where classname=:clname");
		query.setString("clname", className);
		if (query.list().size() > 1) {
			shouldMutate = true;
		}
		tx.commit();
		session.close();
		logger.info("Checking class" + className + " should be mutated: "
				+ shouldMutate);
		return shouldMutate;
	}

	public static Set<String> getClassNamesToMutate() {
		Set<String> resultSet = new HashSet<String>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("select distinct className from Mutation");
		List results = query.list();
		for (Object s : results) {
			resultSet.add(s.toString());
		}
		tx.commit();
		session.close();
		return resultSet;
	}

	public static void main(String[] args) {
		logger.info("Aaa");
		getClassNamesToMutate();
		logger.info("Bbb");
	}

	@SuppressWarnings("unchecked")
	public static Collection<Mutation> getAllMutationsForTestCases(
			Collection<String> tests) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		// String sqlQueryString = "SELECT mutation.* FROM
		// TESTCOVERAGETESTCASENAME AS tname,
		// TESTCOVERAGELINERESULT_TESTCOVERAGETESTCASENAME AS line_name,
		// TESTCOVERAGECLASSRESULT_TESTCOVERAGELINERESULT AS class_line,
		// TESTCOVERAGECLASSRESULT AS classResult, MUTATION AS mutation WHERE
		// tname.testcasename = :tcName AND line_name.testcases_id = tname.id
		// AND line_name.testcoveragelineresult_id = class_line.lineresults_id
		// AND class_line.testcoverageclassresult_id = classResult.id AND
		// classResult.classname = mutation.classname ";
		// Query query = session.createSQLQuery(sqlQueryString);
		String hibernateQuery = "SELECT DISTINCT m FROM Mutation AS m, TestCoverageClassResult AS tccr JOIN tccr.lineResults AS tclr WHERE tclr.testCases.testCaseName = :tcName AND tccr.className = m.className";
		Query query = session.createQuery(hibernateQuery);
		Set<Mutation> results = new HashSet<Mutation>();
		for (String testCaseName : tests) {
			query.setString("tcName", testCaseName);
			logger.info("TestCaseName: " + testCaseName);
			List queryResults = query.list();
			results.addAll(queryResults);
			for (Object o : queryResults) {
				logger.log(Level.INFO, "Type" + o.getClass());
			}
		}
		for (Object s : results) {
			System.out.println(s);
		}
		tx.commit();
		session.close();
		return results;
	}

	/**
	 * Checks if there are mutations for given class in the db.
	 *
	 * @param className
	 *            Name of the class.
	 * @return True, if there is on or more Mutation in the db.
	 */
	public static boolean hasMutationsforClass(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("SELECT count(*) from Mutation where classname=:clname");
		query.setString("clname", className);
		List results = query.list();
		long l = getResultFromCountQuery(results);
		tx.commit();
		session.close();
		return l > 0;
	}

	private static long getResultFromCountQuery(List results) {
		Long l = null;
		if (results.size() > 0 && results.get(0) instanceof Long) {
			l = (Long) results.get(0);
		} else {
			throw new RuntimeException("Expected a Long result");
		}
		return l.longValue();
	}

	public static boolean isCoveredMutation(Mutation mutation) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String hibernateQuery = "SELECT COUNT(*) FROM TestCoverageClassResult as tccr join tccr.lineResults as lineres where tccr.className=:clname and lineres.lineNumber=:lnumber";
		Query query = session.createQuery(hibernateQuery);
		query.setString("clname", mutation.getClassName());
		query.setInteger("lnumber", mutation.getLineNumber());
		List result = query.list();
		long l = getResultFromCountQuery(result);
		tx.commit();
		session.close();
		return l > 0;
	}
}
