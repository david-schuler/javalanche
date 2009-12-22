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
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;

/**
 * Allows to use Junit4 Tests for Mutation Testing.
 * 
 * @author David Schuler
 * 
 */
public class Junit4MutationTestDriver extends MutationTestDriver {

	private static Logger logger = Logger
			.getLogger(Junit4MutationTestDriver.class);

	private Map<String, Description> allTests = new HashMap<String, Description>();

	public Junit4MutationTestDriver() {

		System.out
				.println("Junit4MutationTestDriver.Junit4MutationTestDriver()");
		Runner r = null;
		Throwable t = null;
		try {
			r = getTestSuiteRunner();
		} catch (ClassNotFoundException e) {
			t = e;
		} catch (InitializationError e) {
			t = e;
		} finally {
			if (t != null) {
				String message = "Could not initialize junit 4 test suite "
						+ MutationProperties.TEST_SUITE;
				logger.warn(message, t);
				throw new RuntimeException(message, t);
			}
		}
		allTests = getTests(r);
		logger.info("All tests" + allTests);
	}

	private Runner getTestSuiteRunner() throws ClassNotFoundException,
			InitializationError {
		return Junit4Util.getRuner();
		// Class<?> forName = null;
		// String testSuite = MutationProperties.TEST_SUITE;
		// System.out.println("Junit4MutationTestDriver.getTestSuiteRunner()");
		// Runner r = null;
		// String addProp = System.getProperty("javalanche.test.methods");
		// if (addProp != null) {
		// String[] split = addProp.split(":");
		// if (addProp != null) {
		// final Multimap<String, String> methods = new HashMultimap<String,
		// String>();
		//
		// for (String testMethod : split) {
		// String testClass = Junit4Util.getTestClass(testMethod);
		// methods.put(testClass, testMethod);
		// }
		// RunnerBuilder runnerBuilder = new RunnerBuilder() {
		// @Override
		// public Runner runnerForClass(Class<?> testClass)
		// throws Throwable {
		//
		// Request aClass = Request.aClass(testClass);
		// final Collection<String> methodNames = methods
		// .get(testClass.getName());
		// Request filtered = aClass.filterWith(new Filter() {
		//
		// @Override
		// public String describe() {
		// return "Javalanche test filter";
		// }
		//
		// @Override
		// public boolean shouldRun(Description description) {
		// String name = description.getClassName() + "."
		// + description.getMethodName();
		// boolean var = methodNames.contains(name);
		// return var;
		// }
		// });
		// return filtered.getRunner();
		// }
		// };
		// Set<String> keySet = methods.keySet();
		// List<Class<?>> classes = new ArrayList<Class<?>>();
		//
		// for (String className : keySet) {
		// classes.add(Class.forName(className));
		// }
		// r = new Suite(runnerBuilder, classes.toArray(new Class<?>[0]));
		// } else if (testSuite.startsWith("-c")) {
		// // Classes
		// List<Class<?>> classes = new ArrayList<Class<?>>();
		// for (String className : split) {
		// Class<?> clazz = Class.forName(className);
		// classes.add(clazz);
		// }
		// r = new Suite(new AllDefaultPossibilitiesBuilder(false),
		// classes.toArray(new Class[0]));
		// }
		// } else {
		// forName = Class.forName(testSuite);
		// r = new Suite(forName, new AllDefaultPossibilitiesBuilder(false));
		// }
		// return r;
	}

	private static Map<String, Description> getTests(Runner r) {
		Map<String, Description> testMap = new HashMap<String, Description>();
		List<Description> descs = new ArrayList<Description>();
		Description description = r.getDescription();
		logger.debug(description);
		descs.add(description);
		while (descs.size() > 0) {
			Description d = descs.remove(0);
			ArrayList<Description> children = d.getChildren();
			if (children != null && children.size() > 0) {
				descs.addAll(children);
			} else {
				logger.debug("Got test case: " + d);
				testMap.put(d.toString(), d);
			}

		}
		return testMap;
	}

	@Override
	protected List<String> getAllTests() {
		return new ArrayList<String>(allTests.keySet());
	}

