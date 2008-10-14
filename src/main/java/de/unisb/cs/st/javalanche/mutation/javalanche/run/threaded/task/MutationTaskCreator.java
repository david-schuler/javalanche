package de.unisb.cs.st.javalanche.mutation.run.threaded.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

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

	public static final String MUTATION_TASK_PROJECT_FILE_PREFIX = MUTATION_TASK_FILE_PREFIX
			+ MutationProperties.PROJECT_PREFIX.replace('.', '_');

	private static final String MUTATION_TASK_FILE_FORMAT = MUTATION_TASK_PROJECT_FILE_PREFIX
			+ "-%02d.txt";

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
		deleteTasks();
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

	private static void deleteTasks() {
		File dir = new File(MutationProperties.RESULT_DIR);

		File[] toDelete = dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.startsWith(MUTATION_TASK_PROJECT_FILE_PREFIX)) {
					return true;
				}
				return false;
			}

		});
		if (toDelete == null) {
			logger.info("Got no files to delete in directory" + dir);
			return;
		}
		for (File d : toDelete) {
			boolean delete = d.delete();
			if (delete) {
				logger.info("Deleted task: " + d);
			} else {
				logger.info("Could not delete task: " + d);
			}
		}
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
				numberOfIds, prefix, numberOfIds);
		logger.info("Got " + mutationIds.size() + " mutations");
		return mutationIds;
	}

	private static File writeListToFile(List<Long> list, int id) {
		String filename = getFilename(id);
		File resultFile = new File(filename);
		StringBuilder sb = new StringBuilder();
		for (Long l : list) {
			sb.append(l);
			sb.append("\n");
		}
		Io.writeFile(sb.toString(), resultFile);
		return resultFile;
	}

	private static String getFilename(int id) {
		String filename = String.format(MutationProperties.RESULT_DIR
				+ MUTATION_TASK_FILE_FORMAT, id);
		return filename;
	}

	public static void main(String[] args) {
		MutationProperties.checkProperty(MutationProperties.PROJECT_PREFIX_KEY);
		createMutationTasks();
	}

}
