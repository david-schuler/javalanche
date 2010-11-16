package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.classes;

import junit.framework.TestCase;

public class AllTests extends TestCase {

	public void testSkipElse() {
		int m1 = SkipElseTEMPLATE.m1(1);
		assertEquals(3, m1);
		int m12 = SkipElseTEMPLATE.m1(10);
		assertEquals(2, m12);
	}
}
