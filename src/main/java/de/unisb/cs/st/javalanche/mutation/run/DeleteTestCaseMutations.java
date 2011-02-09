/*
* Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.run;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

/**
 * Delete all Mutations from DB that have the word test in their classname.
 *
 * @author David Schuler
 *
 */
public class DeleteTestCaseMutations {

	private static Logger logger = Logger
			.getLogger(DeleteTestCaseMutations.class);

	public static void main(String[] args) {
		deleteMutations();
	}

	@SuppressWarnings("unchecked")
	private static void deleteMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "SELECT * from Mutation WHERE className LIKE '%test%'";
		Query query = session.createSQLQuery(queryString).addEntity(
				Mutation.class);
		List<Mutation> testCaseMutations = query.list();

		for (Mutation m : testCaseMutations) {
			if (m.getMutationResult() != null) {
				session.delete(m.getMutationResult());
			}
			session.delete(m);
		}
		logger.info("Deleted " + testCaseMutations.size() + " Mutations");
		tx.commit();
		session.close();
	}
}
