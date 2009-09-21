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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.testclasses.jumps;

import junit.framework.TestCase;


public class JumpsTest extends TestCase {

	Jumps jumps = new Jumps();

	// @Test
	public void testMethod1() {
		assertEquals(1, jumps.method1(1));
		assertEquals(-1, jumps.method1(-51));
	}

	// @Test
	public void testMethod2() {
		assertEquals(1, jumps.method2(5));
		assertEquals(0, jumps.method2(0));
		assertEquals(-1, jumps.method2(-9));
	}

	// @Test
	public void testMethod3() {
		assertEquals(-5, jumps.method3(5));
	}

	// @Test
	public void testMethod4() {
		assertEquals(true, jumps.method4(0));
		assertEquals(false, jumps.method4(-8));
	}

	public void testMethod5() {
		jumps.method1(0);
		jumps.method2(0);
		jumps.method3(0);
		jumps.method4(0);
	}
}
