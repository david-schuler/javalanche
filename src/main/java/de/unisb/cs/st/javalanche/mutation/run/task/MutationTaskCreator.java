/*
 * Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.run.task;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
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
			+ ConfigurationLocator.getJavalancheConfiguration()
					.getProjectPrefix().replace('.', '_');

	private static final String MUTATION_TASK_FILE_FORMAT = MUTATION_TASK_PROJECT_FILE_PREFIX
			+ "-%02d.txt";

	private static Logger logger = Logger.getLogger(MutationTaskCreator.class);

	/**
	 * Default number of tasks that will be created.
	 */
	private static final int DEFAULT_NUMBER_OF_TASKS = 1000;

	/**
	 * Default number of mutations per task.
	 */
	private static final int DEFAULT_MUTATIONS_PER_TASK = 400;

	public static final String MUTATION_PER_TASK_KEY = "javalanche.mutations.per.task";

	private static final String MUTATION_FIXED_NUMBER_OF_TASKS_KEY = "javalanche.fixed.number.of.tasks";

	public static final String TASK_FILENAME_KEY = "javalanche.task.file.name";

	private static final File TASK_DIR = ConfigurationLocator
			.getJavalancheConfiguration().getOutputDir();

	public static void createMutationTasks() throws IOException {
		deleteTasks();
		int numberOfTasks = DEFAULT_NUMBER_OF_TASKS;
		int mutationsPerTask = DEFAULT_MUTATIONS_PER_TASK;
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
		System.out.println("MutationTaskCreator.createMutationTasks()");
		createMutationTasks(numberOfTasks, mutationsPerTask);
	}

	private static void deleteTasks() {
		File dir = TASK_DIR;

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
	 * @throws IOException
	 */
	public static void createMutationTasks(int numberOfTasks,
			int mutationsPerTask) throws IOException {
		String prefix = ConfigurationLocator.getJavalancheConfiguration()
				.getProjectPrefix();
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
				writeListToFile(idsForTask, i, numberOfTasks);
			} else {
				logger.info("No more mutations. Finishing after file "
						+ (i - 1));

				break;
			}
		}
		i = i - 1;
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

	private static File writeListToFile(List<Long> list, int taskId,
			int totalNumberOfTasks) throws IOException {
		File resultFile = getFileName(taskId, totalNumberOfTasks);
		List<String> lines = new ArrayList<String>();
		for (Long l : list) {
			lines.add(l + "");
		}
		FileUtils.writeLines(resultFile, lines);
		System.out.println("Task created: " + resultFile.getCanonicalPath());
		return resultFile;
	}

	public static File getFileName(int id, int totalNumberOfTasks) {
		String property = System.getProperty(TASK_FILENAME_KEY);
		if (property != null && property.length() > 0
				&& totalNumberOfTasks == 1) {
			return new File(property);
		}
		String filename = String.format(MUTATION_TASK_FILE_FORMAT, id);
		File resultFile = new File(ConfigurationLocator
				.getJavalancheConfiguration().getOutputDir(), filename);
		return resultFile;
	}

	public static void main(String[] args) throws IOException {
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		if (projectPrefix == null) {
			throw new RuntimeException("Project prefix not specified");
		}
		createMutationTasks();
	}

}
