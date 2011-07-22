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
package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
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

	private Runner masterRunner;

	public Junit4MutationTestDriver() {
		masterRunner = null;
		Throwable t = null;
		try {
			masterRunner = Junit4Util.getRunner();
		} catch (ClassNotFoundException e) {
			t = e;
		} catch (InitializationError e) {
			t = e;
		} finally {
			if (t != null) {
				String message = "Could not initialize tests suite for:"
						+ ConfigurationLocator.getJavalancheConfiguration()
								.getTestNames();
				logger.warn(message, t);
				throw new RuntimeException(message, t);
			}
		}
		allTests = getTests(masterRunner);
		removeExludedTests();
	}

	private void removeExludedTests() {
		String excludes = ConfigurationLocator.getJavalancheConfiguration()
				.getExcludedTests();
		if (excludes != null) {
			List<String> excludeList = new ArrayList<String>();
			if (excludes.startsWith("file://")) {
				String fileName = excludes.substring(7);
				try {
					excludeList = FileUtils.readLines(new File(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				String[] split = excludes.split(":");
				excludeList = Arrays.asList(split);
			}
			for (String string : excludeList) {
				if (allTests.containsKey(string)) {
					allTests.remove(string);
				}
			}
		}

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
				String testName = getTestName(d);
				String insertTestName = testName;
				int count = 0;
				while (testMap.containsKey(insertTestName)) {
					count++;
					insertTestName = testName + "-instance-" + count;
				}
				logger.debug("Got test case: " + insertTestName + "Desc: " + d);
				testMap.put(insertTestName, d);
			}

		}
		return testMap;
	}

	private static String getTestName(Description d) {
		return d.getClassName() + "." + d.getMethodName();
	}

	@Override
	protected List<String> getAllTests() {
		return new ArrayList<String>(allTests.keySet());
	}

	private static Runner getRunner(Description desc, boolean useSuite) {
		String className = getClassName(desc);
		Runner tcr = null;
		try {
			Class<?> clazz;
			clazz = Class.forName(className);
			logger.info("Creating Runner for " + className);
			Class<? extends Runner> runWithRunner = getRunWithRunner(clazz,
					useSuite);
			Constructor<? extends Runner> constructor = runWithRunner
					.getConstructor(Class.class);
			tcr = constructor.newInstance(clazz);
			// logger.debug("Runner Type " + tcr.getClass());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			logger.warn("Invocation Exception ", e);
			throw new RuntimeException(e);
		}
		return tcr;
	}

	private static Class<? extends Runner> getRunWithRunner(Class<?> clazz,
			boolean useSuite) {
		RunWith runWithAnnotation = clazz.getAnnotation(RunWith.class);
		if (runWithAnnotation == null) {
			AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(
					useSuite);
			try {
				return builder.runnerForClass(clazz).getClass();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			// return BlockJUnit4ClassRunner.class;
		}
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

	private static void runTest(final Description desc,
			RunListener runListener, Runner masterRunner) {
		try {
			// Runner r = getRunner(desc, false);
			Runner r = Junit4Util.getRunner();
			logger.info("Got Tests: " + r.testCount());

			((Filterable) r).filter(new Filter() {

				@Override
				public String describe() {
					return "Javalanche single tests filter";
				}

				@Override
				public boolean shouldRun(Description description) {
					logger.debug("1" + description.toString());
					logger.debug("2" + description.getClassName() + " "
							+ description.getMethodName());
					logger.debug("3" + desc.toString());

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
			logger.warn("No test remain for test " + desc, e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InitializationError e) {
			throw new RuntimeException(e);
		}
	}


	private static class TestRunListener extends RunListener {

		List<Failure> failures = new ArrayList<Failure>();
		List<Failure> errors = new ArrayList<Failure>();

		String message = null;

		@Override
		public void testFailure(Failure failure) throws Exception {
			// if(message != null){
			// logger.warn("Message exists " + message);
			// }
			Throwable e = failure.getException();
			logger.debug("Adding failure: " + failure
					+ "Exception of failure: " + failure.getException());
			// Junit4 does distinguish between failures and errors. Thus, the
			// type of the exception is checked.
			if (e instanceof AssertionError) {
				failures.add(failure);
			} else {
				errors.add(failure);
			}
			if (failure != null) {
				message = failure.getMessage();
				logger.debug("Setting failure message: " + message);
				if (ConfigurationLocator.getJavalancheConfiguration()
						.storeTestMessages()) {
					message += "\n" + failure.getTrace();
				}
			}
		}

		public List<Failure> getFailures() {
			return failures;
		}

		public void addFailure(Description desc, Throwable t) {
			try {
				testFailure(new Failure(desc, t));
			} catch (Exception e) {
				throw new RuntimeException("Could not add test failure: " + e,
						e);
			}
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
				runTest(desc, runListener, masterRunner);
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

			public void setFailed(String message, Throwable t) {
				Exception e;
				if (t != null) {
					e = new Exception(message, t);
				} else {
					e = new Exception(message);
				}
				runListener.addFailure(allTests.get(testName), e);
			}

		};
		return r;
	}
}
