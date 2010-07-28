package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.classes.JumpsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class NegateJumpsBytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public NegateJumpsBytecodeTest() throws Exception {
		super(JumpsTEMPLATE.class);
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(5, 1, m1, clazz);
		checkUnmutated(0, -1, m1, clazz);
		checkMutation(24, MutationType.NEGATE_JUMP, 0, 5, -1, m1, clazz);
		checkMutation(24, MutationType.NEGATE_JUMP, 0, -5, 1, m1, clazz);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 1, m2, clazz);
		checkUnmutated(0, 0, m2, clazz);
		checkUnmutated(-2, -1, m2, clazz);
		checkMutation(32, MutationType.NEGATE_JUMP, 0, 2, -1, m2, clazz);
		checkMutation(32, MutationType.NEGATE_JUMP, 0, -2, 1, m2, clazz);
		checkMutation(34, MutationType.NEGATE_JUMP, 0, 0, -1, m2, clazz);
		checkMutation(34, MutationType.NEGATE_JUMP, 0, -2, 0, m2, clazz);
	}

	@Test
	public void testM3() throws Exception {
		Method m3 = clazz.getMethod("m3", int.class);
		checkUnmutated(7, -7, m3, clazz);
		checkUnmutated(-7, 7, m3, clazz);
		checkMutation(43, MutationType.NEGATE_JUMP, 0, -7, -7, m3, clazz);
		checkMutation(43, MutationType.NEGATE_JUMP, 0, 7, 7, m3, clazz);
	}

	@Test
	public void testM4() throws Exception {
		Method m4 = clazz.getMethod("m4", int.class);
		checkUnmutated(-1, false, m4, clazz);
		checkUnmutated(2, true, m4, clazz);
		checkMutation(52, MutationType.NEGATE_JUMP, 0, 2, false, m4, clazz);
		checkMutation(52, MutationType.NEGATE_JUMP, 0, -1, true, m4, clazz);
		checkMutation(55, MutationType.NEGATE_JUMP, 0, 2, false, m4, clazz);
		checkMutation(55, MutationType.NEGATE_JUMP, 0, -1, true, m4, clazz);
	}

}
