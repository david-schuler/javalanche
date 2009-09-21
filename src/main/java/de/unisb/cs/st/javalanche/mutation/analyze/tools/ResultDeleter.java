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
package de.unisb.cs.st.javalanche.mutation.analyze.tools;

import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

/**
 * Deletes the mutation test results from the database.
 *
 * @author David Schuler
 *
 */
public class ResultDeleter {

	private static Logger logger = Logger.getLogger(ResultDeleter.class);

	/**
	 * Deletes all mutation test results for classes with the specified
	 * MutationProperties.PROJECT_PREFIX.
	 */
	public static void deleteAllWithPrefix() {
		String prefix = MutationProperties.PROJECT_PREFIX;
		String query = "FROM Mutation WHERE mutationResult IS NOT NULL AND className LIKE '"
				+ prefix + "%'";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes all mutation test results from the database.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteAllMutationResult() {
		String query = "from Mutation where mutationResult IS NOT NULL";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes results for all mutations that are returned by the given qeury.
	 *
	 * @param query
	 *            Query to get the mutations that are
	 */
	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String query) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(query);
		List<Mutation> mutations = q.list();
		int deletes = 0, flushs = 0;
		StopWatch stp = new StopWatch();
		for (Mutation m : mutations) {
			MutationTestResult result = m.getMutationResult();
			if (result != null) {
				m.setMutationResult(null);
				session.delete(result);
				deletes++;
			}
			if (deletes > 20) {
				// 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				stp.reset();
				stp.start();
				flushs++;
				session.flush();
//				session.clear();
				logger.info("Did flush. It took: "
						+ DurationFormatUtils.formatDurationHMS(stp.getTime()));
				deletes = 0;
			}
		}
		logger.info(String.format("Deleted %d mutation results", mutations
				.size()));
		tx.commit();
		session.close();
	}

	/**
	 * Delet mutation result for mutation with given id.
	 *
	 * @param id
	 *            the id of the mutation to delete
	 */
	private static void deleteMutationsResultsForId(long id) {
		String query = "FROM Mutation WHERE id=" + id;
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes mutation resutlts from the database. If a project prefix is
	 * specified, all results of mutations for this project are deleted. If an
	 * id is given as argument the result of the mutation with the given id is
	 * deleted.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (MutationProperties.PROJECT_PREFIX != null) {
			deleteAllWithPrefix();
		} else {
			if (args.length >= 1) {
				if (args[0].toLowerCase().equals("all")) {
					if (MutationProperties.PROJECT_PREFIX != null) {
						deleteAllWithPrefix();
					} else {
						deleteAllMutationResult();
					}
				} else {
					deleteMutationsResultsForId(Long.parseLong(args[0]));
				}
			} else {
				System.out.print("Specify an option: a file or all ");
			}
		}
	}

}
