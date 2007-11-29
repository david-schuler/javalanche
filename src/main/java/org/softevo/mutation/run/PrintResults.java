package org.softevo.mutation.run;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.TestMessage;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class PrintResults {

	private static Logger logger = Logger.getLogger(PrintResults.class);

	public static void main(String[] args) {
		// printFirstWithResults(100);
		// printFromFil es();
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
		for (Mutation mutation : results) {
			System.out.println(mutation.toShortString());
			System.out.println("Errors");
			for (TestMessage testMessage : mutation.getMutationResult()
					.getErrors()) {
				 System.out.println(testMessage);
				errors.add(new TestMessage(testMessage));
				errorTests.add(testMessage.getTestCaseName());

			}
			System.out.println("Failures");
			for (TestMessage testMessage : mutation.getMutationResult()
					.getFailures()) {
				 System.out.println(testMessage);
				failures.add(new TestMessage(testMessage));
				failingTests.add(testMessage.getTestCaseName());
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
				// System.out.println(passingMap.get(passing));
			}
			if (failingTests.contains(passing)) {
				System.out.println(passing
						+ " contained in failures and passing");
				// System.out.println(passingMap.get(passing));
			}
		}
		System.out.printf("Total passing %d failing %d error %d", passingTests
				.size(), failingTests.size(), errorTests.size());
		tx.commit();
		session.close();
	}

	private static void printFirstWithResults(int max) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("FROM Mutation WHERE mutationResult IS NOT NULL");
		query.setMaxResults(max);
		List results = query.list();
		for (Object object : results) {
			logger.info(object);
		}
		tx.commit();
		session.close();
	}

	private static void printFromFiles() {
		File dir = new File(MutationProperties.RESULT_DIR);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith("mutation-task")) {
					return true;
				}
				return false;
			}
		});
		for (File file : files) {
			printMutationsForFile(file);
		}
	}

	private static void printMutationsForFile(File file) {
		System.out.println("\nResults for File: " + file.toString());
		List<Long> idList = Io.getIDsFromFile(file);
		List<Mutation> mutations = QueryManager.getMutationsFromDbByID(idList
				.toArray(new Long[0]));
		for (Mutation mutation : mutations) {
			System.out.println(mutation);
		}
	}

}
