package org.softevo.mutation.results.persistence;

import java.io.FileWriter;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;

public class QueryManager {

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
					.createQuery("from Mutation as m where m.className=:name and m.lineNumber=:number and mutationType=:type");
			query.setParameter("name", mutation.getClassName());
			query.setParameter("number", mutation.getLineNumber());
			query.setParameter("type", mutation.getMutationType());
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
		mutation.setMutationResult(mutationTestResult);
		session.update(mutation);
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public static List<Mutation> getAllMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation");
		List l = query.list();
		List<Mutation> mutations = (List<Mutation>) l;
		tx.commit();
		session.close();
		return mutations;
	}

	public static void saveMutation(Mutation mutation) {
		save(mutation);
	}

	public static void save(Object objectToSave) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(objectToSave);
		tx.commit();
		session.close();
	}

	public static void getLineNumber(String className, int lineNumber) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from TestCoverageClassResult as classe where class.className=:clname");
		query.setString("clname", className);
		query.setFetchSize(10);
		List l  = query.list();
		System.out.println(l.size());
		tx.commit();
		session.close();
	}

}
