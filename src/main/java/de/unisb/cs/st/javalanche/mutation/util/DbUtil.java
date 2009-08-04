package de.unisb.cs.st.javalanche.mutation.util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class DbUtil {

	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String message = "Everything ok";
		try {
			Object object = session.get(Mutation.class, 1L);
			// session.save(mutation);
			// Long id = mutation.getId();
			// System.out.println(id);
		} catch (SQLGrammarException e) {
			message = "schemaexport";
			// ignore -> ant schema export
		} catch (GenericJDBCException e) {
			message = "start db";
		}
		tx.commit();
		session.close();
		System.out.println(message);
	}
}
