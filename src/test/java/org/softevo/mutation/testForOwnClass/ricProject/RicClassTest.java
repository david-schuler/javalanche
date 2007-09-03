package org.softevo.mutation.testForOwnClass.ricProject;


import junit.framework.TestCase;

public class RicClassTest extends TestCase {
	private RicClass ric;

	public void setUp(){
		ric = new RicClass();
	}

	public void testMethod1() {
		assertEquals(ric.method1(50), 5*50);
	}

	public void testMethod2() {
		assertEquals(ric.method2(), 500l);
	}

	public void testMethod3() {
		assertTrue(ric.method3(4));
	}
	

}
