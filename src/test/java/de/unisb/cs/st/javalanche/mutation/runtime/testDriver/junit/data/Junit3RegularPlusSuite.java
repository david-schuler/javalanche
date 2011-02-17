package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Junit3RegularPlusSuite extends TestCase {

	public static int check = 0;
	public static int suite = 0;

	public void test1() {
		check += 1;
		int res = 4 * 5;
		assertEquals(20, res);
	}

	public void test2() {
		check += 2;
		int res = 4 * 5 + 1;
		assertEquals(21, res);
	}

	public static Test suite() {
		suite += 1;
		TestSuite suite = new TestSuite();
		suite.addTestSuite(Junit3RegularPlusSuite.class);
		return suite;
	}
}
