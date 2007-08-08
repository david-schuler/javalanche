package org.softevo.mutation.testsuite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.coverageResults.TestSuiteCoverageResult;
import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationSwitcher {

	private static Logger logger = Logger.getLogger(MutationSwitcher.class
			.getName());

	private List<Mutation> mutations;

	private Iterator<Mutation> iter;

	private Mutation actualMutation;

	private TestSuiteCoverageResult testSuiteCoverageResult;

	public MutationSwitcher() {
		initMutations();
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
		if (testSuiteCoverageResult == null) {
			try {
				testSuiteCoverageResult = TestSuiteCoverageResult.getFromXml();
			} catch (OutOfMemoryError e) {
				System.err
						.println("Not enough memory for reading coverage results");
				throw e;
			}
		}
		Set<String> testNames = new HashSet<String>();
		List<String> tests = testSuiteCoverageResult.getTestsForLine(
				actualMutation.getClassName(), actualMutation.getLineNumber());
		for (String testName : tests) {
			testNames.add(testName);
		}
		return testNames;
	}

	public boolean hasNext() {
		return iter.hasNext();
	}

	public Mutation next() {
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
			logger.info("enabling mutation: " + actualMutation.getMutionId()
					+ " in line " + actualMutation.getLineNumber());
			System.setProperty(actualMutation.getMutationVariable(), "1");
		}
	}

	public void switchOff() {
		if (actualMutation != null) {
			System.clearProperty(actualMutation.getMutationVariable());
			logger.info("disabling mutation: " + actualMutation.getMutionId());
		}

	}
}
