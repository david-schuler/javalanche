package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.classes.RemoveCallsTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.classes.ReplaceIntegerTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
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
		Mutation mutation = new Mutation(className, m1.getName(), 6, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m1, 2, 0);
		check(mutation, "6", m1, 2, 12);
		check(mutation, "4", m1, 2, 8);
	}

	private void check(Mutation mutation, String mutationVal, Method method,
			Object input, Object expectedResult) throws IllegalAccessException,
			InvocationTargetException, InstantiationException {
		mutation.setOperatorAddInfo(mutationVal);
		checkMutation(mutation, input, expectedResult, method, clazz);
	}

	private void check(Mutation mutation, String mutationVal, Method method,
			Object expectedResult) throws IllegalAccessException,
			InvocationTargetException, InstantiationException {
		mutation.setOperatorAddInfo(mutationVal);
		checkMutation(mutation, expectedResult, method, clazz);
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2");
		checkUnmutated(500l, m2, clazz);
		Mutation mutation = new Mutation(className, m2.getName(), 11, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m2, 0l);
		check(mutation, "101", m2, 505l);
		check(mutation, "99", m2, 495l);
		mutation = new Mutation(className, m2.getName(), 12, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m2, 0l);
		check(mutation, "6", m2, 600l);
		check(mutation, "4", m2, 400l);
	}

	@Test
	public void testM3() throws Exception {
		Method m3 = clazz.getMethod("m3", int.class);
		checkUnmutated(4, false, m3, clazz);
		checkUnmutated(5, true, m3, clazz);
		checkUnmutated(6, false, m3, clazz);
		Mutation mutation = new Mutation(className, m3.getName(), 17, 0,
				MutationType.REPLACE_CONSTANT);

		check(mutation, "0", m3, 5, false);
		check(mutation, "6", m3, 5, false);
		check(mutation, "4", m3, 5, false);

		check(mutation, "0", m3, 0, true);
		check(mutation, "6", m3, 6, true);
		check(mutation, "4", m3, 4, true);

	}
}
