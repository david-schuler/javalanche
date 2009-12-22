package de.unisb.cs.st.javalanche.mutation.runtime.testDriver.junit;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
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
		getRunner().run(notifier);
	}
}
