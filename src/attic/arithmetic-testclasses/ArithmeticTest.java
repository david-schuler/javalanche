/*
* Copyright (C) 2011 Saarland University
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
