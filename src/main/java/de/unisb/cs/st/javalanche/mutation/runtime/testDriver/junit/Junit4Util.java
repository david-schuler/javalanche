package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
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

	public static Runner getRuner() throws ClassNotFoundException,
			InitializationError {
		Class<?> forName = null;
		String testSuite = MutationProperties.TEST_SUITE;
		System.out.println("Junit4MutationTestDriver.getTestSuiteRunner()");
		Runner r = null;
		if (MutationProperties.TEST_METHODS != null) {
			r = getMethodsRunner(MutationProperties.TEST_METHODS);
		} else if (MutationProperties.TEST_CLASSES != null) {
			r = getClassesRunner(MutationProperties.TEST_CLASSES);
		} else {
			logger.info("Getting test suite for name: " + testSuite);
			forName = Class.forName(testSuite);
			r = new Suite(forName, new AllDefaultPossibilitiesBuilder(false));
		}
		return r;
	}

	private static Runner getClassesRunner(String testClasses)
			throws ClassNotFoundException, InitializationError {
		Runner r;
		String[] split = testClasses.split(":");
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String className : split) {
			Class<?> clazz = Class.forName(className);
			classes.add(clazz);
		}
		r = new Suite(new AllDefaultPossibilitiesBuilder(false), classes
				.toArray(new Class[0]));
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
		final Multimap<String, String> methods = new HashMultimap<String, String>();
		for (String testMethod : testMethods) {
			String testClass = getTestClass(testMethod);
			methods.put(testClass, testMethod);
		}
		return methods;
	}

	static String getTestClass(String testMethod) {
		int lastIndexOf = testMethod.lastIndexOf('.');
		return testMethod.substring(0, lastIndexOf);
	}

}
