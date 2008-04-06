package org.softevo.mutation.runtime.testsuites;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSuiteUtil {

	private static Logger logger = Logger.getLogger(TestSuiteUtil.class);

	/**
	 * Prevent initialization
	 */
	private TestSuiteUtil() {
	}

	public static Map<String, Test> getAllTests(TestSuite testSuite) {
		Map<String, Test> resultMap = new HashMap<String, Test>();
		return collectTests(testSuite, resultMap);
	}

	private static Map<String, Test> collectTests(TestSuite testSuite,
			Map<String, Test> resultMap) {
		for (Enumeration<Test> e = testSuite.tests(); e.hasMoreElements();) {
			Test test = e.nextElement();
			if (test instanceof TestSuite) {
				TestSuite suite = (TestSuite) test;
				collectTests(suite, resultMap);
			} else if (test instanceof TestCase) {
				TestCase testCase = (TestCase) test;
				String fullTestName = ScanAndCoverageTestSuite
						.getFullTestCaseName(testCase);
				String nameForMap = checkName(fullTestName, resultMap);
				resultMap.put(nameForMap, testCase);
			} else if (test instanceof Test) {
				String testName = getNameForTest(test);
				String keyForMap = checkName(testName, resultMap);
				resultMap.put(keyForMap, test);
			} else {
				throw new RuntimeException("Not handled type: "
						+ test.getClass());
			}
		}
		return resultMap;
	}


	private static String getNameForTest(Test test) {
		String testName = test.getClass().getCanonicalName();
		if (testName == null) {
			testName = "UnknownTest";
		}
		return testName;
	}

	private static String checkName(String testName, Map<String, Test> resultMap) {
		String nameToReturn = testName;
		if (resultMap.containsKey(nameToReturn)) {
			logger.debug("Key already contained: " + testName);
			int i = 1;
			while (resultMap.containsKey(nameToReturn)) {
				i++;
				nameToReturn = testName + "-instance-" + i;
			}
		}
		return nameToReturn;
	}

}
