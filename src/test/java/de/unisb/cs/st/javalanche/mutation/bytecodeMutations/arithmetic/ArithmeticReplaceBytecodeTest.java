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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.classes.ArithmeticTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class ArithmeticReplaceBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public ArithmeticReplaceBytecodeTest() throws Exception {
		super(ArithmeticTEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(2, 4, m1, clazz);
		checkMutation(27, MutationType.ARITHMETIC_REPLACE, 0, 2, 0, m1, clazz);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 0, m2, clazz);
		checkMutation(31, MutationType.ARITHMETIC_REPLACE, 0, 2, 4, m2, clazz);
	}

	@Test
	public void testM3() throws Exception {
		Method m3 = clazz.getMethod("m3", int.class);
		checkUnmutated(2, -2, m3, clazz);
		checkMutation(35, MutationType.ARITHMETIC_REPLACE, 0, 2, 1, m3, clazz);
	}

	@Test
	public void testM4() throws Exception {
		Method m4 = clazz.getMethod("m4", int.class);
		checkUnmutated(17, 3, m4, clazz);
		checkMutation(40, MutationType.ARITHMETIC_REPLACE, 0, 17, 85, m4, clazz);
	}

	@Test
	public void testM5() throws Exception {
		Method m5 = clazz.getMethod("m5", int.class);
		checkUnmutated(2, false, m5, clazz);
		checkMutation(45, MutationType.ARITHMETIC_REPLACE, 0, 2, true, m5,
				clazz);
	}

}
