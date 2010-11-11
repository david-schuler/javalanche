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
package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.ds.util.io.Io;

/**
 * BYtecode related Utility class
 * 
 * @author David Schuler
 * 
 */
public class AsmUtil {

	private static Logger logger = Logger.getLogger(AsmUtil.class);

	/**
	 * Class should not be initialized.
	 */
	private AsmUtil() {
	}

	/**
	 * Returns a string representation of a class given as a byte array.
	 * 
	 * @param classBytes
	 *            the bytes of the class
	 * @return a string representation of the class
	 */
	public static String classToString(byte[] classBytes) {
		ClassReader cr = new ClassReader(classBytes);
		StringWriter sw = new StringWriter();
		TraceClassVisitor tv = new TraceClassVisitor(new PrintWriter(sw));
		cr.accept(tv, 0);
		return sw.toString();
	}

	public static String classToString(Class<?> clazz) {
		String className = clazz.getCanonicalName().replace('.', '/')
				+ ".class";
		return classNameToString(className);
	}

	private static String classNameToString(String className) {
		logger.info("Name of resource: " + className);
		// ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		ClassLoader classLoader = AsmUtil.class.getClassLoader();
		// ClassLoader classLoader = getClassLoaderFromClasspath();
		URL resource = classLoader.getResource(className);
		logger.info("Location of class file: " + resource);
		File f = new File(resource.getFile());
		try {
			byte[] bytesFromFile = Io.getBytesFromFile(f);
			return classToString(bytesFromFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ClassLoader getClassLoaderFromClasspath() {
		ClassLoader classLoader;
		String classPath = System.getProperty("java.class.path");
		String[] split = classPath.split(":");
		List<URL> urls = new ArrayList<URL>();
		for (String string : split) {
			try {
				URL url = new File(string).toURI().toURL();
				urls.add(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		classLoader = new URLClassLoader(urls.toArray(new URL[0]));
		return classLoader;
	}

	public static String classToString(String className) {
		if (!className.endsWith("class")) {
			className = className.replace('.', '/') + ".class";
		}
		return classNameToString(className);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		String className = System.getProperty("class.name");
		System.out.println("Getting bytecode for class: " + className);
		System.out.println(classToString(className));
	}

	public static String checkClass(byte[] transformedBytecode) {
		ClassReader cr = new ClassReader(transformedBytecode);
		StringWriter sw = new StringWriter();
		CheckClassAdapter check = new CheckClassAdapter(new ClassWriter(
				ClassWriter.COMPUTE_MAXS));
		// cr.accept(check, ClassReader.EXPAND_FRAMES);
		// original classes
		// cr.accept(check,0);
		// CheckClassAdapter.verify(cr, false, new PrintWriter(sw)); TODO
		// reports failure on
		return sw.toString();
	}

	public static void checkClass2(byte[] classfileBuffer) {
		ClassReader cr2 = new ClassReader(classfileBuffer);
		CheckClassAdapter check = new CheckClassAdapter(new ClassWriter(
				ClassWriter.COMPUTE_MAXS));
		cr2.accept(check, ClassReader.EXPAND_FRAMES);
	}
}
