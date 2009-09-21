/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.testclasses;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

public class SysExitTest extends TestCase {

	@Test
	public void testDoNothing() {
	}

	@Ignore("Not to execute during normal tests")
	@Test
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

	@Ignore("Not to execute during normal tests")
	@Test
	public void testMethod2() {
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
