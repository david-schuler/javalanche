package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.listeners;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.collect.PrimitiveArrays;
import com.sun.org.apache.bcel.internal.generic.ALOAD;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;
import de.unisb.cs.st.javalanche.invariants.runtime.InvariantObserver;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.InvariantAddResult;
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

	private static Logger logger = Logger
			.getLogger(InvariantPerTestListener.class);

	private final Map<String, Set<Integer>> idsPerTest = new HashMap<String, Set<Integer>>();

	private final Map<Long, Integer> results = new HashMap<Long, Integer>();
	private final Map<Set<Integer>, File> files = new IdentityHashMap<Set<Integer>, File>();
	private final Map<String, Integer> testNumbers = new HashMap<String, Integer>();

	private Map<String, Collection<Integer>> violationsPerTest = new HashMap<String, Collection<Integer>>();

	private final Set<Integer> totalViolations = new HashSet<Integer>();

	private static final File DIR = new File("invariant-files");

	public void end() {
		String filename = MutationProperties.MUTATION_FILE_NAME;
		int start = filename.lastIndexOf("-");
		int end = filename.lastIndexOf(".txt");
		if (start > -1 && end > 0) {
			int i = Integer.parseInt(filename.substring(start + 1, end));
			XmlIo.toXML(results, new File(DIR, "invariant_violations_per_test-"
					+ i + ".xml"));
		}

		if (MutationProperties.RUN_MODE == MutationProperties.RunMode.CHECK_INVARIANTS_PER_TEST) {
			List<Integer> removeIds = new ArrayList<Integer>();
			Set<Entry<String, Collection<Integer>>> entrySet = violationsPerTest
					.entrySet();
			for (Entry<String, Collection<Integer>> entry : entrySet) {
				Collection<Integer> violations = entry.getValue();
				Set<Integer> ids = idsPerTest.get(entry.getKey());
				System.out.println("InvariantPerTestListener.end() VIOLATIONS: " + violations.size() + " LEARNED: " + ids.size() );
				Set<Integer> violationSet = new HashSet<Integer>(violations);
				violationSet.retainAll(ids);
				System.out.println(" RETAINSIZE" + violationSet.size());
				for (Integer violatedId : violations) {
					if (ids.contains(violatedId)) {
						System.out.println("Check for id " + violatedId);
						if(!isAlsoLearnedFromOtherTests(violatedId, entry.getKey())){
							removeIds.add(violatedId);
						}
					}
				}
			}
		System.out.println("InvariantPerTestListener.end() Invariants that can be disabled: " + removeIds.size() + " \n " + removeIds );
		}

	}

	private boolean isAlsoLearnedFromOtherTests(Integer violatedId, String testName) {
		Set<Entry<String, Set<Integer>>> idsPerTestEntries = idsPerTest.entrySet();
		for (Entry<String, Set<Integer>> entry : idsPerTestEntries) {
			if(entry.getKey().equals(testName)){
				if(entry.getValue().contains(violatedId)){
					return true;
				}
			}
		}
		return false;
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
			File file = new File(DIR, "invariant-ids-" + number + ".ser");
			Set<Integer> ids = SerializeIo.get(file);
			idsPerTest.put(testName, ids);
			testNumbers.put(testName, number);
			files.put(ids, file);
		}
		return 0;
	}

	private static String getTestName(String line) {
		int index = line.indexOf(',');
		int index2 = line.indexOf('(');
		String test = line.substring(index + 1, index2);
		String className = line.substring(index2 + 1, line.length() - 1);
		return className + "." + test;
	}

	private static int getNumber(String line) {
		int end = line.indexOf(',');
		String number = line.substring(end - 4, end);
		return Integer.parseInt(number);
	}

	public void mutationEnd(Mutation mutation) {
		results.put(mutation.getId(), totalViolations.size());
		MutationTestResult mutationResult = mutation.getMutationResult();
		InvariantAddResult invariantAddResult = new InvariantAddResult(
				violationsPerTest);
		mutationResult.addResults(invariantAddResult);
		mutationResult.setDifferentViolatedInvariants(totalViolations.size());
		mutationResult.setTotalViolations(totalViolations.size());
		// mutationResult.setViolatedInvariants(PrimitiveArrays
		// .toIntArray(totalViolations));

	}

	public void mutationStart(Mutation mutation) {
		totalViolations.clear();
		violationsPerTest.clear();
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
				List<Integer> invariantList = PrimitiveArrays
						.asList(violatedInvariantsArray);
				totalViolations.addAll(invariantList);
				violationsPerTest.put(testName, invariantList);
			}
			if (MutationProperties.RUN_MODE == MutationProperties.RunMode.CHECK_INVARIANTS_PER_TEST) {
				handleViolations(testName);
			}
		}
		InvariantObserver.reset();
	}

	private void handleViolations(String testName) {
		InvariantObserver instance = InvariantObserver.getInstance();
		Set<Integer> idsForThisTest = idsPerTest.get(testName);
		Set<Integer> violations = new HashSet<Integer>();
		int[] violatedInvariantsArray = instance.getViolatedInvariantsArray();

		for (int i : violatedInvariantsArray) {
			if (idsForThisTest.contains(i)) {
				violations.add(i);
			}
		}
		if (violations.size() > 0) {
			totalViolations.addAll(violations);
		}

		logger.info(violations.size() + " violations for " + testName
				+ " out of " + violatedInvariantsArray.length
				+ " violations and invariants for this test: "
				+ idsForThisTest.size());

		File file = files.get(idsForThisTest);
		File checkedFile = new File(file.getParentFile(),
				InvariantProperties.CHECKED_PREFIX + file.getName());
		idsForThisTest.removeAll(violations);
		SerializeIo.serializeToFile(idsForThisTest, checkedFile);

		assert testNumbers.get(testName) != null;
		File violationsFile = new File(DIR, "invariant-violations-test-"
				+ testNumbers.get(testName) + ".ser");
		List<Integer> allViolations = PrimitiveArrays
				.asList(violatedInvariantsArray);
		logger.info("All violations size: " + allViolations.size());
		SerializeIo.serializeToFile(allViolations, violationsFile);
		File violaionsTextFile = new File(DIR, "invariant-violations-test-"
				+ testNumbers.get(testName) + ".txt");
		Io.writeFile(allViolations.toString(), violaionsTextFile);
	}

	public void testStart(String testName) {
		InvariantObserver.reset();
	}

}
