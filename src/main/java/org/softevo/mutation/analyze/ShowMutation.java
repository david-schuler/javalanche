package org.softevo.mutation.analyze;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class ShowMutation {

	public static void main(String[] args) {
		showMutation();
	}

	@SuppressWarnings("unchecked")
	private static void showMutation() {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("FROM Mutation WHERE id = :id");
		query.setLong("id", 300l);
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		for (Mutation mutation : mutations) {
			System.out.println(mutation);
		}
		tx.commit();
		session.close();
	}
}
