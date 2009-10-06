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
package de.unisb.cs.st.javalanche.mutation.properties;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.ds.util.io.Io;

public class MutationPropertiesTest {

	private static final String MP_CLASS_NAME = "de.unisb.cs.st.javalanche.mutation.properties.MutationProperties";

	
	private static class MyClassLoader extends ClassLoader {

		private final ClassLoader parent2;

		public MyClassLoader(ClassLoader parent) {
			super(parent);
			parent2 = parent;

		}
		
		
		@Override
		public InputStream getResourceAsStream(String name) {
			System.out.println("MyClassLoader.getResourceAsStream()");
			if (MutationProperties.PROPERTIES_FILE.equals(name)) {
				return new ByteArrayInputStream("mutation.trace=true\n"
						.getBytes());
			}
			return super.getResourceAsStream(name);
		}
		
		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (MP_CLASS_NAME.equals(name)) {
				InputStream in = parent2.getResourceAsStream(name.replace('.',
						'/')
						+ ".class");
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int c;
				try {
					while ((c = in.read()) != -1) {
						out.write(c);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] b = out.toByteArray();
				defineClass(MP_CLASS_NAME, b, 0, b.length);
			}
			return super.loadClass(name);
		}

	}

	private URLClassLoader getUrlClassLoader() throws MalformedURLException {
		String cp = System.getProperty("java.class.path");
		String[] entries = cp.split(":");
		List<URL> urls = new ArrayList<URL>();
		for (String s : entries) {
			URL u = new File(s).toURL();
			urls.add(u);
		}
		URLClassLoader urlCL = new URLClassLoader(urls.toArray(new URL[0]),
				null);
		return urlCL;
	}

	
	@Test
	public void testWithOwnClassLoader() throws ClassNotFoundException,
			MalformedURLException, InstantiationException,
			IllegalAccessException, SecurityException, NoSuchFieldException {
		ClassLoader cl = getUrlClassLoader();
		Class<?> loadClass = cl.loadClass(MP_CLASS_NAME);
		Field field2 = loadClass.getField("TRACE_BYTECODE");
		assertTrue(Boolean.FALSE.equals(field2.get(null)));
	}

	@Test
	public void testOverwriteProperyInFile() throws ClassNotFoundException,
			MalformedURLException, InstantiationException,
			IllegalAccessException, SecurityException, NoSuchFieldException {
		ClassLoader cl = new MyClassLoader(getUrlClassLoader());
		Class<?> loadClass = cl.loadClass(MP_CLASS_NAME);
		Field field2 = loadClass.getField("TRACE_BYTECODE");
		assertEquals(Boolean.TRUE, field2.get(null));
	}


}
