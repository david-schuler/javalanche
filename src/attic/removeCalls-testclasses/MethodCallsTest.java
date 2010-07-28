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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses;

import junit.framework.TestCase;

public class MethodCallsTest extends TestCase{

	MethodCalls m = new MethodCalls();

	public void testMethod1() {
		int result = m.supressFail1();
		assertEquals(23, result);
	}

	public void testMethod2() {
		int result = m.supressFail2();
		assertEquals(23, result);
	}

	public void testMethod3() {
		int result = m.supressFail3();
		assertEquals(23, result);
	}

	public void testMethod4() {
		int result = m.ignoreMethodForResult();
		assertEquals(23, result);
	}
}
