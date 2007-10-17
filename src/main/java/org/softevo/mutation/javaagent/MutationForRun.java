package org.softevo.mutation.javaagent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;

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

	private static final int MAX_MUTATIONS = getMaxMutations();

	private static final String MUTATIONS_PER_RUN_KEY = "mutationsPerRun";

	private static final boolean NON_RANDOM = true;


	private List<Mutation> mutations;


	public static MutationForRun getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static int getMaxMutations() {
		String mutationsPerRun = System.getProperty(MUTATIONS_PER_RUN_KEY);
		if (mutationsPerRun != null) {
			int mutations = Integer.parseInt(mutationsPerRun);
			return mutations;
		}
		return 0;
	}


	private MutationForRun() {
		mutations = getMutationsForRun();
		logger.info("Aplying " + mutations.size() + " mutations");
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
		if (System.getProperty("mutation.file") != null) {
			logger.info("Found Property mutation.file");
			String filename = System.getProperty("mutation.file");

			if (!filename.equals("")) {
				logger.info("Value of mutation file: " + filename);
				File file = new File(filename);
				if (file.exists()) {
					logger.info("Location of mutation.file: "
							+ file.getAbsolutePath());
					return getMutationsByFile(file);
				} else {
					logger.info("Mutation file does not exist" + file);
				}
			}
		} else {
			logger.info("Property not found: mutation.file");
			// throw new RuntimeException("property not found");
		}
		if (NON_RANDOM) {
			return getMutationsFromDB();
		}
		return null;
	}

	private static List<Mutation> getMutationsFromDB() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createSQLQuery(
						"SELECT m.* FROM Mutation m JOIN TestCoverageClassResult tccr ON m.classname = tccr.classname JOIN TestCoverageClassResult_TestCoverageLineResult AS class_line ON class_line.testcoverageclassresult_id = tccr.id JOIN TestCoverageLineResult AS tclr ON tclr.id = class_line.lineresults_id 	WHERE m.mutationresult_id IS NULL AND m.linenumber = tclr.linenumber")
				.addEntity(Mutation.class);

		query.setMaxResults(MAX_MUTATIONS);
		List results = query.list();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		for (Object m : results) {
			Mutation mutation = (Mutation) m;
			// Query hqlQuery = session.createQuery("Mutation as m inner join
			// fetch m.mutationResult inner join fetch m.mutationResult.failures
			// inner join fetch m.mutationResult.errors inner join fetch
			// m.mutationResult.passing WHERE m.id = :id" );
			// hqlQuery.setLong("id", mutation.getId());
			// Mutation mutationToAdd = (Mutation) hqlQuery.uniqueResult();
			Mutation mutationToAdd = mutation;
			logger.info(mutationToAdd);
			mutationList.add(mutationToAdd);
		}
		tx.commit();
		session.close();
		return mutationList;
	}

	private static List<Mutation> getMutationsByFile(File file) {
		List<Long> idList = getIDsFromFile(file);
		return getMutationsFromDbByID(idList.toArray(new Long[0]));
	}

	private static List<Long> getIDsFromFile(File file) {
		List<Long> idList = new ArrayList<Long>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				String id = br.readLine();
				idList.add(Long.valueOf(id));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idList;
	}

	private static List<Mutation> getMutationsFromDbByID(Long[] ids) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		// Query query = session
		// .createQuery("FROM Mutation m inner join fetch m.mutationResult inner
		// join fetch m.mutationResult.failures inner join fetch
		// m.mutationResult.errors inner join fetch m.mutationResult.passing
		// WHERE m.id IN (:ids)");
		Query query = session
				.createQuery("FROM Mutation m  WHERE m.id IN (:ids)");

		query.setParameterList("ids", ids);
		List results = query.list();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		for (Object m : results) {
			mutationList.add((Mutation) m);
		}
		tx.commit();
		session.close();
		return mutationList;
	}

	public void reinit() {
		mutations = getMutationsForRun();
		logger.info("Got " + mutations.size() + " mutations");
	}

	public boolean containsMutation(Mutation mutationFromDb) {
		return mutations.contains(mutationFromDb);
	}
}
