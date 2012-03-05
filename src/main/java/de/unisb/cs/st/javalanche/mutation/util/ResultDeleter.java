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
package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
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
	 * 
	 * @param projectPrefix
	 */
	public static void deleteAllWithPrefix(String prefix) {
		String query = "FROM Mutation WHERE mutationResult IS NOT NULL AND className LIKE '"
				+ prefix + "%'";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes all mutation test results from the database.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteAllMutationResults() {
		String query = "from Mutation where mutationResult IS NOT NULL";
		deleteMutationResultsFromQuery(query);
	}

	/**
	 * Deletes results for all mutations that are returned by the given query.
	 * 
	 * @param query
	 *            query for which the mutations are deleted.
	 */
	@SuppressWarnings("unchecked")
	private static void deleteMutationResultsFromQuery(String query) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session.createQuery(query);
		deleteResults(session, q);
		tx.commit();
		session.close();
	}

	/**
	 * Deletes results for mutations with given ids.
	 * 
	 * @param ids
	 *            ids of the mutations for which the result is deleted.
	 */
	private static void deleteMutationResults(List<Long> ids) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query q = session
				.createQuery("FROM Mutation WHERE mutationResult IS NOT NULL AND id IN (:idList)");
		q.setParameterList("idList", ids);
		deleteResults(session, q);
		tx.commit();
		session.close();
	}

	public static void deleteResults(Session session, Query q) {
		@SuppressWarnings("unchecked")
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
				// session.clear();
				logger.info("Did flush. It took: "
						+ DurationFormatUtils.formatDurationHMS(stp.getTime()));
				deletes = 0;
			}
		}
		logger.info(String.format("Deleted %d mutation results",
				mutations.size()));
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
	 * Deletes mutation results from the database. If a project prefix is
	 * specified, all results of mutations for this project are deleted. If an
	 * id is given as argument the result of the mutation with the given id is
	 * deleted.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		boolean alreadyDeletedResults = false;
		if (args.length >= 1) {
			alreadyDeletedResults = handleArgument(args[0], projectPrefix);
		}
		if (!alreadyDeletedResults) {
			if (projectPrefix != null) {
				deleteAllWithPrefix(projectPrefix);
			} else {
				System.out
						.println("Did not delete results. No project prefix given.");
			}
		}
	}

	public static boolean handleArgument(String argument, String projectPrefix)
			throws IOException {
		if (argument.toLowerCase().equals("all")) {
			if (projectPrefix != null) {
				System.out.println("Deleting all mutation results with prefix "
						+ projectPrefix);
				deleteAllWithPrefix(projectPrefix);
				return true;
			} else {
				System.out
						.println("Deleting all mutation results in database.");
				deleteAllMutationResults();
				return true;
			}

		}

		long mutationId = 0;
		try {
			mutationId = Long.parseLong(argument);
		} catch (NumberFormatException e) {
		}
		if (mutationId != 0) {
			System.out.println("Deleting result for single mutation with id "
					+ mutationId);
			deleteMutationsResultsForId(mutationId);
			return true;
		}

		File f = new File(argument);
		if (f.exists()) {
			System.out
					.println("Deleting results for mutations with ids from file "
							+ f.getAbsolutePath());
			deleteResultsFromFile(f);
			return true;
		}
		return false;
	}

	private static void deleteResultsFromFile(File f) throws IOException {
		List<String> readLines = FileUtils.readLines(f);
		List<Long> ids = new ArrayList<Long>();
		for (String idString : readLines) {
			ids.add(Long.valueOf(idString));
		}
		deleteMutationResults(ids);
	}

}
