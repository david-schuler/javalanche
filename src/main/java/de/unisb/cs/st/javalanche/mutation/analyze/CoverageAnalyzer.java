package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * CoverageAnalyzer prints out all mutations that are not covered by tests.
 *
 * @author David Schuler
 *
 */
public class CoverageAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(CoverageAnalyzer.class);
	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	private Random r = new Random(1816052929);

	public String analyze(Iterable<Mutation> mutations) {
		StringBuilder sb = new StringBuilder();
		List<Mutation> coveredNotKilledMutations = new ArrayList<Mutation>();
		List<Mutation> coveredMutations = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			Set<String> testsCollectedData = QueryManager
					.getTestsCollectedData(m);
			if (testsCollectedData == null || testsCollectedData.size() == 0) {
				// sb.append("\nMutation not covered: " + m);
			} else {
				coveredMutations.add(m);
				if (!m.isKilled()) {
					coveredNotKilledMutations.add(m);
				}
			}
		}
		logger.info("Covered mutations: " + coveredMutations.size());
		logger.info("Covered not killed: " + coveredNotKilledMutations.size());
		List<String> usedClasses = new ArrayList<String>();
		for (int i = 0; i < 20; ) {
			Mutation randomMutation = coveredNotKilledMutations.remove(r
					.nextInt(coveredNotKilledMutations.size()));
			if (!usedClasses.contains(randomMutation.getClassName())) {
				usedClasses.add(randomMutation.getClassName());
				i++;
				sb.append(i + 1 + " " + randomMutation.toShortString());
//				sb.append(i + 1 + " " + randomMutation.toString());

				sb.append("\t Tests: "
						+ QueryManager.getTestsCollectedData(randomMutation)
						+ " \n");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new Random().nextInt());
	}
}
