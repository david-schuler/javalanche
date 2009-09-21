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
package de.unisb.cs.st.javalanche.mutation.testutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class LangUtilTest {

	private static Logger logger = Logger.getLogger(LangUtilTest.class);

	private static final String LANG_UTIL = "keyForLangUtil";

	private static class TestClassLoader extends ClassLoader {

		private final byte[] transformed;

		public TestClassLoader(byte[] transformed) {
			this.transformed = transformed;
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.equals(LANG_UTIL)) {
				return defineClass(TestProperties.LANG_UTIL_CLASS_NAME, transformed, 0,
						transformed.length);
			}
			return super.loadClass(name);
		}
	}

	@Ignore("AspectJ has to be on the classpath")
	@Test
	public void testLangUtil() {
		try {
			System.out.println("Test Started");
			MutationPossibilityCollector mpc = new MutationPossibilityCollector();
			BytecodeTransformer bt = new MutationScannerTransformer(mpc);
			InputStream is = LangUtilTest.class
					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
			bt.transformBytecode(new ClassReader(is));
			mpc.toDB();
			MutationManager.setApplyAllMutation(true);
			logger.info("Mutations added");
			BytecodeTransformer bct = new MutationTransformer();
			InputStream is2 = LangUtilTest.class
					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
			byte[] transformed;
			transformed = bct.transformBytecode(new ClassReader(is2));
			TestClassLoader tcl = new TestClassLoader(transformed);
			tcl.loadClass(LANG_UTIL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		MutationManager.setApplyAllMutation(false);
	}
}
