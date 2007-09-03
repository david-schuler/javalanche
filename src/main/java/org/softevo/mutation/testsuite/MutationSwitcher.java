package org.softevo.mutation.testsuite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationSwitcher {

	private static Logger logger = Logger.getLogger(MutationSwitcher.class);

	private List<Mutation> mutations;

	private Iterator<Mutation> iter;

	private Mutation actualMutation;

//	private TestSuiteCoverageResult testSuiteCoverageResult;

	public MutationSwitcher() {
//		initMutations();
	}

	private void initMutations() {
		if (mutations == null) {
			mutations = QueryManager.getAllMutations();
			iter = mutations.iterator();
		} else {
			throw new RuntimeException("Already initialized");
		}
	}

	public Set<String> getTests() {
//		if (testSuiteCoverageResult == null) {
//			try {
//				testSuiteCoverageResult = TestSuiteCoverageResult.getFromXml();
//			} catch (OutOfMemoryError e) {
//				System.err.println("Not enough memory for reading coverage results");
//				throw e;
//			}
//		}
//		Set<String> testNames = new HashSet<String>();
//		List<String> tests = testSuiteCoverageResult.getTestsForLine(
//				actualMutation.getClassName(), actualMutation.getLineNumber());
//		for (String testName : tests) {
//			testNames.add(testName);
//		}
		String[] testCases = QueryManager.getTestCases(actualMutation);
		if(testCases == null){
			return null;
		}
		return new HashSet<String>(Arrays.asList(testCases));
	}

	public boolean hasNext() {
		if(iter == null){
			initMutations();
		}
		return iter.hasNext();
	}

	public Mutation next() {
		if(iter == null){
			initMutations();
		}
		while (iter.hasNext()) {
			actualMutation = iter.next();

			if(actualMutation.getMutationResult() == null){
				return actualMutation;
			}
			else{
				logger.info("Mutation already got Results");
			}
		}
		return actualMutation;
	}

	public void switchOn() {
		if (actualMutation != null) {
			logger.info("enabling mutation: " + actualMutation.getMutationVariable()
					+ " in line " + actualMutation.getLineNumber()  + " - " +  actualMutation.toString());
			System.setProperty(actualMutation.getMutationVariable(), "1");
		}
	}

	public void switchOff() {
		if (actualMutation != null) {
			System.clearProperty(actualMutation.getMutationVariable());
			logger.info("disabling mutation: " + actualMutation.getMutationVariable());
		}

	}
}
