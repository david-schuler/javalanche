package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses;

import junit.framework.TestCase;

public class MethodCallsTest extends TestCase{

	MethodCalls m = new MethodCalls();

	public void testMethod1() {
		int result = m.supressFail1();
		assertEquals(23, result);
	}

	public void testMethod2() {
		int result = m.supressFail2();
		assertEquals(23, result);
	}

	public void testMethod3() {
		int result = m.supressFail3();
		assertEquals(23, result);
	}

	public void testMethod4() {
		int result = m.ignoreMethodForResult();
		assertEquals(23, result);
	}
}
