package de.unisb.cs.st.javalanche.mutation.evolutionary;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.analyze.tools.ResultDeleter;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class StepCreator {

	public static final String DEFAULT_TASK_FILE = MutationProperties.OUTPUT_DIR
			+ "/task-file.txt";
	private static Logger logger = Logger.getLogger(StepCreator.class);
	static Random r = new Random();

	public static void createNextStep() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List<Mutation> previousResults = QueryManager.getMutationsForProject(
				MutationProperties.PROJECT_PREFIX, session);
		for (Mutation m : previousResults) {
			if (m.getMutationResult() != null) {
				logger.info("Got result for mutation: " + m);
			} else {
				logger.info("Got no result for mutation: " + m);
			}

		}
		tx.commit();
		session.close();
		// Delete all results - processing must be done earlier.
		ResultDeleter.deleteAllWithPrefix();
		logger.info("Deleted results");
		Set<Long> covered = MutationCoverageFile.getCoveredMutations();
		List<Long> mutationIds = QueryManager.getMutationsWithoutResult(
				covered, 10);
		logger.info("Got " + mutationIds.size() + " mutations");
		Map<Long, String> stepInfoMap = new HashMap<Long, String>();
		for (Long id : mutationIds) {
			stepInfoMap.put(id, (r.nextInt(4) + 1) + "");
		}

		StepInfo stepInfo = new StepInfo(stepInfoMap);
		stepInfo.writeToDefaultLocation();
		writeTaskFile(mutationIds);
	}

	private static void writeTaskFile(List<Long> mutationIds) {
		StringBuilder sb = new StringBuilder();
		for (Long id : mutationIds) {
			sb.append(id).append('\n');
		}
		Io.writeFile(sb.toString(), new File(DEFAULT_TASK_FILE));
	}

	public static void main(String[] args) {
		createNextStep();
	}
}
