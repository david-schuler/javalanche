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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class RemoveCallsBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public RemoveCallsBytecodeTest() throws Exception {
		super(RemoveCallsTEMPLATE.class);
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1");
		checkUnmutated(23, m1, clazz);
		checkMutation(7, MutationType.REMOVE_CALL, 0, new Object[0], 5, m1,
				clazz);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 23, m2, clazz);
		checkUnmutated(0, 5, m2, clazz);
		checkMutation(19, MutationType.REMOVE_CALL, 0, 2, 5, m2, clazz);
		checkMutation(19, MutationType.REMOVE_CALL, 0, 0, 5, m2, clazz);
	}

	@Test
	public void testM3() throws Exception {
		Method m2 = clazz.getMethod("m3");
		checkUnmutated(23, m2, clazz);
		checkMutation(34, MutationType.REMOVE_CALL, 0, 5, m2, clazz);
	}

	@Test
	public void testM4() throws Exception {
		Method m4 = clazz.getMethod("m4");
		checkUnmutated(23, m4, clazz);
		checkMutation(47, MutationType.REMOVE_CALL, 0, 23, m4, clazz);
	}

}
