package org.softevo.mutation.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.results.persistence.HibernateUtil;

public class ThreadPool {

	private static final String EXEC_DIR = "/scratch/schuler/mutationTest/src/scripts/";

	private static Logger logger = Logger.getLogger(ThreadPool.class);

	private static final int NUMBER_OF_THREADS = 10;

	private static final int MAX_MUTATIONS = 10000;

	private static final int NUMBER_OF_TASKS = 10;

	private static final int MUTATIONS_PER_TASK = 100;

	private static final String TASK_NAME = "test-no-compile";

	private static final String MUTATION_COMMAND = "/scratch/schuler/mutationTest/src/scripts/threaded-run-tests.sh";

	ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	private List<Long> mutationIDs;

	public static void main(String[] args) {
		ThreadPool tp = new ThreadPool();
		tp.start();
	}

	private void start() {
		logger.info("Start fetching mutations");
		mutationIDs = getMutationsIdListFormDb();
		logger.info("Fetched " + mutationIDs.size() + " mutations");
		List<File> files = writeTaskFiles();
		int counter = 0;
		for (File f : files) {
			String outputFile = String.format("process-output-%02d.txt",
					counter);
			counter++;
			ProcessStarter ps = new ProcessStarter(MUTATION_COMMAND,
					new String[] { "-Dmutation.file=" + f.getAbsolutePath() },
					new File(EXEC_DIR), new File(outputFile));
			logger.info("Process: " + ps.toString());
			pool.submit(ps);
		}
		while (!pool.isTerminated()) {
			try {
				pool.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<File> writeTaskFiles() {
		List<File> resultFileList = new ArrayList<File>();
		for (int i = 0; i < NUMBER_OF_TASKS; i++) {
			List<Long> list = getMutionIDs(MUTATIONS_PER_TASK);
			File resultFile = writeListToFile(list, i);
			resultFileList.add(resultFile);
		}
		return resultFileList;
	}

	private File writeListToFile(List<Long> list, int id) {
		String filename = String.format("mutation-task-%02d.txt", id);
		File resultFile = new File(filename);
		StringBuilder sb = new StringBuilder();
		for (Long l : list) {
			sb.append(l);
			sb.append("\n");
		}
		Io.writeFile(sb.toString(), resultFile);
		return resultFile;
	}

	// private List<Long> getFakeList() {
	// List<Long> list = new ArrayList<Long>();
	// for (long i = 0; i < 40000; i++) {
	// list.add(i);
	// }
	// return list;
	// }

	private List<Long> getMutionIDs(int numberOfIds) {
		List<Long> list = new ArrayList<Long>();
		Random r = new Random();
		for (int i = 0; i < numberOfIds; i++) {
			int position = r.nextInt(mutationIDs.size());
			list.add(mutationIDs.remove(position));
		}
		return list;
	}

	private static List<Long> getMutationsIdListFormDb() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createSQLQuery("SELECT m.id FROM Mutation m JOIN TestCoverageClassResult tccr ON m.classname = tccr.classname JOIN TestCoverageClassResult_TestCoverageLineResult AS class_line ON class_line.testcoverageclassresult_id = tccr.id JOIN TestCoverageLineResult AS tclr ON tclr.id = class_line.lineresults_id 	WHERE m.mutationresult_id IS NULL AND m.linenumber = tclr.linenumber");
		query.setMaxResults(MAX_MUTATIONS);
		List results = query.list();
		List<Long> idList = new ArrayList<Long>();
		for (Object id : results) {
			idList.add(Long.valueOf(id.toString()));
		}
		tx.commit();
		session.close();
		return idList;
	}

}
