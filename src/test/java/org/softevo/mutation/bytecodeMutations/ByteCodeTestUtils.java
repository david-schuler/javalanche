package org.softevo.mutation.bytecodeMutations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.coverageResults.db.TestCoverageClassResult;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.javaagent.MutationForRun;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.SingleTestResult;
import org.softevo.mutation.results.persistence.HibernateUtil;
import org.softevo.mutation.results.persistence.QueryManager;

/**
 *
 * Class contains several helper methods for Unittests that test the different
 * mutations.
 *
 * @author David Schuler
 *
 */
public class ByteCodeTestUtils {

	private static final String DEFAULT_OUTPUT_FILE = "redefine-ids.txt";

	private static Logger logger = Logger.getLogger(ByteCodeTestUtils.class);

	private ByteCodeTestUtils() {
	}

	public static void deleteCoverageData(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from TestCoverageClassResult WHERE className=:clname");
		query.setString("clname", className);
		List l = query.list();
		for (Object o : l) {
			session.delete(o);
		}
		tx.commit();
		session.close();
	}

	public static void generateTestDataInDB(String classFileName,
			CollectorByteCodeTransformer collectorTransformer) {
		File classFile = new File(classFileName);
		FileTransformer ft = new FileTransformer(classFile);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		collectorTransformer.setMpc(mpc);
		ft.process(collectorTransformer);
		mpc.toDB();
	}

	@SuppressWarnings("unchecked")
	public static void deleteTestMutationResult(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("from Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", className);
		List<Mutation> mutations = q.list();
		for (Mutation m : mutations) {
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				logger.info("Trying to delete + " + singleTestResult);
				m.setMutationResult(null);
				session.delete(singleTestResult);
			}
		}
		tx.commit();
		session.close();
	}

	public static void deleteTestMutationResultOLD(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("from Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", className);
		List mutations = q.list();
		for (Object m : mutations) {
			((Mutation) m).setMutationResult(null);
		}
		tx.commit();
		session.close();
	}

	public static void deleteMutations(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("delete from Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", className);
		int rowsAffected = q.executeUpdate();
		logger.info("Deleted " + rowsAffected + " rows");
		tx.commit();
		session.close();
	}

	public static void generateCoverageData(String className,
			String[] testCaseNames, int[] linenumbers) {
		List<TestCoverageTestCaseName> names = new ArrayList<TestCoverageTestCaseName>();
		for (String name : testCaseNames) {
			names.add(TestCoverageTestCaseName
					.getTestCoverageTestCaseName(name));
		}
		List<TestCoverageLineResult> lineResult = new ArrayList<TestCoverageLineResult>();
		List<String> testCaseNamesList = Arrays.asList(testCaseNames);
		for (int number : linenumbers) {
			lineResult
					.add(new TestCoverageLineResult(number, testCaseNamesList));
		}
		TestCoverageClassResult classResult = new TestCoverageClassResult(
				className, lineResult);
		logger.info(classResult);
		try {
			QueryManager.save(classResult);
		} catch (org.hibernate.exception.ConstraintViolationException e) {
		}
	}

	public static String[] generateTestCaseNames(String testCaseClassName,
			int numberOfMethods) {
		String[] testCaseNames = new String[numberOfMethods];
		for (int i = 0; i < numberOfMethods; i++) {
			testCaseNames[i] = testCaseClassName + ".testMethod" + (i + 1);
		}
		return testCaseNames;
	}

	public static String getFileNameForClass(Class clazz) {
		String result = null;
		try {
			String className = clazz.getSimpleName() + ".class";
			result = clazz.getResource(className).getFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Tests if exactly one testMethod failed because of the mutation.
	 *
	 * @param testClassName
	 *            The class that test the mutated class.
	 */
	@SuppressWarnings("unchecked")
	public static void testResults(String testClassName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		int nonNulls = 0;
		for (Mutation m : mList) {
			System.out.println(m);
			SingleTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				Assert.assertEquals("Mutation: " + m, 1, singleTestResult
						.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected failing tests because of mutations",
				nonNulls >= mList.size());
	}

	@SuppressWarnings("unchecked")
	public static void redefineMutations(String testClassName) {
		List<Long> ids = new ArrayList<Long>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		for (Mutation m : mList) {
			ids.add(m.getId());
		}
		tx.commit();
		session.close();
		StringBuilder sb = new StringBuilder();
		for (Long l : ids) {
			sb.append(l + "\n");
		}
		File file = new File(DEFAULT_OUTPUT_FILE);
		Io.writeFile(sb.toString(), file);
		System.setProperty("mutation.file", file.getAbsolutePath());
		MutationForRun.getInstance().reinit();
	}

	public static void addMutations(String filename){
		FileTransformer ft = new FileTransformer(new File(filename));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new MutationScannerTransformer(mpc));
		mpc.toDB();
	}
}
