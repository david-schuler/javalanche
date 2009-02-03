package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.PrimitiveArrays;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.invariants.runtime.InvariantObserver;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

/**
 * Class collects the invariants that where violated by every test, and adds
 * them to the mutation's result if they where learned from this test.
 *
 * @author David Schuler
 *
 */
public class InvariantPerTestListener implements MutationTestListener {

	private Map<String, Set<Integer>> idsPerTest = new HashMap<String, Set<Integer>>();

	private Map<Long, Integer> results = new HashMap<Long, Integer>();

	private Set<Integer> totalViolations = new HashSet<Integer>();

	private static final File DIR = new File("invariant-files");

	public void end() {
		String filename = MutationProperties.MUTATION_FILE_NAME;
		int start = filename.lastIndexOf("-");
		int end = filename.lastIndexOf(".txt");
		int i = Integer.parseInt(filename.substring(start + 1, end));
		XmlIo.toXML(results, new File(DIR, "invariant_violations_per_test-" + i
				+ ".xml"));
	}

	private int init() {

		File[] listFiles = DIR.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.endsWith("mapping.txt")) {
					return true;
				}
				return false;
			}

		});
		assert (listFiles.length == 1);
		List<String> linesFromFile = Io.getLinesFromFile(listFiles[0]);
		for (String line : linesFromFile) {
			int number = getNumber(line);
			String testName = getTestName(line);
			Set<Integer> ids = SerializeIo.get(new File(DIR, "invariant-ids-"
					+ number + ".txt"));
			idsPerTest.put(testName, ids);
		}
		return 0;
	}

	private String getTestName(String line) {
		int index = line.indexOf(',');
		int index2 = line.indexOf('(');
		String test = line.substring(index + 1, index2);
		String className = line.substring(index2 + 1, line.length() - 1);
		return className + "." + test;
	}

	private int getNumber(String line) {
		int end = line.indexOf(',');
		String number = line.substring(end - 4, end);
		return Integer.parseInt(number);
	}

	public void mutationEnd(Mutation mutation) {
		results.put(mutation.getId(), totalViolations.size());
		MutationTestResult mutationResult = mutation.getMutationResult();
		mutationResult.setDifferentViolatedInvariants(totalViolations.size());
		mutationResult.setTotalViolations(totalViolations.size());
		mutationResult.setViolatedInvariants(PrimitiveArrays
				.toIntArray(totalViolations));
	}

	public void mutationStart(Mutation mutation) {
		totalViolations.clear();
		InvariantObserver.reset();
	}

	public void start() {
		init();
	}

	public void testEnd(String testName) {
		InvariantObserver instance = InvariantObserver.getInstance();
		if (instance != null) {
			int totalViolatedInvariants = instance
					.getTotalInvariantViolations();

			if (totalViolatedInvariants > 0) {
				int[] violatedInvariantsArray = instance
						.getViolatedInvariantsArray();
				Set<Integer> idsForThisTest = idsPerTest.get(testName);
				Set<Integer> violations = new HashSet<Integer>();
				for (int i : violatedInvariantsArray) {
					if (idsForThisTest.contains(i)) {
						violations.add(i);
					}
				}
				if (violations.size() > 0) {
					totalViolations.addAll(violations);
				}
			}
			InvariantObserver.reset();
		}

	}

	public void testStart(String testName) {
		InvariantObserver.reset();

	}

}
