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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes.WaitNotifyTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class WaitNotifyTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public WaitNotifyTest() throws Exception {
		super(WaitNotifyTEMPLATE.class);
		config.setMutationType(MutationType.REPLACE_THREAD_CALL, true);
		// verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testReplaceNotifyAll() throws Exception {
		Method m1 = clazz.getMethod("m1");
		checkUnmutated(2, m1, clazz);
		checkMutation(42, MutationType.REPLACE_THREAD_CALL, 0, 1, m1, clazz);
	}

	@Test
	public void testReplaceNotify() throws Exception {
		Method m2 = clazz.getMethod("m2");
		checkUnmutated(1, m2, clazz);
		checkMutation(65, MutationType.REPLACE_THREAD_CALL, 0, 2, m2, clazz);
	}

}