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
package de.unisb.cs.st.javalanche.mutation.run.threaded.task;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.HandleUnsafeMutations;

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

	private static final String MUTATION_FIXED_NUMBER_OF_TASKS_KEY = "javalanche.fixed.number.of.tasks";

	private static final String TASK_DIR = getTaskDir();

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
		String mutationTargetTasks = System
				.getProperty(MUTATION_FIXED_NUMBER_OF_TASKS_KEY);

		if (mutationTargetTasks != null) {
			numberOfTasks = Integer.parseInt(mutationTargetTasks);
			Set<Long> coveredMutations = MutationCoverageFile
					.getCoveredMutations();

			List<Long> mutationIds = QueryManager.getMutationsWithoutResult(
					coveredMutations, 0);
			int size = mutationIds.size();
			mutationsPerTask = (int) Math.ceil(size * 1. / numberOfTasks);
		}
		createMutationTasks(numberOfTasks, mutationsPerTask);
	}

	private static String getTaskDir() {
		String property = System.getProperty("javalanche.mutation.output.dir");
		if (property == null) {
			property = MutationProperties.OUTPUT_DIR;
		}
		return property;
	}

	private static void deleteTasks() {
		File dir = new File(TASK_DIR);

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
	 * given number of mutations. Note: The MutationProperties.PROJECT_PREFIX
	 * variable has to be set.
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
		Collections.shuffle(mutationIds);
		int i = 1;
		for (; i <= numberOfTasks; i++) {
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
				i = i - 1;
				break;
			}
		}
		System.out.println("Created " + i + " mutation tasks");
	}

	private static List<Long> getMutations(String prefix, int limit) {
		logger.info("Trying to fetch " + limit + " mutations");
		Set<Long> covered = MutationCoverageFile.getCoveredMutations();
		List<Long> mutationIds = QueryManager.getMutationsWithoutResult(
				covered, limit);

		logger.info("Covered Mutations " + covered.size());
		logger.info("Got " + mutationIds.size() + " mutations");
		return mutationIds;
	}

	private static File writeListToFile(List<Long> list, int id) {
		String filename = String.format(MUTATION_TASK_FILE_FORMAT, id);
		File resultFile = new File(MutationProperties.OUTPUT_DIR, filename);
		StringBuilder sb = new StringBuilder();
		for (Long l : list) {
			sb.append(l);
			sb.append("\n");
		}
		Io.writeFile(sb.toString(), resultFile);
		try {
			System.out
					.println("Task created: " + resultFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultFile;
	}

	public static void main(String[] args) {
		MutationProperties.checkProperty(MutationProperties.PROJECT_PREFIX_KEY);
		HandleUnsafeMutations.handleUnsafeMutations(HibernateUtil
				.getSessionFactory());
		createMutationTasks();
	}

}
