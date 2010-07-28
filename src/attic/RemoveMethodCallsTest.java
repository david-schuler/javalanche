/*
* Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.CollectorByteCodeTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCalls;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCallsTest;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class RemoveMethodCallsTest {

	// -javaagent:./target/javaagent.jar -Dinvariant.mode=OFF
	// -Dmutation.run.mode=mutation
	// -Dmutation.coverage.information=false

	private static class RemoveMethodCallsTransformer extends
			CollectorByteCodeTransformer {

		@Override
		protected ClassVisitor classVisitorFactory(ClassWriter cw) {
			return new RemoveMethodCallsPossibilityClassAdapter(cw, mpc,
					new HashMap<Integer, Integer>());
		}

	}

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses.MethodCalls";
		ByteCodeTestUtils
				.doSetup(classname, new RemoveMethodCallsTransformer());
	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = MethodCalls.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = MethodCallsTest.class
			.getName();

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 4);

	private static final int[] linenumbers = { 7, 19, 31, 44 };

	@Before
	public void setup() {
		// ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.generateCoverageData(TEST_CLASS_NAME, testCaseNames,
				linenumbers);
	}

	@After
	public void tearDown() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
	}

	@Test
	public void runTests() {
		MutationProperties.RUN_MODE = RunMode.MUTATION_TEST;
		System.setProperty(MutationProperties.RESULT_FILE_KEY,
				"target/unittestResults.xml");
		ByteCodeTestUtils.redefineMutations(TEST_CLASS_NAME);
		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
		TestSuite suite = new TestSuite(MethodCallsTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		MethodCalls methodCalls = new MethodCalls(); // ensure that class is
		// loaded
		selectiveTestSuite.run(new TestResult());
		testResults(TEST_CLASS_NAME);
	}

	/**
	 * Tests if exactly one testMethod failed because of the mutation.
	 * 
	 * @param testClassName
	 *            The class that test the mutated class.
	 */
	@SuppressWarnings("unchecked")
	private static void testResults(String testClassName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		int nonNulls = 0;

		List<Integer> expectOneError = Arrays.asList(7, 19, 31);
		List<Integer> expectNoError = Arrays.asList(44);

		for (Mutation m : mList) {
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				int expectedErrors = 1;
				if (expectNoError.contains(m.getLineNumber())) {
					expectedErrors = 0;
				}
				Assert.assertEquals("Mutation: " + m, expectedErrors,
						singleTestResult
						.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());
			} else {
				System.out.println(m);
			}
		}
		tx.commit();
		session.close();
		Assert.assertTrue("Expected failing tests because of mutations",
				nonNulls >= mList.size());
	}

}
