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
package de.unisb.cs.st.javalanche.mutation.runtime.testsuites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.TestName;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;

public class CheckNamesTestSuite extends TestSuite {

	private static final boolean RUN_TESTS = false;

	private static Logger logger = Logger.getLogger(CheckNamesTestSuite.class);

	public CheckNamesTestSuite(String name) {
		super(name);
	}

	@Override
	public void run(TestResult result) {
		TestResult firstTestResult = new TestResult();
		super.run(firstTestResult);
		logger.info("First test result " + firstTestResult.runCount());
		Map<String, Test> allTests = TestSuiteUtil.getAllTests(this);
		if (allTests.size() != firstTestResult.runCount()) {
			throw new RuntimeException("Found unequal number of tests."
					+ "\n Tests to run: " + allTests.size()
					+ "\n Tests that were run: " + firstTestResult.runCount());
		}
		checkTests(allTests);
		int testCount = 0;
		Set<Entry<String, Test>> allTestEntrySet = allTests.entrySet();
		if (RUN_TESTS) {
			for (Map.Entry<String, Test> entry : allTestEntrySet) {
				testCount++;
				logger.info("Running Test (" + testCount + "/"
						+ allTestEntrySet.size() + ")" + entry.getValue());
				try {
					runTest(entry.getValue(), result);
				} catch (Error e) {
					logger.warn("Exception During test " + e.getMessage());
					e.printStackTrace();
				}
				logger.info("Test Finished (" + testCount + ")"
						+ entry.getValue());
			}
		}
	}

	private void checkTests(Map<String, Test> allTests) {
		List<TestName> testNames = getTestsFromDB();
		List<String> testNamesStringList = new ArrayList<String>();
		for (TestName testName : testNames) {
			testNamesStringList.add(testName.getName());
			if (!allTests.containsKey(testName.getName())) {
				throw new RuntimeException(
						"Test from db not contained in this run: "
								+ testName.getName());
			}
		}
		logger.info(String.format("Found all %d tests from db ", testNames
				.size()));
		for (String testNameFromMap : allTests.keySet()) {
			if (!testNamesStringList.contains(testNameFromMap)) {
				throw new RuntimeException(
						"Test of this run not contained in db:  "
								+ testNameFromMap + "\nList:\n"
								+ testNamesStringList);
			}
		}
		logger.info(String.format("Found all %d actual tests in db ", allTests
				.size()));
	}

	@SuppressWarnings("unchecked")
	private List<TestName> getTestsFromDB() {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from TestName as tm where tm.name LIKE '"
						+ MutationProperties.PROJECT_PREFIX + "%'");
		// System.out.println(MutationProperties.PROJECT_PREFIX);
		// query.setString("prefix", MutationProperties.PROJECT_PREFIX);
		List<TestName> testnames = query.list();
		tx.commit();
		session.close();
		return testnames;
	}

	/**
	 * Transforms a {@link TestSuite} to a {@link CheckNamesTestSuite}. This
	 * method is called by instrumented code to insert this class instead of the
	 * TestSuite.
	 *
	 * @param testSuite
	 *            the original TestSuite
	 * @return a {@link CheckNamesTestSuite} that contains the given TestSuite
	 */
	public static CheckNamesTestSuite toCheckNamesTestSuite(TestSuite testSuite) {
		logger.info("Transforming TestSuite to enable mutations");
		CheckNamesTestSuite returnTestSuite = new CheckNamesTestSuite(testSuite
				.getName());
		returnTestSuite.addTest(testSuite);
		return returnTestSuite;
	}

}
