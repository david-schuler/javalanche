package de.unisb.cs.st.javalanche.mutation.hibernate.stress;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

@SuppressWarnings("unchecked")
public class StressTest {
	private static Logger logger = Logger.getLogger(StressTest.class);

	private static final int NUMBER_OF_THREADS = 6;

	private static final long CHECK_PERIOD = 3;

	private static final int NUMBER_OF_TASKS = 18;

	private static final int MUTATIONS_PER_THREAD = 10;

	private static class QueryInsertThread implements Runnable {

		private int limit;

		private int id;

		private static Logger logger = Logger
				.getLogger(QueryInsertThread.class);

		public void run() {
			logger.info("Thread started (id: " + id + ")");
			for (int i = 0; i < limit; i++) {
				Mutation m = new Mutation("test.class. " + id + "-" + i, i, i,
						MutationType.ARITHMETIC_REPLACE,false);
				QueryManager.saveMutation(m);
				logger.info("Mutation saved: " + m);
			}
			logger.info("Thread ended (id: " + id + ")");
		}

		public QueryInsertThread(int id, int limit) {
			super();
			this.limit = limit;
			this.id = id;
		}
	}

	@Ignore("This test takes to long.")
	@Test
	public void testQuery() {
		deleteAllTestMutations();
		Assert.assertEquals(0, getNumberOfTestMutations());
		final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(NUMBER_OF_THREADS);

		for (int i = 0; i < NUMBER_OF_TASKS; i++) {
			Runnable r = new QueryInsertThread(i, MUTATIONS_PER_THREAD);
			pool.execute(r);
		}
		while (!pool.isShutdown()) {
			try {
				boolean processesFinished = pool.awaitTermination(CHECK_PERIOD,
						TimeUnit.SECONDS);
				logger.info("Threads still running. Threads: "
						+ pool.getActiveCount());
				logger.info("Pool Size:" + pool.getPoolSize());
				logger.info("Task Count: " + pool.getTaskCount());
				logger.info("Completed: " + pool.getCompletedTaskCount());
				logger.info("Finished: " + processesFinished);
				if (pool.getTaskCount() == pool.getCompletedTaskCount()
						&& pool.getActiveCount() == 0) {
					pool.shutdown();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		logger.info("Threads have been shutdown");
		Assert.assertEquals(NUMBER_OF_TASKS *  MUTATIONS_PER_THREAD, getNumberOfTestMutations());
		deleteAllTestMutations();
	}

	public static int getNumberOfTestMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String sqlQueryString = "SELECT * FROM Mutation WHERE className LIKE 'test.%'";
		Query query = session.createSQLQuery(sqlQueryString);
		List queryResults = query.list();
		tx.commit();
		session.close();
		return queryResults.size();
	}

	@SuppressWarnings("unchecked")
	private static void deleteAllTestMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "from Mutation WHERE className LIKE 'test.%'";
		Query q = session.createQuery(queryString);
		List<Mutation> mutations = q.list();
		for (Mutation m : mutations) {
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				m.setMutationResult(null);
				session.delete(singleTestResult);
			}
			session.delete(m);
		}
		logger.info(String.format("Deleting %d test mutations", mutations
				.size()));
		tx.commit();
		session.close();
	}

}
