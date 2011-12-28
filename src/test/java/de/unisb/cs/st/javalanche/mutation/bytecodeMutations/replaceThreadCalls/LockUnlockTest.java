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

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes.LockUnlockTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class LockUnlockTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public LockUnlockTest() throws Exception {
		super(LockUnlockTEMPLATE.class);
		config.setMutationType(MutationType.REPLACE_THREAD_CALL, true);
		// verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testReplaceLock() throws Exception {
		Method m1 = clazz.getMethod("m1");
		checkUnmutated(false, m1, clazz);
		try {
			checkMutation(11, MutationType.REPLACE_THREAD_CALL, 0, true, m1,
					clazz);
			fail("Expected exception caused by mutation");
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			assertThat(cause, instanceOf(IllegalMonitorStateException.class));
		}
	}

	@Test
	public void testReplaceUnlock() throws Exception {
		Method m1 = clazz.getMethod("m1");
		checkUnmutated(false, m1, clazz);
		checkMutation(15, MutationType.REPLACE_THREAD_CALL, 0, true, m1, clazz);
	}

}