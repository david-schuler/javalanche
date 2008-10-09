package de.st.cs.unisb.javalanche.run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.TestMessage;
import de.st.cs.unisb.javalanche.results.persistence.HibernateUtil;

public class PrintResults {

	private static Logger logger = Logger.getLogger(PrintResults.class);

	public static void main(String[] args) {
		// printFirstWithResults(100);
		// printFromFiles();
		logger.info("PrintResult started");
		printUnmutated();
	}

	@SuppressWarnings("unchecked")
	private static void printUnmutated() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE mutationResult IS NOT NULL AND mutationType=0");
		// query.setMaxResults(max);
		List<Mutation> results = query.list();
		Set<TestMessage> errors = new HashSet<TestMessage>();
		Set<TestMessage> failures = new HashSet<TestMessage>();
		Set<String> passingTests = new HashSet<String>();
		Set<String> failingTests = new HashSet<String>();
		Set<String> errorTests = new HashSet<String>();
		Map<String, Mutation> passingMap = new HashMap<String, Mutation>();
		Map<String, List<TestMessage>> failureMap = new HashMap<String, List<TestMessage>>();
		Map<String, List<TestMessage>> errorMap = new HashMap<String, List<TestMessage>>();
		for (Mutation mutation : results) {
			System.out.println(mutation.toShortString());
			System.out.println("Errors");
			for (TestMessage testMessage : mutation.getMutationResult()
					.getErrors()) {
				System.out.println(testMessage);
				errors.add(new TestMessage(testMessage));
				errorTests.add(testMessage.getTestCaseName());
				String testName = testMessage.getTestCaseName();
				List<TestMessage> testMessageList = null;
				if (errorMap.get(testName) == null) {
					testMessageList = new ArrayList<TestMessage>();
					errorMap.put(testName, testMessageList);
				} else {
					testMessageList = errorMap.get(testName);
				}
				testMessageList.add(testMessage);
			}
			System.out.println("Failures");
			for (TestMessage testMessage : mutation.getMutationResult()
					.getFailures()) {
				System.out.println(testMessage);
				failures.add(new TestMessage(testMessage));
				failingTests.add(testMessage.getTestCaseName());
				String testName = testMessage.getTestCaseName();
				List<TestMessage> testMessageList = null;
				if (failureMap.get(testName) == null) {
					testMessageList = new ArrayList<TestMessage>();
					failureMap.put(testName, testMessageList);
				} else {
					testMessageList = failureMap.get(testName);
				}
				testMessageList.add(testMessage);
			}

			System.out.println("Passing");
			for (TestMessage testMessage : mutation.getMutationResult()
					.getPassing()) {
				System.out.println(testMessage);
				passingTests.add(testMessage.getTestCaseName());
				passingMap.put(testMessage.getTestCaseName(), mutation);
			}
		}
		System.out.println("\n\nErrors");
		for (TestMessage error : errors) {
			System.out.println(error);
		}
		System.out.println("\n\nFailures");
		for (TestMessage failure : failures) {
			System.out.println(failure);
		}

		System.out.println("\n\nFailures Short");
		for (String failure : failingTests) {
			System.out.println(failure);
		}

		System.out.println("\n\nError Short");
		for (String error : errorTests) {
			System.out.println(error);

		}

		System.out.println("\n\nCheck Passing");
		for (String passing : passingTests) {
			if (errorTests.contains(passing)) {
				System.out.println(passing + " contained in error and passing");
				for (TestMessage tm : errorMap.get(passing)) {
					System.out.println("\t" + tm);
				}
			}
			if (failingTests.contains(passing)) {
				System.out.println(passing
						+ " contained in failures and passing");
				for (TestMessage tm : failureMap.get(passing)) {
					System.out.println("\t" + tm);
				}
			}
		}
		System.out.printf("Total passing %d failing %d error %d", passingTests
				.size(), failingTests.size(), errorTests.size());
		tx.commit();
		session.close();
	}

	// private static void printFirstWithResults(int max) {
	// Session session = HibernateUtil.getSessionFactory().openSession();
	// Transaction tx = session.beginTransaction();
	// Query query = session
	// .createQuery("FROM Mutation WHERE mutationResult IS NOT NULL");
	// query.setMaxResults(max);
	// List<?> results = query.list();
	// for (Object object : results) {
	// logger.info(object);
	// }
	// tx.commit();
	// session.close();
	// }
	//
	// private static void printFromFiles() {
	// File dir = new File(MutationProperties.RESULT_DIR);
	// File[] files = dir.listFiles(new FilenameFilter() {
	// public boolean accept(File dir, String name) {
	// if (name.startsWith("mutation-task")) {
	// return true;
	// }
	// return false;
	// }
	// });
	// for (File file : files) {
	// printMutationsForFile(file);
	// }
	// }

	// private static void printMutationsForFile(File file) {
	// System.out.println("\nResults for File: " + file.toString());
	// List<Long> idList = Io.getIDsFromFile(file);
	// List<Mutation> mutations = QueryManager.getMutationsFromDbByID(idList
	// .toArray(new Long[0]));
	// for (Mutation mutation : mutations) {
	// System.out.println(mutation);
	// }
	// }

}
