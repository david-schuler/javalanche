package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.testclasses;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

public class SysExitTest extends TestCase {

	@Test
	public void testDoNothing(){}

	@Ignore("Not to execute during normal tests")
	@Test
	public void tmethod1() {
		SysExit sExit = new SysExit();
		try {
			sExit.method1();
			fail();
		} catch (RuntimeException e) {
		}

		try {
			sExit.method2();
			fail();
		} catch (RuntimeException e) {
		}
		try {
			sExit.systemExit("aa");
			fail();
		} catch (RuntimeException e) {
		}

	}

}
