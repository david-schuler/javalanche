package org.softevo.mutation.results.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
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
		m2.setMutationResult(mutationTestResult);
//		session.save(mutationTestResult);
//		logger.info("ID" + mutation.getId());
//		logger.info(mutationTestResult);
		session.update(m2);
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
		// Query query = session
		// .createQuery("from TestCoverageClassResult as clazz where
		// clazz.className=:clname and clazz.lineNumber=:lnumber");

		// Query query = session
		// .createQuery("from TestCoverageClassResult as clazz where
		// clazz.className=:clname");

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
		Query query = session.createQuery("select className from Mutation");
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

}
