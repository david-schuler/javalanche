package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.classes.ReplaceIntegerTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class ReplaceIntegerBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public ReplaceIntegerBytecodeTest() throws Exception {
		super(ReplaceIntegerTEMPLATE.class);
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(2, 10, m1, clazz);
		checkMutation(6, MutationType.RIC_ZERO, 0, 2, 0, m1, clazz);
		checkMutation(6, MutationType.RIC_PLUS_1, 0, 2, 12, m1, clazz);
		checkMutation(6, MutationType.RIC_MINUS_1, 0, 2, 8, m1, clazz);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2");
		checkUnmutated(500l, m2, clazz);
		checkMutation(11, MutationType.RIC_ZERO, 0, 0l, m2, clazz);
		checkMutation(11, MutationType.RIC_PLUS_1, 0, 505l, m2, clazz);
		checkMutation(11, MutationType.RIC_MINUS_1, 0, 495l, m2, clazz);
		checkMutation(12, MutationType.RIC_ZERO, 0, 0l, m2, clazz);
		checkMutation(12, MutationType.RIC_PLUS_1, 0, 600l, m2, clazz);
		checkMutation(12, MutationType.RIC_MINUS_1, 0, 400l, m2, clazz);
	}

	@Test
	public void testM3() throws Exception {
		Method m2 = clazz.getMethod("m3", int.class);
		checkUnmutated(4, false, m2, clazz);
		checkUnmutated(5, true, m2, clazz);
		checkUnmutated(6, false, m2, clazz);

		checkMutation(17, MutationType.RIC_ZERO, 0, 5, false, m2, clazz);
		checkMutation(17, MutationType.RIC_PLUS_1, 0, 5, false, m2, clazz);
		checkMutation(17, MutationType.RIC_MINUS_1, 0, 5, false, m2, clazz);

		checkMutation(17, MutationType.RIC_ZERO, 0, 0, true, m2, clazz);
		checkMutation(17, MutationType.RIC_PLUS_1, 0, 6, true, m2, clazz);
		checkMutation(17, MutationType.RIC_MINUS_1, 0, 4, true, m2, clazz);
	}
}
