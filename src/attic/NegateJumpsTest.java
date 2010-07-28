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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import static org.hamcrest.number.OrderingComparisons.*;
import static org.junit.Assert.*;

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
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.Jumps;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.JumpsTest;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.runtime.testsuites.MutationTestSuite;

public class NegateJumpsTest {

	static {
		String classname = "de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps.Jumps";
		ByteCodeTestUtils.doSetup(classname,
				new NegateJumpsCollectorTransformer(null));

	}

	@SuppressWarnings("unchecked")
	private static final Class TEST_CLASS = Jumps.class;

	private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

	private static final String UNITTEST_CLASS_NAME = JumpsTest.class.getName();

	// private static final String TEST_CLASS_FILENAME = ByteCodeTestUtils
	// .getFileNameForClass(TEST_CLASS);

	private static String[] testCaseNames = ByteCodeTestUtils
			.generateTestCaseNames(UNITTEST_CLASS_NAME, 5);

	private static final int[] linenumbers = { 6, 14, 16, 25, 34, 37 };

	@Before
	public void setup() {
		ByteCodeTestUtils.deleteTestMutationResult(TEST_CLASS_NAME);
		ByteCodeTestUtils.deleteCoverageData(TEST_CLASS_NAME);
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
		ByteCodeTestUtils.redefineMutations(TEST_CLASS_NAME);
		MutationTestSuite selectiveTestSuite = new MutationTestSuite();
		TestSuite suite = new TestSuite(JumpsTest.class);
		selectiveTestSuite.addTest(suite);
		@SuppressWarnings("unused")
		Jumps jumps = new Jumps(); // ensure that class is loaded
		// ClassLoader cl = NegateJumpsTest.class.getClassLoader()
		CoverageDataRuntime instance = CoverageDataRuntime.getInstance();
		selectiveTestSuite.run(new TestResult());
		testResults();
	}

	@SuppressWarnings("unchecked")
	private void testResults() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", TEST_CLASS_NAME);
		List<Mutation> mList = query.list();
		int nonNulls = 0;
		for (Mutation m : mList) {
			System.out.println(m);
			MutationTestResult singleTestResult = m.getMutationResult();
			if (singleTestResult != null
					&& m.getMutationType() != MutationType.NO_MUTATION) {
				nonNulls++;
				Assert.assertTrue(2 >= singleTestResult.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());
				Assert.assertTrue("Expected at least one error for mutation:  "
						+ m, 1 <= singleTestResult.getNumberOfErrors()
						+ singleTestResult.getNumberOfFailures());

				Assert.assertTrue(singleTestResult.isTouched());
			}
		}
		tx.commit();
		session.close();
		assertThat(nonNulls, greaterThan(5));
	}

}
