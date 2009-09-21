/*
* Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.util;

import static de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.*;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.results.Invariant;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;

public class MutationMerger {




	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		for (Server s : new Server[] { Server.KUBRICK }) {
			Session session = getSessionFactory(s).openSession();
			Transaction transaction = session.beginTransaction();
			Query query = session
					.createQuery("from Mutation fetch all properties WHERE mutationResult!=null ");
//			Criteria cr = session.createCriteria(Mutation.class);
//			cr.setFetchMode("mutationResult", FetchMode.JOIN);
//			cr.setFetchMode("mutationResult.passing", FetchMode.JOIN);
//			cr.setFetchMode("mutationResult.failing", FetchMode.JOIN);
//			cr.setFetchMode("mutationResult.errors", FetchMode.JOIN);

			query.setMaxResults(100);
			List<Mutation> list = query.list();
			System.out.println(s + "  " + list.size());
			for (Mutation mutation : list) {
				mutation.loadAll();
			}
			transaction.commit();
			session.close();
			Session localSession = getSessionFactory(Server.LOCALHOST).openSession();
			Transaction localTransaction = localSession.beginTransaction();
			for (Mutation m : list) {
				int count = 0;
				System.out.println(m.getId());
				saveTM(m, localSession);
				localSession.save(m.getMutationResult());
				localSession.save(m);
				System.out.println(m.getMutationResult().getDate());
				// System.out.println(m.getMutationResult());
				count++;
				if (count % 5 == 0) {
					localSession.flush();
					localSession.clear();
				}
			}
			localTransaction.commit();
			localSession.close();
		}
	}

	private static void saveTM(Mutation m, Session localSession) {
		MutationTestResult mutationResult = m.getMutationResult();
		if (mutationResult != null) {
			for (TestMessage tm : mutationResult.getPassing()) {
				localSession.save(tm);
			}
			for (TestMessage tm : mutationResult.getFailures()) {
				localSession.save(tm);
			}
			for (TestMessage tm : mutationResult.getErrors()) {
				localSession.save(tm);
			}
			for (Invariant in : mutationResult.getInvariants()) {
				localSession.save(in);
			}
		}
	}

}
