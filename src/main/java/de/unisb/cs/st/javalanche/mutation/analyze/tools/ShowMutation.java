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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil;

/**
 * Fetches one or more mutations from the database an prints it to the console.
 */
public class ShowMutation {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <mutationID> [<mutationID>]*");
			System.out.println("Showing one default mutation");
			showMutation(70308);
		}
		for (int i = 0; i < args.length; i++) {
			long mutationID = Long.parseLong(args[i]);
			showMutation(mutationID);
		}
	}

	/**
	 * Fetches one a mutation from the database an prints it to the console.
	 * 
	 * @param id
	 *            the id of the mutation to print
	 */
	@SuppressWarnings("unchecked")
	private static void showMutation(long id) {

		SessionFactory sessionFactory = HibernateServerUtil
				.getSessionFactory(HibernateServerUtil.Server.HOBEL);
		QueryManager.setSessionFactory(sessionFactory);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("FROM Mutation WHERE id = :id");
		query.setLong("id", id);
		@SuppressWarnings("unchecked")
		List<Mutation> mutations = query.list();
		int count = 0;
		for (Mutation mutation : mutations) {
			System.out.println((count++) + "  " + mutation);
			System.out.println("TestCases for mutation: "
					+ MutationCoverageFile.getCoverageDataId(mutation.getId())
							.size());
			boolean coveredMutation = QueryManager.isCoveredMutation(mutation);
			System.out.println("Is covered: " + coveredMutation);
		}
		tx.commit();
		session.close();
	}
}
