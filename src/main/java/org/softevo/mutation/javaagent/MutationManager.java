package org.softevo.mutation.javaagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class MutationManager {

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 * see http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
	 */
	private static class SingletonHolder {
		private final static MutationManager INSTANCE = new MutationManager();
	}


	private static final int MAX_MUTATIONS = 1000;

	public static MutationManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private List<Mutation> mutations;

	private MutationManager() {
		mutations = getMutationsFromDB();
	}

	public Collection<String> getClassNames(){
		Set<String> classNames = new HashSet<String>();
		for(Mutation m : mutations){
			classNames.add(m.getClassName());
		}
		return classNames;
	}

	public List<Mutation> getMutations(){
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
