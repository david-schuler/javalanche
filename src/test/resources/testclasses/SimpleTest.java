package testclasses;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleTest {

	public static final String ERROR_MESSAGE = "Error Message";

	@Test
	public void testPass() {
		assertEquals(1, 1);
	}
	@Test
	public void testFailure() {
		assertEquals(1, 2);
	}
	
	@Test
	public void testError() {
		throw new RuntimeException(ERROR_MESSAGE);
	}
}
