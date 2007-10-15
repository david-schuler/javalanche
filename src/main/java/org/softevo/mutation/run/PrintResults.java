package org.softevo.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class PrintResults {

	private static Logger logger = Logger.getLogger(PrintResults.class);

	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE mutationResult IS NOT NULL");
		query.setMaxResults(200);
		List results = query.list();
		for (Object object : results) {
			logger.info(object);
		}
		tx.commit();
		session.close();
	}

}
