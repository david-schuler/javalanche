package org.softevo.mutation.run;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.TestMessage;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.run.analyze.MutatedUnmutatedAnalyzer;

public class CompareWithUnmutated {

	private static Logger logger = Logger.getLogger(CompareWithUnmutated.class);

	private static class CompData {

		private String className;

		private int killed;

		private int survived;

		public CompData(String className) {
			this.className = className;
		}

		public void addKilled() {
			killed++;
		}

		public void addSurvived() {
			survived++;
		}

		/**
		 * @return the killed
		 */
		public int getKilled() {
			return killed;
		}

		/**
		 * @return the survived
		 */
		public int getSurvived() {
			return survived;
		}

		/**
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

	}

	private static class TestsAnalyzer implements MutatedUnmutatedAnalyzer {

		private List<Mutation> failures = new ArrayList<Mutation>();

		private int passing = 0;

		private int unMutatedHasMoreFailures;

		public void handleMutation(Mutation mutated, Mutation unMutated) {
			SingleTestResult mutatedResult = mutated.getMutationResult();
			SingleTestResult unMutatedResult = unMutated.getMutationResult();

			if (mutatedResult != null && unMutatedResult != null) {
				if (mutatedResult.getRuns() != unMutatedResult.getRuns()) {
					failures.add(mutated);
					failures.add(unMutated);
				} else {
					passing++;
				}
			}

			int unMutatedFailures = unMutatedResult.getNumberOfFailures()
					+ unMutatedResult.getNumberOfErrors();
			int mutatedFailures = mutatedResult.getNumberOfFailures()
					+ mutatedResult.getNumberOfErrors();
			if (unMutatedFailures > mutatedFailures) {
				unMutatedHasMoreFailures++;
			}

		}

		public String getResults() {
			StringBuilder sb = new StringBuilder();
			sb.append("Pairs with unequal number of runs: "
					+ (int) (failures.size() / 2.));
			sb.append('\n');
			sb.append("Pairs with equal number of runs:" + passing);
			sb.append('\n');
			sb.append("Mutations with less errors than unmutated: "
					+ unMutatedHasMoreFailures);
			return sb.toString();
		}
	}

	private static class KilledAnalyzer implements MutatedUnmutatedAnalyzer {

		private List<Mutation> failures = new ArrayList<Mutation>();

		private int passing = 0;

		private int killed;

		private int notKilled;

		public void handleMutation(Mutation mutated, Mutation unMutated) {
			SingleTestResult mutatedResult = mutated.getMutationResult();
			SingleTestResult unMutatedResult = unMutated.getMutationResult();

			int unMutatedFailures = unMutatedResult.getNumberOfFailures();
			int unMutatedErrors = unMutatedResult.getNumberOfErrors();
			int unMutatedRuns = unMutatedResult.getRuns();

			int mutatedFailures = mutatedResult.getNumberOfFailures();
			int mutatedErrors = mutatedResult.getNumberOfErrors();
			int mutatedRuns = mutatedResult.getRuns();

			if (unMutatedRuns == mutatedRuns
					&& mutatedErrors + mutatedFailures > unMutatedErrors
							+ unMutatedFailures) {
				killed++;
			} else {
				notKilled++;
			}

		}

		public String getResults() {
			return String.format("Mutations killed %d\n"
					+ "Mutations not killed: %d\n" + "Total: %d", killed,
					notKilled, killed + notKilled);
		}

	}

	private static class UnMutatedTestAnlayzer implements
			MutatedUnmutatedAnalyzer {

		private static class TestCaseOutcome {

			String name;

			int passed;

			int failed;

			List<String> failureMessages = new ArrayList<String>();

			public TestCaseOutcome(String name) {
				super();
				this.name = name;
			}

			public void addPassed() {
				passed++;
			}

			public void addFailed(String string) {
				failureMessages.add(string);
				failed++;
			}
		}

		Map<String, TestCaseOutcome> testCaseMap = new HashMap<String, TestCaseOutcome>();

		public String getResults() {
			int inconsistent = 0;
			int total = 0;
			for (TestCaseOutcome outcome : testCaseMap.values()) {
				if (outcome.failed != 0 && outcome.passed != 0) {
					inconsistent++;
				}
				total++;
			}
			writeResultFile();
			return "Tests with inconsistent outcome: " + inconsistent
					+ " out of " + total;
		}

		public void writeResultFile() {
			StringBuffer sb = new StringBuffer();
			for (TestCaseOutcome outcome : testCaseMap.values()) {
				if (outcome.failed != 0 && outcome.passed != 0) {
					sb.append(outcome.name);
					sb.append('\n');
					sb.append("Messages:\n");
					for (String message : outcome.failureMessages) {
						sb.append('\t' + message);
						sb.append('\n');
					}
					sb
							.append("--------------------------------------------------\n");
				}
			}
			Io.writeFile(sb.toString(), new File("unMutatedAnalyze.txt"));
		}

		public void handleMutation(Mutation mutated, Mutation unMutated) {
			SingleTestResult testResult = unMutated.getMutationResult();
			for (TestMessage tm : testResult.getPassing()) {
				TestCaseOutcome outcome = getOutcome(tm);
				outcome.addPassed();
			}
			for (TestMessage tm : testResult.getFailures()) {
				TestCaseOutcome outcome = getOutcome(tm);
				outcome.addFailed(tm.getMessage());
			}
			for (TestMessage tm : testResult.getErrors()) {
				TestCaseOutcome outcome = getOutcome(tm);
				outcome.addFailed(tm.getMessage());
			}

		}

		private TestCaseOutcome getOutcome(TestMessage tm) {
			TestCaseOutcome outcome = null;
			String testCaseName = tm.getTestCaseName();
			if (testCaseMap.containsKey(testCaseName)) {
				outcome = testCaseMap.get(testCaseName);
			} else {
				outcome = new TestCaseOutcome(testCaseName);
				testCaseMap.put(testCaseName, outcome);
			}
			return outcome;
		}

	}

	public static void main(String[] args) {
		checkResults();
	}

	private static void checkResults() {
		Map<String, CompData> compDataMap = new HashMap<String, CompData>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List l = doQuery(session);
		MutatedUnmutatedAnalyzer[] analyzers = new MutatedUnmutatedAnalyzer[] {
				new TestsAnalyzer(), new KilledAnalyzer(),
				new UnMutatedTestAnlayzer() };
		int discovered = 0;
		int unDiscovered = 0;
		for (Object o : l) {
			Object[] array = (Object[]) o;
			Mutation mutated = (Mutation) array[0];
			Mutation unMutated = (Mutation) array[1];
			for (MutatedUnmutatedAnalyzer analyzer : analyzers) {
				analyzer.handleMutation(mutated, unMutated);
			}
			SingleTestResult mutatedResult = mutated.getMutationResult();
			SingleTestResult unMutatedResult = unMutated.getMutationResult();
			if (mutatedResult != null && unMutatedResult != null) {
				int mutatedFailing = mutatedResult.getNumberOfFailures()
						+ mutatedResult.getNumberOfErrors();
				int unMutatedFailing = unMutatedResult.getNumberOfFailures()
						+ unMutatedResult.getNumberOfErrors();
				String className = mutated.getClassName();
				CompData compData = null;
				if (compDataMap.get(className) == null) {
					compData = new CompData(className);
				} else {
					compData = compDataMap.get(className);
				}
				if (mutatedFailing > unMutatedFailing) {
					compData.addKilled();
					discovered++;
				} else if (unMutatedFailing == mutatedFailing) {
					compData.addSurvived();
					unDiscovered++;
				} else {
					compData.addSurvived();
					unDiscovered++;
					String message = "More tests failed for unmutated"
							+ +unMutatedFailing + " vs. " + mutatedFailing;
					logger.warn(message);
					System.out.println("Mutated " + mutated.getId());
					System.out.println("Unmutated " + unMutated.getId());
					// throw new RuntimeException(message);
				}

			}
		}

		tx.commit();
		session.close();

		System.out.printf(
				"Discovered Mutations: %d  Undiscovered Mutations: %d\n",
				discovered, unDiscovered);
		for (MutatedUnmutatedAnalyzer analyzer : analyzers) {
			System.out.println(analyzer.getResults());
		}
	}

	private static List doQuery(Session session) {
		String queryString = "SELECT {m1.*}, {m2.*} FROM Mutation m1 INNER JOIN Mutation m2 ON m1.lineNumber = m2.lineNumber AND m1.className = m2.className WHERE m1.mutationType != 0 AND m2.mutationType = 0 AND m1.mutationResult_id IS NOT NULL AND m2.mutationResult_id IS NOT NULL";
		SQLQuery query = session.createSQLQuery(queryString);
		query.addEntity("m1", Mutation.class);
		query.addEntity("m2", Mutation.class);
		List l = query.list();
		System.out.println("Size " + l.size());
		return l;
	}

	private static void printCompare(SingleTestResult mutatedResult,
			SingleTestResult unMutatedResult) {
		String compareString = String
				.format(
						"Mutated(%d): %d %d %d Unmutated(%d): %d %d %d (runs, errors, failures)",
						mutatedResult.getId(), mutatedResult.getRuns(),
						mutatedResult.getNumberOfErrors(), mutatedResult
								.getNumberOfFailures(),
						unMutatedResult.getId(), unMutatedResult.getRuns(),
						unMutatedResult.getNumberOfErrors(), unMutatedResult
								.getNumberOfFailures());
		System.out.println(compareString);
	}

	private static void unrollArray(Object o, int depth) {
		if (o instanceof Object[]) {
			Object[] array = (Object[]) o;
			System.out.println("ARRAY:");
			for (Object element : array) {
				unrollArray(element, depth + 1);
			}
		} else {
			System.out.println(depth + ": " + o);
		}
	}
}
