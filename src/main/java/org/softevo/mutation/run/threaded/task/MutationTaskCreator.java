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
	 * Default number of tasks that will be created.
	 */
	private static final int DEFAULT_NUMBER_OF_TASKS = 20;

	/**
	 * Default number of mutations per task.
	 */
	private static final int DEFAULT_MUTATIONS_PER_TASK = 40;

	private static final String MUTATION_PER_TASK_KEY = "mutation.mutations.per.task";

	private static final String MUTATION_NUMBER_OF_TASKS_KEY = "mutation.number.of.tasks";

	public static void createMutationTasks() {
		int numberOfTasks = DEFAULT_NUMBER_OF_TASKS;
		int mutationsPerTask = DEFAULT_MUTATIONS_PER_TASK;
		String numberOfTasksProperty = System
				.getProperty(MUTATION_NUMBER_OF_TASKS_KEY);
		if (numberOfTasksProperty != null) {
			numberOfTasks = Integer.parseInt(numberOfTasksProperty);
		}
		String mutationsPerTaskProperty = System
				.getProperty(MUTATION_PER_TASK_KEY);
		if (mutationsPerTaskProperty != null) {
			mutationsPerTask = Integer.parseInt(mutationsPerTaskProperty);
		}
		createMutationTasks(numberOfTasks, mutationsPerTask);
	}

	/**
	 * Generates given number of mutation task, where each task consists of a
	 * given number of mutations. Note: The
	 * {@link MutationProperties.PROJECT_PREFIX} variable has to be set.
	 *
	 * @param numberOfTasks
	 *            number of tasks that should be created
	 * @param mutationsPerTask
	 *            number of mutations per task
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
			if (idsForTask.size() > 0) {
				writeListToFile(idsForTask, i);
			} else {
				logger.info("No more mutations. Finishing after file "
						+ (i - 1));
				break;

			}
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

}
