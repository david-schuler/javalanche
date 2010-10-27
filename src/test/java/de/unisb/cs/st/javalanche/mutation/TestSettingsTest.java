package de.unisb.cs.st.javalanche.mutation;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

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
		assertThat(JAVA_LANG_CLASS + " has to be on the boot classpath",
				resource, notNullValue());
	}

}
