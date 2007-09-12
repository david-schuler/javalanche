package org.softevo.mutation.bytecodeMutations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.coverageResults.db.TestCoverageClassResult;
import org.softevo.mutation.coverageResults.db.TestCoverageLineResult;
import org.softevo.mutation.coverageResults.db.TestCoverageTestCaseName;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
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

	public static void generateTestDataInDB(String classFileName, CollectorByteCodeTransformer collectorTransformer) {
		File classFile = new File(classFileName);
		FileTransformer ft = new FileTransformer(classFile);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		collectorTransformer.setMpc(mpc);
		ft.process(collectorTransformer);
		mpc.toDB();
	}

	public static void deleteTestMutationResult(String className) {
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

	public static String getFileNameForClass(Class clazz){
		String result = null;
		try {
			String className = clazz.getSimpleName() + ".class";
			result = clazz.getResource(className).getFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
