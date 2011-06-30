package de.unisb.cs.st.javalanche.mutation.run.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.PropertyUtil;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;

public class RandomTaskCreator {

	private static final String SEED_KEY = "javalanche.seed";

	private static void createRandomTask() throws IOException {
		String seedString = PropertyUtil.getProperty(SEED_KEY);
		int seed = Integer.parseInt(seedString);
		String numString = PropertyUtil
				.getProperty(MutationTaskCreator.MUTATION_PER_TASK_KEY);
		int numberOfMutations = Integer.parseInt(numString);
		String fileName = PropertyUtil
				.getProperty(MutationTaskCreator.TASK_FILENAME_KEY);
		createTaskRandomSelection(numberOfMutations, seed, fileName);
	}

	public static void createTaskRandomSelection(int numberOfMutations,
			int seed, String fileName) throws IOException {
		Random r = new Random(seed);
		Set<Long> coveredSet = MutationCoverageFile.getCoveredMutations();
		List<Long> covered = new ArrayList<Long>(coveredSet);
		Set<Long> selected = new HashSet<Long>();
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < numberOfMutations && covered.size() > 0; i++) {
			int index = r.nextInt(covered.size());
			Long id = covered.get(index);
			covered.remove(index);
			selected.add(id);
			lines.add(id + "");
		}
		// List<Mutation> mutations = QueryManager.getMutationsByIds(selected
		// .toArray(new Long[0]));
		// Collections.sort(mutations);
		File file = new File(fileName);
		FileUtils.writeLines(file, lines);

	}

	public static void main(String[] args) throws IOException {
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		if (projectPrefix == null) {
			throw new RuntimeException("Project prefix not specified");
		}
		createRandomTask();
	}

}
