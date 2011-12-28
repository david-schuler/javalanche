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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls.classes.JoinSleepTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class JoinSleepTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public JoinSleepTest() throws Exception {
		super(JoinSleepTEMPLATE.class);
		config.setMutationType(MutationType.REPLACE_THREAD_CALL, true);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testReplaceJoin() throws Exception {
		Method m3 = clazz.getMethod("m3");
		Matcher<Long> greaterThan100 = Matchers.greaterThan(100l);
		Matcher<Long> lessThan100 = Matchers.lessThan(100l);
		checkUnmutated(lessThan100, m3, clazz);
		Mutation queryMutation = new Mutation(className, m3.getName(), 43, 0,
				MutationType.REPLACE_THREAD_CALL);
		checkMutationWithMatcher(queryMutation, null, greaterThan100, m3, clazz);
	}

	@Test
	public void testReplaceJoinNanos() throws Exception {
		Method m4 = clazz.getMethod("m4");
		Matcher<Long> greaterThan100 = Matchers.greaterThan(100l);
		Matcher<Long> lessThan100 = Matchers.lessThan(100l);
		checkUnmutated(lessThan100, m4, clazz);
		Mutation queryMutation = new Mutation(className, m4.getName(), 55, 0,
				MutationType.REPLACE_THREAD_CALL);
		checkMutationWithMatcher(queryMutation, null, greaterThan100, m4, clazz);
	}

}
