package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Junit3Suite {

	public static Test suite() {
		TestSuite suite = new TestSuite(Junit3Suite.class.getName());
		suite.addTestSuite(Junit3TestCase2.class);
		suite.addTestSuite(TestCaseForJunitTest.class);
		return suite;
	}

}
