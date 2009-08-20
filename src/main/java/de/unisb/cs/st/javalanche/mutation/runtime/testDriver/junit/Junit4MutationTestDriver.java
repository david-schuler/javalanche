package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.junit.internal.builders.JUnit4Builder;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestRunnable;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.SingleTestResult.TestOutcome;

public class Junit4MutationTestDriver extends MutationTestDriver {

	private static Logger logger = Logger
			.getLogger(Junit4MutationTestDriver.class);

	private Map<String, Description> allTests = new HashMap<String, Description>();

	public Junit4MutationTestDriver() {
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
				logger.warn(message);
				throw new RuntimeException(message, t);
			}
		}
		allTests = getTests(r);
		logger.info("All tests" + allTests);
	}

	private Runner getTestSuiteRunner() throws ClassNotFoundException,
			InitializationError {
		Class<?> forName = null;
		forName = Class.forName(MutationProperties.TEST_SUITE);
		Runner r = new Suite(forName, new JUnit4Builder());
		return r;
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

	private static BlockJUnit4ClassRunner getRunner(Description desc) {
		String className = getClassName(desc);
		BlockJUnit4ClassRunner tcr = null;
		try {
			Class<?> clazz;
			clazz = Class.forName(className);
			tcr = new BlockJUnit4ClassRunner(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InitializationError e) {
			e.printStackTrace();
		}
		return tcr;
	}

	private static String getClassName(Description desc) {
		String s = desc.toString();
		int start = s.indexOf('(');
		int end = s.lastIndexOf(')');
		return s.substring(start + 1, end);
	}

	private static void runTest(final Description desc, RunListener runListener) {
		try {
			BlockJUnit4ClassRunner r = getRunner(desc);
			r.filter(new Filter() {

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

		@Override
		public void testFailure(Failure failure) throws Exception {
			failures.add(failure);
		}

		public List<Failure> getFailures() {
			return failures;
		}

		public void addFailure(Description desc, Exception e) {
			failures.add(new Failure(desc, e));
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
				String message = "";// TODO
				TestOutcome outcome = TestOutcome.PASS;
				if (runListener.getFailures().size() > 0) {
					outcome = TestOutcome.FAIL;
				}
				// else if (result.errorCount() > 0) {
				// outcome = TestOutcome.ERROR; //TODO
				// }
				SingleTestResult res = new SingleTestResult(testName, message,
						outcome, stopWatch.getTime());
				return res;
			}

			public void setFailed(String message) {
				Exception e = new Exception(message);
				runListener.addFailure(allTests.get(testName), e);
			}

		};
		return r;
	}
}
