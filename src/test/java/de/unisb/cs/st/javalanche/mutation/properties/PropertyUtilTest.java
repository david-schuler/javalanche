package de.unisb.cs.st.javalanche.mutation.properties;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyUtilTest {

	@Test
	public void testBooleanDefaultFalse() {
		boolean prop = PropertyUtil.getPropertyOrDefault(
				"non.exsisting.property", false);
		assertFalse(prop);
	}

	@Test
	public void testBooleanDefaultTrue() {
		boolean prop = PropertyUtil.getPropertyOrDefault(
				"non.exsisting.property", true);
		assertTrue(prop);
	}

	@Test
	public void testBooleanPropertySetTrue() {
		String propertyKey = "existing.property";
		System.setProperty(propertyKey, "true");
		boolean prop = PropertyUtil.getPropertyOrDefault(propertyKey, false);
		assertTrue(prop);
	}

	@Test
	public void testBooleanPropertySetFalse() {
		String propertyKey = "existing.property";
		System.setProperty(propertyKey, "false");
		boolean prop = PropertyUtil.getPropertyOrDefault(propertyKey, true);
		assertFalse(prop);
	}

	@Test
	public void testBooleanUseDefaultForWrongArg() {
		String propertyKey = "existing.property";
		System.setProperty(propertyKey, "asfasdas");
		boolean prop = PropertyUtil.getPropertyOrDefault(propertyKey, false);
		assertFalse(prop);
	}

	@Test
	public void testCheckProperty() {
		String key = "exsisting.property";
		System.setProperty(key, "asfasdas");
		PropertyUtil.checkProperty(key);
	}

	@Test
	public void testCheckPropertyFail() {
		String key = "non.exsisting.property";
		try {
			PropertyUtil.checkProperty(key);
			fail("Expected exception");
		} catch (IllegalStateException e) {
			String message = e.getMessage();
			assertThat(message, containsString((key)));
		}
	}
}