	private static Runner getRunner(Description desc) {
		String className = getClassName(desc);
		Runner tcr = null;
		try {
			Class<?> clazz;
			clazz = Class.forName(className);
			Class<? extends Runner> runWithRunner = getRunWithRunner(clazz);
			Constructor<? extends Runner> constructor = runWithRunner
					.getConstructor(Class.class);
			tcr = constructor.newInstance(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return tcr;
	}

	private static Class<? extends Runner> getRunWithRunner(Class<?> clazz) {
		RunWith runWithAnnotation = clazz.getAnnotation(RunWith.class);
		if (runWithAnnotation == null)
			return BlockJUnit4ClassRunner.class;
		Class<? extends Runner> runner = runWithAnnotation.value();
		if (!runnerImplementsFilterable(runner))
			return BlockJUnit4ClassRunner.class;
		return runner;
	}

	private static boolean runnerImplementsFilterable(
			Class<? extends Runner> runner) {
		for (Class<?> interfaze : runner.getInterfaces()) {
			if (Filterable.class.equals(interfaze))
				return true;
		}
		return false;
	}

	private static String getClassName(Description desc) {
		String s = desc.toString();
		int start = s.indexOf('(');
		int end = s.lastIndexOf(')');
		return s.substring(start + 1, end);
	}

	private static void runTest(final Description desc, RunListener runListener) {
		try {
			Runner r = getRunner(desc);
			((Filterable) r).filter(new Filter() {

				@Override
				public String describe() {
					return "Javalanche single tests filter";
				}

				@Override
				public boolean shouldRun(Description description) {
					if (description.toString().equals(desc.toString())) {
						return true;
					}
					return false;
				}

			});
			RunNotifier notifier = new RunNotifier();
			notifier.addListener(runListener);
			r.run(notifier);
		} catch (NoTestsRemainException e) {
			e.printStackTrace();
			logger.warn(e);
		}
	}

	private static class TestRunListener extends RunListener {

		List<Failure> failures = new ArrayList<Failure>();
		List<Failure> errors = new ArrayList<Failure>();

		String message;

		@Override
		public void testFailure(Failure failure) throws Exception {
			Throwable e = failure.getException();
			// Junit4 does distinguish between failures and errors. Thus, the
			// type of the exception is checked.
			if (e instanceof AssertionError) {
				failures.add(failure);
			} else {
				errors.add(failure);
			}
			if (failure != null) {
				message = failure.getMessage();
				if (!MutationProperties.IGNORE_EXCEPTION_TRACES) {
					message += "\n" + failure.getTrace();
				}
			}
		}

		public List<Failure> getFailures() {
			return failures;
		}

		public void addFailure(Description desc, Exception e) throws Exception {
			testFailure(new Failure(desc, e));
		}

		public List<Failure> getErrors() {
			return errors;
		}

		public String getMessage() {
			return message;
		}

	}

	@Override
	protected MutationTestRunnable getTestRunnable(final String testName) {

		MutationTestRunnable r = new MutationTestRunnable() {

			boolean finished = false;

			TestRunListener runListener = new TestRunListener();

			private StopWatch stopWatch = new StopWatch();

			public void run() {
				// try {
				// stopWatch.start();
				final Description desc = allTests.get(testName);
				logger.debug("Start running " + desc);
				runTest(desc, runListener);
				logger.debug("Run finished " + desc);
				setFinished();
			}

			private synchronized void setFinished() {
				finished = true;
			}

			public synchronized boolean hasFinished() {
				return finished;
			}

			public SingleTestResult getResult() {
				String message = runListener.getMessage();
				TestOutcome outcome = TestOutcome.PASS;
				if (runListener.getFailures().size() > 0) {
					outcome = TestOutcome.FAIL;
				} else if (runListener.getErrors().size() > 0) {
					outcome = TestOutcome.ERROR;
				}
				SingleTestResult res = new SingleTestResult(testName, message,
						outcome, stopWatch.getTime());
				return res;
			}

			public void setFailed(String message) {
				Exception e = new Exception(message);
				try {
					runListener.addFailure(allTests.get(testName), e);
				} catch (Exception e2) {
					logger.warn(e);
					e2.printStackTrace();
				}
			}

		};
		return r;
	}
}
