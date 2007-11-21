package org.softevo.mutation.bytecodeMutations.sysexit.testclasses;

import junit.framework.TestCase;

public class SysExitTest extends TestCase {

	public void testMethod1() {
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
