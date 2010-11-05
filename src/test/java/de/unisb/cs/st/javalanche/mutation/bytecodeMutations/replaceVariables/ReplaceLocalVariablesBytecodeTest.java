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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariables4TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class ReplaceLocalVariablesBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public ReplaceLocalVariablesBytecodeTest() throws Exception {
		super(ReplaceVariables4TEMPLATE.class);
		verbose = true;
		MutationProperties.TRACE_BYTECODE = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1");
		checkUnmutated(1, m1, clazz);
		checkMutation(9, MutationType.REPLACE_VARIABLE, 0, new Object[0], 200,
				m1, clazz);
		checkMutation(9, MutationType.REPLACE_VARIABLE, 1, new Object[0], 300,
				m1, clazz);
	}

}
