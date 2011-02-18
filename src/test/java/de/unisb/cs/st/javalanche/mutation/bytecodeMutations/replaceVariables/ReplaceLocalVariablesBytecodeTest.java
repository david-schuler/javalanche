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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.ReplaceVariables4TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.util.JavalancheTestConfiguration;

public class ReplaceLocalVariablesBytecodeTest {

	private static JavalancheConfiguration configBack;

	@BeforeClass
	public static void setUpClass() throws Exception {
		configBack = ConfigurationLocator.getJavalancheConfiguration();
	}

	@AfterClass
	public static void tearDownClass() {
		ConfigurationLocator.setJavalancheConfiguration(configBack);
	}

	@Test
	public void testM1() throws Exception {
		BaseBytecodeTest b = new BaseBytecodeTest(
				ReplaceVariables4TEMPLATE.class);
		JavalancheTestConfiguration config = b.getConfig();
		config.setMutationType(MutationType.REPLACE_VARIABLE, true);
		Class<?> clazz = b.prepareTest();
		Method m1 = clazz.getMethod("m1");
		b.checkUnmutated(1, m1, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), m1.getName(), 9, 0,
				MutationType.REPLACE_VARIABLE);
		m.setOperatorAddInfo("2");
		b.checkMutation(m, new Object[0], 200, m1, clazz);
		m.setOperatorAddInfo("3");
		b.checkMutation(m, new Object[0], 300, m1, clazz);
	}

}
