package de.unisb.cs.st.javalanche.mutation.analyze.invariant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.unisb.cs.st.javalanche.mutation.analyze.AnalyzeUtil;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.InvariantAddResult;
import de.unisb.cs.st.javalanche.mutation.results.InvariantSet;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.InvariantFilesUtil;

public class InvariantSplitAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations) {
		// List<TestName> tests = getAllTests();
		int numberOfMutations = 0;
		List<Mutation> violatingMutations = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			MutationTestResult mutationResult = m.getMutationResult();

			if (mutationResult != null) {
				if(mutationResult.getDifferentViolatedInvariants()>0){
				numberOfMutations++;
				}
				if (mutationResult.getAddResult(InvariantAddResult.class) != null) {

					violatingMutations.add(m);
				}
			}
		}
		List<String> testSplit = getTestSplit(50, getAllTests());
		String computeSplit = computeSplit(violatingMutations, testSplit);
		return "Split size " + testSplit.size() + "results " +  numberOfMutations + " violating: "
				+ violatingMutations.size() + "\n" + computeSplit;
	}

	private String computeSplit(List<Mutation> violatingMutations,
			Collection<String> testSplit) {
		Collection<Mutation> invariantViolatingMutations = computeInvariantViolatingMutations(
				violatingMutations, testSplit);
		// Collection<String> stringTestSplit = getStringSet(testSplit);
		Collection<Mutation> undetectedViolatingMutations = getUndetected(
				invariantViolatingMutations, testSplit);
		int detectedByWholeTestSuite = getNumberOfDetected(undetectedViolatingMutations);
		String result = String
				.format(
						"Split produced %d (out of %d ivm) undetected invariant violating mutations. Out of these  %d %s where detected by the test suite",
						undetectedViolatingMutations.size(),
						invariantViolatingMutations.size(),
						detectedByWholeTestSuite, AnalyzeUtil.formatPercent(
								detectedByWholeTestSuite,
								undetectedViolatingMutations.size()));
		return result;
	}

	private Collection<String> getStringSet(Collection<TestName> testSplit) {
		SortedSet<String> result = new TreeSet<String>();
		for (TestName tm : testSplit) {
			result.add(tm.getName());
		}
		return result;
	}

	private int getNumberOfDetected(
			Collection<Mutation> undetectedViolatingMutations) {
		int detectedCount = 0;
		for (Mutation mutation : undetectedViolatingMutations) {
			detectedCount += mutation.isKilled() ? 1 : 0;
		}
		return detectedCount;
	}

	private Collection<Mutation> getUndetected(Collection<Mutation> mutations,
			Collection<String> tests) {
		Collection<Mutation> detected = getDetected(mutations, tests);
		List<Mutation> results = new ArrayList<Mutation>(mutations);
		results.removeAll(detected);
		return results;
	}

	private Collection<Mutation> getDetected(Collection<Mutation> mutations,
			Collection<String> tests) {
		List<Mutation> results = new ArrayList<Mutation>();
		for (Mutation m : mutations) {
			MutationTestResult mutationResult = m.getMutationResult();
			Collection<TestMessage> errors = mutationResult.getErrors();
			Collection<TestMessage> failures = mutationResult.getFailures();
			if (hasError(errors, tests) || hasError(failures, tests)) {
				results.add(m);
			}
		}
		return results;
	}

	private boolean hasError(Collection<TestMessage> failures,
			Collection<String> tests) {
		for (TestMessage tm : failures) {
			if (tests.contains(tm.getTestCaseName())) {
				return true;
			}
		}
		return false;
	}

	private Collection<Mutation> computeInvariantViolatingMutations(
			List<Mutation> violatingMutations, Collection<String> testSplit) {
		List<Mutation> resultMutations = new ArrayList<Mutation>();
		Set<Integer> invariantsForTests = getInvariantsForTests(testSplit);
		for (Mutation m : violatingMutations) {
			InvariantAddResult invariantAddResult = m.getMutationResult()
					.getAddResult(InvariantAddResult.class);
			Map<String, InvariantSet> violationsPerTest = invariantAddResult
					.getViolationsPerTest();
			Set<Integer> allViolations = new HashSet<Integer>();
			for (String name : testSplit) {
				InvariantSet invariantSet = violationsPerTest.get(name);
				allViolations.addAll(invariantSet.getInvariants());
			}
			allViolations.retainAll(invariantsForTests);
			if (allViolations.size() > 0) {
				resultMutations.add(m);
			}
		}
		return resultMutations;
	}

	private Set<Integer> getInvariantsForTests(Collection<String> testSplit) {
		Map<String, Set<Integer>> invariantMap = InvariantFilesUtil
				.readInvariantPerTestFiles();
		Set<Integer> invariantsForTests = new HashSet<Integer>();
		for (String testName : testSplit) {
			Set<Integer> set = invariantMap.get(testName);
			if (set == null) {
				throw new RuntimeException("Key not conained in map. \nKey: "
						+ testName + "\n Map keys: " + invariantMap.keySet());
			}
			invariantsForTests.addAll(set);
		}
		return invariantsForTests;
	}

	private List<String> getTestSplit(int percent, List<String> tests) {
		if (percent < 0 || percent > 100) {
			throw new IllegalArgumentException(
					"Percent not in range 0-100, was " + percent);
		}
		int size = (int) (tests.size() * (percent / 100.));
		return tests.subList(0, size);
	}

	private List<String> getAllTests() {
		List<String> result = new ArrayList<String>();
		List<TestName> testsForProject = QueryManager.getTestsForProject();
		for (TestName testName : testsForProject) {
			result.add(testName.getName());
		}
		result.remove("NO INFO");
		return result;
	}

}
