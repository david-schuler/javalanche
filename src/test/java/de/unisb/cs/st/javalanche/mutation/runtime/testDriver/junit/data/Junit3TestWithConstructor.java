package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit.data;

import junit.framework.TestCase;

public class Junit3TestWithConstructor extends TestCase{

	private final int value;

	public Junit3TestWithConstructor(int value) {
		this.value = value;

	}

	public void testMethod() throws Exception {
		int x = value * 0;
		assertEquals(0, x);
	}

}
