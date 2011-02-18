///*
// * Copyright (C) 2010 Saarland University
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
//package de.unisb.cs.st.javalanche.mutation.properties;
//
//import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
//import static org.junit.Assert.*;
//import static org.hamcrest.Matchers.*;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//
//public class MutationPropertiesTest {
//
//	// private static final String MP_CLASS_NAME =
//	// "de.unisb.cs.st.javalanche.mutation.properties.MutationProperties";
//
//	private static class MyClassLoader extends ClassLoader {
//
//		private final ClassLoader parent2;
//		private final String propertiesFileContent;
//
//		public MyClassLoader(ClassLoader parent, String propertiesFileContent) {
//			super(parent);
//			parent2 = parent;
//			this.propertiesFileContent = propertiesFileContent;
//
//		}
//
//		@Override
//		public InputStream getResourceAsStream(String name) {
//			System.out.println("MyClassLoader.getResourceAsStream()");
//			if (MutationProperties.PROPERTIES_FILE.equals(name)) {
//				return new ByteArrayInputStream(propertiesFileContent
//						.getBytes());
//			}
//			return super.getResourceAsStream(name);
//		}
//
//		@Override
//		public Class<?> loadClass(String name) throws ClassNotFoundException {
//			if (MP_CLASS_NAME.equals(name)) {
//				InputStream in = parent2.getResourceAsStream(name.replace('.',
//						'/')
//						+ ".class");
//				ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//				int c;
//				try {
//					while ((c = in.read()) != -1) {
//						out.write(c);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				byte[] b = out.toByteArray();
//				defineClass(MP_CLASS_NAME, b, 0, b.length);
//			}
//			return super.loadClass(name);
//		}
//
//	}
//
//	@SuppressWarnings("deprecation")
//	private URLClassLoader getUrlClassLoader() throws MalformedURLException {
//		String cp = System.getProperty("java.class.path");
//		String[] entries = cp.split(":");
//		List<URL> urls = new ArrayList<URL>();
//		for (String s : entries) {
//			URL u = new File(s).toURL();
//			urls.add(u);
//		}
//		URLClassLoader urlCL = new URLClassLoader(urls.toArray(new URL[0]),
//				null);
//		return urlCL;
//	}
//
//	@Test
//	public void testWithOwnClassLoader() throws ClassNotFoundException,
//			MalformedURLException, InstantiationException,
//			IllegalAccessException, SecurityException, NoSuchFieldException {
//		ClassLoader cl = getUrlClassLoader();
//		Class<?> loadClass = cl.loadClass(MP_CLASS_NAME);
//		Field field2 = loadClass.getField("TRACE_BYTECODE");
//		assertTrue(Boolean.FALSE.equals(field2.get(null)));
//	}
//
//	@Test
//	public void testOverwriteProperyInFile() throws ClassNotFoundException,
//			MalformedURLException, InstantiationException,
//			IllegalAccessException, SecurityException, NoSuchFieldException {
//		ClassLoader cl = new MyClassLoader(getUrlClassLoader(),
//				"javalanche.trace.bytecode=true\n");
//		Class<?> loadClass = cl.loadClass(MP_CLASS_NAME);
//		Field field2 = loadClass.getField("TRACE_BYTECODE");
//		assertEquals(Boolean.TRUE, field2.get(null));
//	}
//
//	@Test
//	public void testBooleanOverwriteProperyInFile()
//			throws ClassNotFoundException, MalformedURLException,
//			InstantiationException, IllegalAccessException, SecurityException,
//			NoSuchFieldException {
//		ClassLoader cl = new MyClassLoader(getUrlClassLoader(),
//				STOP_AFTER_FIRST_FAIL_KEY + "=false\n");
//		Class<?> loadClass = cl.loadClass(MP_CLASS_NAME);
//		Field field2 = loadClass.getField("STOP_AFTER_FIRST_FAIL");
//		assertEquals(Boolean.FALSE, field2.get(null));
//	}
//
//
//
//	@Test
//	public void testReleaseProperties() {
//		assertFalse(TRACE_BYTECODE);
//		assertTrue(STOP_AFTER_FIRST_FAIL);
//		assertThat(DEFAULT_TIMEOUT_IN_SECONDS, is(10));
//		assertTrue(IGNORE_MESSAGES);
//		assertTrue(IGNORE_EXCEPTION_TRACES);
//		assertThat(SAVE_INTERVAL, is(50));
//		assertTrue(ENABLE_RIC);
//		assertTrue(ENABLE_NEGATE_JUMPS);
//		assertTrue(ENABLE_ARITHMETIC_REPLACE);
//		assertTrue(ENABLE_REMOVE_CALLS);
//		assertFalse(ENABLE_REPLACE_VARIABLES);
//		assertFalse(JUNIT4_MODE);
//		assertThat(TEST_PERMUTATIONS, is(10));
//	}
//
// }
