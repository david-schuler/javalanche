/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject;

import junit.framework.TestCase;

public class IntegerConstantsTest extends TestCase {
	private IntegerConstants ric;

	public void setUp() {
		ric = new IntegerConstants();
	}

	public void testMethod1() {
		assertEquals(5 * 50, ric.method1(50));
	}

	public void testMethod2() {
		assertEquals(500l, ric.method2());
	}

	public void testMethod3() {
		assertTrue(ric.method3(5));
	}

}
