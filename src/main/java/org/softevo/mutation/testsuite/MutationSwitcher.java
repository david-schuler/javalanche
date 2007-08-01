package org.softevo.mutation.testsuite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.softevo.mutation.coverageResults.TestSuiteCoverageResult;
import org.softevo.mutation.mutationPossibilities.MutationPossibility;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class MutationSwitcher {

	private static Logger logger = Logger.getLogger(MutationSwitcher.class
			.getName());

	private Mutations mutations = Mutations.fromXML();

	private Iterator<MutationPossibility> iter = mutations.iterator();

	private MutationPossibility actualMutation;

	private TestSuiteCoverageResult testSuiteCoverageResult;

	public MutationSwitcher() {

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

	public MutationPossibility next() {
		actualMutation = iter.next();
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
