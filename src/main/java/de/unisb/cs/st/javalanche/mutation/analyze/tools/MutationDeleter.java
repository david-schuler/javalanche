/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.ds.util.Formater;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * 
 * Class with a main method that deletes all mutation with a specified prefix
 * from the database.
 * 
 * @author David Schuler
 * 
 */
public class MutationDeleter {

	private static Logger logger = Logger.getLogger(MutationDeleter.class);

	/**
	 * Deletes all mutation test results for classes with the specified
	 * MutationProperties.PROJECT_PREFIX.
	 */
	public static void deleteAllWithPrefix() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		String query = "FROM Mutation WHERE className LIKE '" + prefix + "%'";
		List<Long> idList = getIdList(query);
		logger.info("Deleting Coverage Data");
		// QueryManager.deleteCoverageResult(idList);
		MutationCoverageFile.deleteCoverageData();
		deleteFromQuery(query);
		String deleteTestsQuery = "FROM TestName WHERE project ='" + prefix
				+ "'";
		deleteFromQuery(deleteTestsQuery);

	}

	private static List<Long> getIdList(String query) {
		Session s = HibernateUtil.getSessionFactory().openSession();
		Transaction t = s.beginTransaction();
		Query query2 = s.createQuery(query);
		@SuppressWarnings("unchecked")
		List<Mutation> list = query2.list();
		List<Long> ids = new ArrayList<Long>();
		for (Mutation mutation : list) {
			ids.add(mutation.getId());
		}

		t.commit();
		s.close();
		return ids;
	}

	/**
	 * Deletes all mutations that match the given query.
	 * 
	 * @param queryString
	 *            query that is used to delete the mutations.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteFromQuery(String queryString) {
		logger.info("Deleting with query: " + queryString);
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(queryString);
		List list = q.list();
		int deletes = 0, flushs = 0;
		for (Object object : list) {
			session.delete(object);
			deletes++;
			if (deletes % 20 == 0) {
				// 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				long startFlush = System.currentTimeMillis();
				flushs++;
				logger.info("Doing temporary flush " + flushs);
				session.flush();
			}
		}
		// int deleted = q.executeUpdate();
		logger.info(String.format("Deleted %d entries", deletes));
		tx.commit();
		session.close();
	}

	/**
	 * Deletes all mutation with a specified prefix from the database.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args) {
		MutationProperties.checkProperty(MutationProperties.PROJECT_PREFIX_KEY);
		deleteAllWithPrefix();
	}
}
