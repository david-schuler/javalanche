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
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Singleton class that holds all mutations that should be applied and executed
 * during a run.
 *
 * @author David Schuler
 *
 */
public class MutationForRun {

	private static Logger logger = Logger.getLogger(MutationForRun.class);

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before. see
	 * http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
	 */
	private static class SingletonHolder {
		private static MutationForRun INSTANCE = new MutationForRun();
	}

	/**
	 * List that holds the mutations that should be applied for this run (It
	 * will only contain mutations without results).
	 */
	private List<Mutation> mutations;

	/**
	 * Contains all mutations that are reported to have been applied.
	 */
	private Set<Mutation> appliedMutations = new HashSet<Mutation>();

	/**
	 * @return the instance of this singleton.
	 */
	public static MutationForRun getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private MutationForRun() {
		mutations = getMutationsForRun();
		logger.info("Applying " + mutations.size() + " mutations");
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
	 * without results, if they already have a result they will not be apllied
	 * again.
	 *
	 * @return the list of mutations for this run.
	 */
	public List<Mutation> getMutations() {
		return Collections.unmodifiableList(mutations);
	}

	/**
	 * Reads a list of mutation ids from a file and fetches the corresponding
	 * mutations from the database. Mutations that already have a result are
	 * filtered such that they get not applied again.
	 *
	 * @return a list of mutations for this run.
	 */
	private static List<Mutation> getMutationsForRun() {
		List<Mutation> mutationsToReturn = new ArrayList<Mutation>();
		if (MutationProperties.MUTATION_FILE_NAME != null) {
			logger.debug("Value of mutation file: "
					+ MutationProperties.MUTATION_FILE_NAME);
			File file = new File(MutationProperties.MUTATION_FILE_NAME);
			if (file.exists()) {
				logger.info("Location of mutation file: "
						+ file.getAbsolutePath());
				mutationsToReturn = getMutationsByFile(file);
			} else {
				logger.info("Mutation file does not exist " + file);
			}
		} else {
			logger.info("Property not found: "
					+ MutationProperties.MUTATION_FILE_KEY);
		}
		filterMutationsWithResult(mutationsToReturn);
		return mutationsToReturn;
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
	 * Reinitializes this singleton - used for unit testing.
	 */
	public void reinit() {
		mutations = getMutationsForRun();
		logger.info("Got " + mutations.size() + " mutations");
	}

	/**
	 *
	 * @param mutation
	 *            the mutation to check
	 * @return true, if the given mutation is a mutation for this run.
	 */
	public boolean containsMutation(Mutation mutation) {
		return hasMutation(mutation);
	}

	/**
	 * Helper method for containsMutation.
	 */
	private boolean hasMutation(Mutation searchMutation) {
		if (searchMutation != null) {
			for (Mutation m : mutations) {
				if (searchMutation.equalsWithoutId(m)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method that is called by the instrumentation classes to signalize that
	 * the given mutation was applied.
	 *
	 * @param mutation
	 *            the mutation that was applied.
	 */
	public static void mutationApplied(Mutation mutation) {
		getInstance()._mutationApplied(mutation);
	}

	private void _mutationApplied(Mutation mutation) {
		appliedMutations.add(mutation);
	}

	/**
	 * Called at the end of mutation testing. Prints a message, and an error
	 * message if one or more mutations where not applied.
	 */
	public void reportAppliedMutations() {
		List<Mutation> notApplied = new ArrayList<Mutation>();
		int applied = 0;
		boolean showMutations = mutations.size() < 5;
		List<Long> appliedIds = new ArrayList<Long>();
		for (Mutation m : appliedMutations) {
			appliedIds.add(m.getId());
		}
		for (Mutation m : mutations) {
			if (appliedIds.contains(m.getId())) {
				applied++;
				if (showMutations) {
					logger.info("Applied Mutation: "
							+ QueryManager.mutationToShortString(m));
				}
			} else {
				notApplied.add(m);
			}
		}
		logger.info(applied + " Mutations out of " + mutations.size()
				+ " where applied to bytecode");
		if (applied < mutations.size() || notApplied.size() > 0) {
			logger.error("Not all mutations where applied to bytecode");
			logger.error(appliedMutations);
			logger.error(mutations);
			for (Mutation mutation : notApplied) {
				logger.warn("Mutation not applied " + mutation.getId());
			}
		}
	}

	/**
	 * @return the number of applied mutations.
	 */
	public static int getNumberOfAppliedMutations() {
		return getInstance().appliedMutations.size();
	}

	/**
	 * @return a set of all applied mutations.
	 */
	public static Set<Mutation> getAppliedMutations() {
		return Collections.unmodifiableSet(getInstance().appliedMutations);
	}

	/**
	 * @return true, if one or more mutations do not have a result.
	 */
	public static boolean hasMutationsWithoutResults() {
		for (Mutation m : getInstance().getMutations()) {
			if (m.getMutationResult() == null) {
				return true;
			}
		}
		return false;
	}
}
