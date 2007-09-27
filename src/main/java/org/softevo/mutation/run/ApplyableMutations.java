package org.softevo.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class ApplyableMutations {

	private static Logger logger = Logger.getLogger(ApplyableMutations.class);


	public static Mutation[] getMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String sqlQuery = "	";
		Query query = session.createSQLQuery(sqlQuery).addEntity(Mutation.class);
		query.setMaxResults(100);
		List results = query.list();
		for (Object object : results) {
			logger.info(object);
		}
		tx.commit();
		session.close();
		return null;
	}
}
