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
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationForRun {

	private static final String MUTATION_FILE = "mutation.file";

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

	private List<Mutation> mutations;

	private List<Mutation> appliedMutations = new ArrayList<Mutation>();

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
		logger.info("Mutaiton Ids: " + join);
	}

	public Collection<String> getClassNames() {
		Set<String> classNames = new HashSet<String>();
		for (Mutation m : mutations) {
			classNames.add(m.getClassName());
		}
		return classNames;
	}

	public List<Mutation> getMutations() {
		return Collections.unmodifiableList(mutations);
	}

	private static List<Mutation> getMutationsForRun() {
		List<Mutation> mutationsToReturn = new ArrayList<Mutation>();
		if (System.getProperty(MUTATION_FILE) != null) {
			logger.debug("Found Property mutation.file");
			String filename = System.getProperty(MUTATION_FILE);
			if (!filename.equals("")) {
				logger.debug("Value of mutation file: " + filename);
				File file = new File(filename);
				if (file.exists()) {
					logger.info("Location of mutation file: "
							+ file.getAbsolutePath());
					mutationsToReturn = getMutationsByFile(file);
				} else {
					logger.info("Mutation file does not exist " + file);
				}
			}
		} else {
			logger.info("Property not found: " + MUTATION_FILE);
		}
		// if (mutationsToReturn.size() == 0) {
		// mutationsToReturn =
		// QueryManager.getCoveredMutationListFromDb(DEFAULT_MUTATIONS_PER_RUN);
		// }
		filterMutationsWithResult(mutationsToReturn);
		return mutationsToReturn;
	}

	private static void filterMutationsWithResult(List<Mutation> mutations) {
		int preSize = mutations.size();
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
		if (mutations == null || mutations.size() == 0) {
			if (mutations == null || preSize == 0) {
				logger.error("No mutations for this run - exiting now");
			} else {
				logger.info("All mutations got results - exiting now");
			}
			System.out.println("ALL_RESULTS");
			System.out
					.println("All mutations have results - They have already been aplied and executed");

			if (MutationProperties.RUN_MODE == RunMode.MUTATION_TEST_INVARIANT) { // TODO
				// INTRODUCE
				// own
				// testRunMode
				System.exit(0);
			}
		}
	}

	private static List<Mutation> getMutationsByFile(File file) {
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

	public void reinit() {
		mutations = getMutationsForRun();
		logger.info("Got " + mutations.size() + " mutations");
	}

	public boolean containsMutation(Mutation mutation) {
		boolean result = hasMutation(mutation);
		if (result) {
			logger.debug("mutation contained:  " + mutation);
		} else {
			logger.debug("mutation not contained:  " + mutation);
		}
		return result;
	}

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

	public static void mutationApplied(Mutation mutation) {
		getInstance()._mutationApplied(mutation);
	}

	private void _mutationApplied(Mutation mutation) {
		appliedMutations.add(mutation);
	}

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

	public static int getNumberOfAppliedMutations() {
		return getInstance().appliedMutations.size();
	}

	public static List<Mutation> getAppliedMutations() {
		return getInstance().appliedMutations;
	}
}
