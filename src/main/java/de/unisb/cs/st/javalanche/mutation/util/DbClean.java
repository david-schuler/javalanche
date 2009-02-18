package de.unisb.cs.st.javalanche.mutation.util;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import de.unisb.cs.st.javalanche.mutation.results.MutationCoverage;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;

public class DbClean {

	public static void main(String[] args) {
		int deleted = -1;
		int allDeleted = 0;
		while (deleted != 0) {
			deleted = cleanCoverage(allDeleted);
			allDeleted += deleted;
		}
	}

	private static int cleanCoverage(int initSize) {

		SessionFactory sessionFactory = HibernateServerUtil
				.getSessionFactory(Server.KUBRICK);
		Session s = sessionFactory.openSession();
		Transaction t = s.beginTransaction();
		int limit = 10000;
		String queryString = "SELECT mc.id FROM MutationCoverage mc LEFT OUTER JOIN Mutation m ON mc.mutationID = m.id WHERE  m.id IS NULL LIMIT "
				+ limit;
		SQLQuery createSQLQuery = s.createSQLQuery(queryString);
		List list = createSQLQuery.list();
		List<Long> ids = new ArrayList<Long>();
		int count = 0;
		for (Object object : list) {
			Long id = Long.valueOf(object.toString());
			ids.add(id);
			MutationCoverage o2 = (MutationCoverage) s.get(
					MutationCoverage.class, id);
			count++;
			s.delete(o2);
			if (count > limit) {
				break;
			}
			if (count % 20 == 0) {
				System.out.println(count + " Coverag results Deleted");// + o2.getTestsNames().size());
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				s.flush();
				s.clear();
			}
		}

		t.commit();
		s.close();
		return list.size();
	}

}
