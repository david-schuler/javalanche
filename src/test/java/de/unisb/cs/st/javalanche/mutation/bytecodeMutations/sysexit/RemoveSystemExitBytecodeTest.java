package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.Matchers;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes.SystemExitTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class RemoveSystemExitBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public RemoveSystemExitBytecodeTest() throws Exception {
		super(SystemExitTEMPLATE.class);
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1");
		try {
			checkUnmutated(3, m1, clazz);
			fail("Expected exception");
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			assertThat(cause.getMessage(), Matchers
					.containsString("Replaced System.exit()"));
		}
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2");

		try {
			checkUnmutated(3, m2, clazz);
			fail("Expected exception");
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			assertThat(cause.getMessage(), Matchers
					.containsString("Replaced System.exit()"));
		}
		checkMutation(17, MutationType.NEGATE_JUMP, 0, 8, m2, clazz);

	}
}
