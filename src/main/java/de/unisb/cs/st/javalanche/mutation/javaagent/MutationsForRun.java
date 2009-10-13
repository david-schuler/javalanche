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
package de.unisb.cs.st.javalanche.mutation.javaagent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.run.threaded.task.MutationTaskCreator;

/**
 * Class that holds all mutations that should be applied and executed during a
 * run.
 * 
 * @author David Schuler
 * 
 */
public class MutationsForRun {

	private static Logger logger = Logger.getLogger(MutationsForRun.class);

	/**
	 * List that holds the mutations that should be applied for this run (It
	 * will only contain mutations without results).
	 */
	private List<Mutation> mutations;

	/**
	 * @return an instance that contains all mutations for IDs from a file
	 *         specified at the command line (see
	 *         MutationProperties.MUTATION_FILE_NAME_KEY).
	 */
	public static MutationsForRun getFromDefaultLocation() {
		if (MutationProperties.SINGLE_TASK_MODE) {
			String fileName = findFile();
			return new MutationsForRun(fileName);
		} else {
			return new MutationsForRun(MutationProperties.MUTATION_FILE_NAME);
		}
	}

	private static String findFile() {
		File f = new File(MutationProperties.OUTPUT_DIR + '/'
				+ MutationTaskCreator.MUTATION_TASK_PROJECT_FILE_PREFIX
				+ "-01.txt");
		if (!f.exists()) {
			throw new RuntimeException("Did not find file " + f);
		}
		logger.info("Found task file " + f);
		return f.getAbsolutePath();
	}

	public MutationsForRun(String fileName) {
		mutations = getMutationsForRun(fileName);
		logger.info("Got " + mutations.size() + " mutations from file: "
				+ fileName);
		List<Long> ids = new ArrayList<Long>();
		for (Mutation m : mutations) {
			logger.debug("Mutation ID: " + m.getId());
			logger.debug(m);
			ids.add(m.getId());
		}
		String join = StringUtils.join(ids.toArray(), ", ");
		logger.info("Mutation Ids: " + join);
	}

	/**
	 * @return The names of the classes to mutate.
	 */
	public Collection<String> getClassNames() {
		Set<String> classNames = new HashSet<String>();
		for (Mutation m : mutations) {
			classNames.add(m.getClassName());
		}
		return classNames;
	}

	/**
	 * The list of mutations for this run. This list will only contain mutations
	 * without results, if they already have a result they will not be applied
	 * again.
	 * 
	 * @return the list of mutations for this run.
	 */
	public List<Mutation> getMutations() {
		return Collections.unmodifiableList(mutations);
	}

	/**
	 * Reads a list of mutation IDs from a file and fetches the corresponding
	 * mutations from the database. Mutations that already have a result are
	 * filtered such that they get not applied again.
	 * 
	 * @param fileName
	 * 
	 * @return a list of mutations for this run.
	 */
	private static List<Mutation> getMutationsForRun(String fileName) {
		List<Mutation> mutationsToReturn = new ArrayList<Mutation>();
		if (fileName != null) {
			File file = new File(fileName);
			if (file.exists()) {
				logger.info("Location of mutation file: "
						+ file.getAbsolutePath());
				mutationsToReturn = getMutationsByFile(file);
			} else {
				logger.warn("Mutation file does not exist: " + file);
			}
		} else {
			logger.warn("Passed null as a filename");
		}
		filterMutationsWithResult(mutationsToReturn);
		return mutationsToReturn;
	}
	
	/**
	 * Reads a list of mutation ids from a file and fetches the corresponding
	 * mutations from the database.
	 * 
	 * @param file
	 *            the file to read from
	 * @return a list of mutations read from the db.
	 */
	public static List<Mutation> getMutationsByFile(File file) {
		List<Long> idList = Io.getIDsFromFile(file);
		List<Mutation> returnList = null;
		if (idList.size() > 0) {
			returnList = QueryManager.getMutationsFromDbByID(idList
					.toArray(new Long[0]));
		} else {
			returnList = new ArrayList<Mutation>();
		}
		return returnList;

	}

	/**
	 * Removes the mutations that have a result from the given list of
	 * mutations.
	 * 
	 * @param mutations
	 *            the list of mutations to be filtered.
	 */
	private static void filterMutationsWithResult(List<Mutation> mutations) {
		if (mutations != null) {
			// make sure that we have not got any mutations that have already an
			// result
			Session session = HibernateUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			List<Mutation> toRemove = new ArrayList<Mutation>();
			for (Mutation m : mutations) {
				session.load(m, m.getId());
				if (m.getMutationResult() != null) {
					logger
							.debug("Found mutation that already has a mutation result "
									+ m);
					toRemove.add(m);
				}
			}
			mutations.removeAll(toRemove);
			tx.commit();
			session.close();
		}
	}

	/**
	 * 
	 * @param mutation
	 *            the mutation to check
	 * @return true, if the given mutation is a mutation for this run.
	 */
	public boolean containsMutation(Mutation mutation) {
		if (mutation != null) {
			for (Mutation m : mutations) {
				if (mutation.equalsWithoutId(m)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getAddInfo(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
