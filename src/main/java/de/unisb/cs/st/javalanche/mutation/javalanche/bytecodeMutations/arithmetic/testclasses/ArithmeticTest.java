package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses;

import junit.framework.TestCase;

public class ArithmeticTest extends TestCase {

	private Arithmetic arithmetic;

	public void setUp() {
		arithmetic = new Arithmetic();
	}

	public void testMethod1() {
		int test = 8;
		assertEquals(test * 2, arithmetic.method1(test));
	}

	public void testMethod2() {
		assertEquals(0, arithmetic.method2(2));
	}

	public void testMethod3() {
		int test = 8;
		assertEquals(test * -1, arithmetic.method3(test));
	}

	public void testMethod4() {
		assertEquals(2, arithmetic.method4(10));
	}

	public void testMethod5() {
		assertTrue(arithmetic.method5(10));
	}

}
