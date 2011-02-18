/*
 * Copyright (C) 2010 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

/**
 * 
 * Class contains several helper methods for UnitTests that test the different
 * mutations.
 * 
 * @author David Schuler
 * 
 */
@SuppressWarnings("unchecked")
public class ByteCodeTestUtils {

	public static final String DEFAULT_OUTPUT_FILE = "redefine-ids.txt";

	private static Logger logger = Logger.getLogger(ByteCodeTestUtils.class);

	private ByteCodeTestUtils() {
	}

	public static void deleteCoverageData(String className) {

		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		List<Long> ids = new ArrayList<Long>();
		for (Mutation mutation : mutationsForClass) {
			ids.add(mutation.getId());
		}
		// TODO
	}

	public static MutationPossibilityCollector generateTestDataInDB(
			String classFileName,
			CollectorByteCodeTransformer collectorTransformer) {
		File classFile = new File(classFileName);
		byte[] classBytes = null;
		try {
			classBytes = FileUtils.readFileToByteArray(classFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// ClassReader cr = new ClassReader(classBytes);
		// FileTransformer ft = new FileTransformer(classFile);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		collectorTransformer.setMpc(mpc);
		// ft.process(collectorTransformer);
		collectorTransformer.transformBytecode(classBytes);
		mpc.toDB();
		return mpc;
	}

	public static void deleteTestMutationResult(String className) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = String
				.format("from Mutation where classname=:clname");
		Query q = session.createQuery(queryString);
		q.setString("clname", className);
		List<Mutation> mutations = q.list();
		for (Mutation m : mutations) {
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				m.setMutationResult(null);
				session.delete(singleTestResult);
			}
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

		Set<String> tests = new HashSet<String>(Arrays.asList(testCaseNames));
		List<Mutation> mutations = QueryManager.getMutationsForClass(className);
		Map<Long, Set<String>> coverageData = new HashMap<Long, Set<String>>();
		for (Mutation mutation : mutations) {
			coverageData.put(mutation.getId(), tests);
		}
		MutationCoverageFile.saveCoverageData(coverageData);
		MutationCoverageFile.reset();
	}

	public static String[] generateTestCaseNames(String testCaseClassName,
			int numberOfMethods) {
		String[] testCaseNames = new String[numberOfMethods];
		for (int i = 0; i < numberOfMethods; i++) {
			testCaseNames[i] = testCaseClassName + ".testMethod" + (i + 1);
		}
		return testCaseNames;
	}

	/**
	 * Tests if exactly one testMethod failed because of the mutation.
	 * 
	 * @param testClassName
	 *            The class that test the mutated class.
	 */
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
			if (m.getMutationType() != MutationType.NO_MUTATION) {
				MutationTestResult singleTestResult = m.getMutationResult();
				if (singleTestResult != null) {
					nonNulls++;
					Assert.assertEquals("Mutation: " + m, 1,
							singleTestResult.getNumberOfErrors()
									+ singleTestResult.getNumberOfFailures());
				}
			}
		}

		tx.commit();
		session.close();
		Assert.assertTrue("Expected failing tests because of mutations",
				nonNulls >= mList.size() / 2);
	}

	public static void redefineMutations(String testClassName,
			JavalancheTestConfiguration c) {
		List<Long> ids = new ArrayList<Long>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		for (Mutation m : mList) {
			System.out.println("REDEFINE: " + m);
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
		c.setMutationIdFile(file.getAbsolutePath());
	}

}
