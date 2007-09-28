package org.softevo.mutation.javaagent;

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
		private final static MutationForRun INSTANCE = new MutationForRun();
	}

	private static final int MAX_MUTATIONS = getMaxMutations();

	private static final String MUTATIONS_PER_RUN_KEY = "mutationsPerRun";

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

	private List<Mutation> mutations;

	private MutationForRun() {
		mutations = getMutationsFromDB();
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

	private static List<Mutation> getMutationsFromDB() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createSQLQuery(
						"SELECT m.* FROM Mutation m JOIN TestCoverageClassResult tccr ON m.classname = tccr.classname JOIN TESTCOVERAGECLASSRESULT_TESTCOVERAGELINERESULT AS class_line ON class_line.testcoverageclassresult_id = tccr.id JOIN TESTCOVERAGELINERESULT AS tclr ON tclr.id = class_line.lineresults_id 	WHERE m.mutationresult_id IS NULL AND m.linenumber = tclr.linenumber")
				.addEntity(Mutation.class);
		query.setMaxResults(MAX_MUTATIONS);
		List results = query.list();
		List<Mutation> mutationList = new ArrayList<Mutation>();
		for (Object m : results) {
			mutationList.add((Mutation) m);
		}
		tx.commit();
		session.close();
		return mutationList;
	}
}
