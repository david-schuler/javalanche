package org.softevo.mutation.run;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

public class PrintResults {

	private static Logger logger = Logger.getLogger(PrintResults.class);

	public static void main(String[] args) {
//		printFirstWithResults(100);
		printFromFiles();
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
