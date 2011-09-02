///*
// * Copyright (C) 2011 Saarland University
// * 
// * This file is part of Javalanche.
// * 
// * Javalanche is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * Javalanche is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser Public License for more details.
// * 
// * You should have received a copy of the GNU Lesser Public License
// * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
// */
//package de.unisb.cs.st.javalanche.mutation.testutil;
//
//import static org.junit.Assert.*;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.util.CheckClassAdapter;
//
//import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationTransformer;
//import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
//import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
//import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
//import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;
//
//public class LangUtilTest {
//
//	private static Logger logger = Logger.getLogger(LangUtilTest.class);
//
//	private static final String LANG_UTIL = "keyForLangUtil";
//
//	private static class TestClassLoader extends ClassLoader {
//
//		private final byte[] transformed;
//
//		public TestClassLoader(byte[] transformed) {
//			this.transformed = transformed;
//		}
//
//		@Override
//		public Class<?> loadClass(String name) throws ClassNotFoundException {
//			if (name.equals(LANG_UTIL)) {
//				return defineClass(TestProperties.LANG_UTIL_CLASS_NAME,
//						transformed, 0, transformed.length);
//			}
//			return super.loadClass(name);
//		}
//	}
//
//	// @Ignore("AspectJ has to be on the classpath")
//	@Test
//	public void testLangUtil() throws IOException {
//			MutationPossibilityCollector mpc = new MutationPossibilityCollector();
//			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//			ClassVisitor cv = new CheckClassAdapter(cw);
//			cv = new MutationsCollectorClassAdapter(cv, mpc);
//			InputStream is = LangUtilTest.class.getClassLoader()
//					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			ClassReader cr = new ClassReader(is);
//			mpc.toDB();
//			
//			logger.info("Mutations added");
//			BytecodeTransformer bct = new MutationTransformer();
//			InputStream is2 = LangUtilTest.class
//					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
//			byte[] transformed;
//
//	}
//}
