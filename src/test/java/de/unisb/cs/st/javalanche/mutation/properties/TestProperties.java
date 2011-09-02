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
package de.unisb.cs.st.javalanche.mutation.properties;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.mchange.util.AssertException;

public class TestProperties {

	public static final String ADVICE_CLASS_NAME = "org.aspectj.weaver.Advice";

	public static final TestClass ADVICE_CLASS = new TestClass(
			ADVICE_CLASS_NAME);

	public static final String LANG_UTIL_CLAZZ = "LangUtil.clazz";

	public static final String LANG_UTIL_CLASS_NAME = "org.aspectj.util.LangUtil";

	public static final TestClass LANG_UTIL_CLASS = new TestClass(
			LANG_UTIL_CLASS_NAME);

	public static final String SIMPLE_FUNCTION_CONTEXT_CLASS_NAME = "org.jaxen.SimpleFunctionContext";

	public static final TestClass SIMPLE_FUNCTION_CONTEXT_CLASS = new TestClass(
			SIMPLE_FUNCTION_CONTEXT_CLASS_NAME);

	public static class TestClass {

		private final String className;

		private byte[] classBytes;

		TestClass(String className) {
			this.className = className;
			int index = className.lastIndexOf('.');
			String simpleClassName = className.substring(index + 1);
			String clazz = simpleClassName + ".clazz";
			InputStream is = TestProperties.class.getClassLoader()
					.getResourceAsStream(clazz);
			assertNotNull("Expexted a stream for file: " + clazz, is);
			try {
				this.classBytes = IOUtils.toByteArray(is);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return "TestClass [className=" + className + ", classBytes.length="
					+ classBytes.length + "]";
		}

		public String getClassName() {
			return className;
		}

		public byte[] getClassBytes() {
			return classBytes;
		}

	}

}
