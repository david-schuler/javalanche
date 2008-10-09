package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject;

import junit.framework.TestCase;

public class IntegerConstantsTest extends TestCase {
	private IntegerConstants ric;

	public void setUp() {
		ric = new IntegerConstants();
	}

	public void testMethod1() {
		assertEquals(5 * 50, ric.method1(50));
	}

	public void testMethod2() {
		assertEquals(500l, ric.method2());
	}

	public void testMethod3() {
		assertTrue(ric.method3(5));
	}

}
