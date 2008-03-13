package org.softevo.mutation.run.threaded.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.persistence.QueryManager;

/**
 * Creates Mutation tasks that can later be executed on multiple JVMs
 *
 * @author David Schuler
 *
 */
public class MutationTaskCreator {

	/**
	 * Prefix for the task files.
	 */
	public static final String MUTATION_TASK_FILE_PREFIX = "mutation-task-";

	private static final String MUTATION_TASK_FILE_FORMAT = MUTATION_TASK_FILE_PREFIX
			+ "%02d.txt";

	private static Logger logger = Logger.getLogger(MutationTaskCreator.class);

	/**
	 * Number of tasks that will be created.
	 */
	private static final int NUMBER_OF_TASKS = 18;// 100;

	/**
	 * Number of mutations per task.
	 */
	private static final int MUTATIONS_PER_TASK = 50;// 1000;

	public static void createMutationTasks() {
		createMutationTasks(NUMBER_OF_TASKS, MUTATIONS_PER_TASK);
	}

	/**
	 * Generates given number of mutation task, where each task hasconsists of a
	 * given number of mutations.
	 *
	 * @param numberOfTasks number of tasks that should be created
	 * @param mutationsPerTask number of mutations per task
	 */
	public static void createMutationTasks(int numberOfTasks,
			int mutationsPerTask) {
		String prefix = MutationProperties.PROJECT_PREFIX;
		int numberOfIds = numberOfTasks * mutationsPerTask;
		List<Long> mutationIds = getMutations(prefix, numberOfIds);
		for (int i = 1; i <= numberOfTasks; i++) {
			List<Long> idsForTask = new ArrayList<Long>();
			if (mutationIds.size() >= mutationsPerTask) {
				idsForTask.addAll(mutationIds.subList(0, mutationsPerTask));
			} else {
				logger.info("Not enough mutations fetched from db");
				idsForTask.addAll(mutationIds);
			}
			mutationIds.removeAll(idsForTask);
			writeListToFile(idsForTask, i);
		}

	}

	private static List<Long> getMutations(String prefix, int numberOfIds) {
		logger.info("Trying to fetch " + numberOfIds + " mutations");
		List<Long> mutationIds = QueryManager.getMutationsIdListFromDb(
				numberOfIds, prefix);
		logger.info("Got " + mutationIds.size() + " mutations");
		return mutationIds;
	}

	private static File writeListToFile(List<Long> list, int id) {
		String filename = String.format(MutationProperties.RESULT_DIR
				+ MUTATION_TASK_FILE_FORMAT, id);
		File resultFile = new File(filename);
		StringBuilder sb = new StringBuilder();
		for (Long l : list) {
			sb.append(l);
			sb.append("\n");
		}
		Io.writeFile(sb.toString(), resultFile);
		return resultFile;
	}

	public static void main(String[] args) {
		createMutationTasks();
	}
	// public fetchRandomMutations() {
	// long startTime = System.currentTimeMillis();
	// logger.info("Start fetching " + MAX_MUTATIONS + " mutations");
	// refreshMutations();
	// // mutationIDs = getFakeList();
	// long fetchTime = System.currentTimeMillis();
	// logger.info("Fetched " + mutationIDs.size() + " mutations in "
	// + Formater.formatMilliseconds(fetchTime - startTime));
	// }

	// private static List<Long> getMutionIDs(int numberOfIds) {
	// List<Long> list = new ArrayList<Long>();
	// Random r = new Random();
	// for (int i = 0; i < numberOfIds; i++) {
	// if (mutationIDs.size() > 0) {
	// int position = r.nextInt(mutationIDs.size());
	// list.add(mutationIDs.remove(position));
	// } else {
	// logger.info("Not enough mutations fetched from db");
	// break;
	// }
	// }
	// return list;
	// }

}
