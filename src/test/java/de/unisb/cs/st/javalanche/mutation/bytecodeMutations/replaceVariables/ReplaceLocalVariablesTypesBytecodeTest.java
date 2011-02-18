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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariables5TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class ReplaceLocalVariablesTypesBytecodeTest {

	private static Class<?> clazz;
	private static JavalancheConfiguration configBack;
	private static BaseBytecodeTest b;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configBack = ConfigurationLocator.getJavalancheConfiguration();
		b = new BaseBytecodeTest(ReplaceVariables5TEMPLATE.class);
		JavalancheTestConfiguration config = b.getConfig();
		config.setMutationType(MutationType.REPLACE_VARIABLE, true);
		clazz = b.prepareTest();
	}

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}


	@Test
	public void testDoubles() throws Exception {
		Method m1 = clazz.getMethod("m1");
		b.checkUnmutated(1., m1, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), m1.getName(), 9, 0,
				MutationType.REPLACE_VARIABLE);
		m.setOperatorAddInfo("3");
		b.checkMutation(m, new Object[0], 2.2, m1, clazz);
		m.setOperatorAddInfo("5");
		b.checkMutation(m, new Object[0], 3.2, m1, clazz);
	}

	@Test
	public void testFloats() throws Exception {
		Method m = clazz.getMethod("m2");
		b.checkUnmutated(1.f, m, clazz);
		b.checkMutation(15, MutationType.REPLACE_VARIABLE, 0, new Object[0],
				1.2f, m, clazz);
	}

	@Test
	public void testLongs() throws Exception {
		Method m = clazz.getMethod("m3");
		b.checkUnmutated(1l, m, clazz);
		b.checkMutation(21, MutationType.REPLACE_VARIABLE, 0, new Object[0],
				1234567890l, m, clazz);
	}

	@Test
	public void testObject() throws Exception {
		Method m = clazz.getMethod("m4");
		b.checkUnmutated("A", m, clazz);
		b.checkMutation(27, MutationType.REPLACE_VARIABLE, 0, new Object[0],
				"B",
				m, clazz);
	}

}
