/*
* Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

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
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstants;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstantsTest;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class ReplaceIntegerTest {

	//-javaagent:./target/javaagent.jar -Dinvariant.mode=OFF -Dmutation.run.mode=mutation-no-invariant -Dmutation.coverage.information=false
	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject.IntegerConstants";
		ByteCodeTestUtils.doSetup(classname,new RicCollectorTransformer(null));
	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = IntegerConstants.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = IntegerConstantsTest.class
			.getName();

//	private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
//			.getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 3);

	private static final int[] linenumbers = { 6, 11, 12, 16, 17, 18 };

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
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
		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
		TestSuite suite = new TestSuite(IntegerConstantsTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		IntegerConstants ric = new IntegerConstants();
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
		for (Mutation m : mList) {
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null) {
				nonNulls++;
				if (m.getMutationType() != Mutation.MutationType.NO_MUTATION && m.getLineNumber() != 16 && m.getLineNumber() != 18) {
					Assert.assertEquals("Mutation: " + m, 1, singleTestResult
							.getNumberOfErrors()
							+ singleTestResult.getNumberOfFailures());
				}
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
