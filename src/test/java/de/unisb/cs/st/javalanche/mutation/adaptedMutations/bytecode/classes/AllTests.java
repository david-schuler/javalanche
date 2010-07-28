package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes");
		// $JUnit-BEGIN$
		suite
				.addTestSuite(de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes.AllTests.class);
		// $JUnit-END$
		return suite;
	}

	public void testSkipElse() {
		int m1 = SkipElseTEMPLATE.m1(1);
		assertEquals(3, m1);
		int m12 = SkipElseTEMPLATE.m1(10);
		assertEquals(2, m12);
	}
}
