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

package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.runners.SuiteMethod;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class Junit4Util {

	private static Logger logger = Logger.getLogger(Junit4Util.class);

	public static Runner getRunner() throws ClassNotFoundException,
			InitializationError {
		Class<?> forName = null;
		String testSuite = MutationProperties.TEST_SUITE;
		Runner r = null;
		if (MutationProperties.TEST_METHODS != null) {
			if (MutationProperties.TEST_METHODS.startsWith("file:")) {
				String fileName = MutationProperties.TEST_METHODS.substring(5);
				try {
					String testsFromFile = FileUtils.readFileToString(new File(
							fileName));
					r = getMethodsRunner(testsFromFile);
				} catch (IOException e) {
					throw new RuntimeException("Could not read file: "
							+ fileName, e);
				}
			} else {
				r = getMethodsRunner(testSuite);
			}
		} else if (testSuite.contains(":")) {
			r = getClassesRunner(testSuite);
		} else {
			logger.info("Getting test suite for name: " + testSuite);
			forName = Class.forName(testSuite);
			try {
				Method suite = getSuiteMethod(forName);
				if (suite != null) {
					r = new SuiteMethod(forName);
				} else {
					r = new AllDefaultPossibilitiesBuilder(true)
							.runnerForClass(forName);
				}
				if (r == null) {
					r = new Suite(forName, new AllDefaultPossibilitiesBuilder(
							true));
				}
			} catch (Throwable e) {
				// e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return r;
	}

	private static Method getSuiteMethod(Class<?> forName) {
		Method[] methods = forName.getMethods();
		for (Method method : methods) {
			if (method.getName().equals("suite")
					&& method.getParameterTypes().length == 0) {
				return method;
			}
		}
		return null;
	}

	private static Runner getClassesRunner(String testClasses)
			throws ClassNotFoundException, InitializationError {
		Runner r;
		String[] split = testClasses.split(":");
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : split) {
			try {
				Class<?> clazz = Class.forName(className);
				classes.add(clazz);
			} catch (ClassNotFoundException e) {
				if (className.trim().length() == 0) {
					throw new RuntimeException("Classname with length 0.", e);
				}
				throw new RuntimeException("Class not found: " + className, e);
			} catch (VerifyError e) {
				throw new RuntimeException("Verrify error for " + className, e);
			}
		}
		r = new Suite(new AllDefaultPossibilitiesBuilder(true),
				classes.toArray(new Class[0]));
		return r;
	}

	private static Runner getMethodsRunner(String testMethods)
			throws ClassNotFoundException, InitializationError {
		String[] testMethodsSplit = testMethods.split(":");
		final Multimap<String, String> methods = getMethodMap(testMethodsSplit);
		RunnerBuilder runnerBuilder = new RunnerBuilder() {
			@Override
			public Runner runnerForClass(Class<?> testClass) throws Throwable {
				Request aClass = Request.aClass(testClass);
				final Collection<String> methodNames = methods.get(testClass
						.getName());
				Request filtered = aClass.filterWith(new Filter() {

					@Override
					public String describe() {
						return "Javalanche test filter";
					}

					@Override
					public boolean shouldRun(Description description) {
						String name = description.getClassName() + "."
								+ description.getMethodName();
						logger.debug("Testname: " + name);
						boolean var = methodNames.contains(name);
						return var;
					}
				});
				return filtered.getRunner();
			}
		};
		Class<?>[] classes = getClasses(methods);
		return new Suite(runnerBuilder, classes);
	}

	private static Class<?>[] getClasses(final Multimap<String, String> methods)
			throws ClassNotFoundException {
		Set<String> keySet = methods.keySet();
		List<Class<?>> classes = new ArrayList<Class<?>>();

		for (String className : keySet) {
			classes.add(Class.forName(className));
		}
		return classes.toArray(new Class<?>[0]);
	}

	private static Multimap<String, String> getMethodMap(String[] testMethods) {
		final Multimap<String, String> methods = HashMultimap.create();
		for (String testMethod : testMethods) {
			if (!testMethod.trim().isEmpty()) {
				String testClass = getTestClass(testMethod);
				methods.put(testClass, testMethod);
			}
		}
		return methods;
	}

	static String getTestClass(String testMethod) {
		int lastIndexOf = testMethod.lastIndexOf('.');
		if (lastIndexOf < 0) {
			throw new RuntimeException("Did not find class name for test: "
					+ testMethod);
		}
		return testMethod.substring(0, lastIndexOf);
	}

}
