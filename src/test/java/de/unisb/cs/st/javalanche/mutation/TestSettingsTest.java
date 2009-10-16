package de.unisb.cs.st.javalanche.mutation;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.RunMode;

/**
 * 
 * This test checks if the tests were invoked with the right arguments.
 * 
 * @author David Schuler
 * 
 */
public class TestSettingsTest {

	private static final String JAVA_LANG_CLASS = "java/lang/CoverageDataRuntime.class";

	@Test
	public void testJavaLangClass() {
		ClassLoader rootCl = ClassLoader.getSystemClassLoader();
		while (rootCl.getParent() != null) {
			rootCl = rootCl.getParent();
		}

		URL resource = rootCl.getResource(JAVA_LANG_CLASS);
		assertThat(JAVA_LANG_CLASS + " has to be on th boot classpath",
				resource, notNullValue());
	}

	@Test
	public void testJavaagent() {
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();
		assertThat(
				"Test have to be run with the javalanche javaagent. JVM arg: -javaagent:./target/javaagent.jar",
				arguments, hasItem(containsString("javaagent")));
	}

	@Test
	public void testRunMode() {
		assertThat(MutationProperties.RUN_MODE, is(RunMode.MUTATION_TEST));
	}
}
