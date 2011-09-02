package org.junit.internal.runners;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Junit38Suite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(Junit38Test.class);
		return suite;
	}
}
