package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import java.lang.adabu2.Tracer;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;

@RunWith(PlaceholderTestSuite.class)
public class PlaceholderTestSuite extends Runner {

	private Runner r;

	public PlaceholderTestSuite(Class<?> c) {
		if (MutationProperties.TEST_CLASSES == null
				&& MutationProperties.TEST_METHODS == null) {
			throw new IllegalStateException(
					String
							.format(
									"Either property %s or %s has to be set when running this test suite",
									TEST_CLASSES_KEY, TEST_METHODS_KEY));
		}
	}

	@Test
	public void testMethod() {
		System.out.println("PlaceholderTestSuite.testMethod()");
	}

	@Override
	public Description getDescription() {
		return getRunner().getDescription();
	}

	private Runner getRunner() {
		if (r == null) {
			try {
				r = Junit4Util.getRuner();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InitializationError e) {
				e.printStackTrace();
			}
		}
		return r;
	}

	@Override
	public void run(RunNotifier notifier) {
		Runner runner = getRunner();
		addTraceListener(notifier);
		runner.run(notifier);
	}

	private void addTraceListener(RunNotifier notifier) {
		String property = System.getProperty("javalanche.trace");
		if (property != null && property.equals("true")) {
			notifier.addListener(new RunListener() {
				@Override
				public void testStarted(Description description)
						throws Exception {
					Tracer.setNewMethod(true);
					super.testStarted(description);
					System.out
							.println("PlaceholderTestSuite.run(...).new RunListener() {...}.testStarted()  Running "
									+ description);
				}

				@Override
				public void testRunStarted(Description description)
						throws Exception {
					System.out
							.println("PlaceholderTestSuite.run(...).new RunListener() {...}.testRunStarted()");
					super.testRunStarted(description);
				}

				@Override
				public void testRunFinished(Result result) throws Exception {
					System.out
							.println("PlaceholderTestSuite.run(...).new RunListener() {...}.testRunFinished()");
					Tracer.setTestEnd(true);
					super.testRunFinished(result);
				}
			});
		}
	}
}
