package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import java.util.ArrayList;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.classes.ReplaceConstantTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class ReplaceConstantTest extends BaseBytecodeTest {

	private Class<?> clazz;

	private List<Mutation> scan;

	private static List<Integer> lineNumbers = Arrays.asList(6, 11, 16, 21, 26);

	public ReplaceConstantTest() throws Exception {
		super(ReplaceConstantTEMPLATE.class);
		clazz = prepareTest();
	}

	@Override
	protected List<Mutation> scan(File classFile) throws IOException {

		List<Mutation> superResult = super.scan(classFile);
		List<Mutation> result = new ArrayList<Mutation>(superResult);

		for (Mutation mutation : superResult) {
			if (lineNumbers.contains(mutation.getLineNumber())
					&& mutation.getMutationType() == MutationType.REPLACE_CONSTANT
					&& mutation.getBaseMutationId() == null) {
				List<Mutation> additionalMutations = insertAdditionalMutations(mutation);
				result.addAll(additionalMutations);
			}
		}
		return result;

	}

	private List<Mutation> insertAdditionalMutations(Mutation m) {
		List<Mutation> result = new ArrayList<Mutation>();
		for (int i = 10; i < 16; i++) {
			Mutation m2 = Mutation.copyMutation(m);
			m2.setAddInfo("Generated replace mutation. Value: " + i);
			m2.setOperatorAddInfo(i + "");
			m2.setBaseMutationId(m.getId());
			QueryManager.saveMutation(m2);
			result.add(m2);
		}
		return result;
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
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(2, 10, m1, clazz);
		Mutation mutation = new Mutation(className, m1.getName(), 6, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m1, 2, 0);
		check(mutation, "6", m1, 2, 12);
		check(mutation, "10", m1, 2, 20);
		check(mutation, "11", m1, 2, 22);
		check(mutation, "12", m1, 2, 24);
		check(mutation, "13", m1, 2, 26);
		check(mutation, "14", m1, 2, 28);
		check(mutation, "15", m1, 2, 30);
	}

	@Test
	public void testM2() throws Exception {
		Method m = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 10l, m, clazz);
		Mutation mutation = new Mutation(className, m.getName(), 11, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m, 2, 0l);
		check(mutation, "6", m, 2, 12l);
		check(mutation, "10", m, 2, 20l);
		check(mutation, "11", m, 2, 22l);
		check(mutation, "12", m, 2, 24l);
		check(mutation, "13", m, 2, 26l);
		check(mutation, "14", m, 2, 28l);
		check(mutation, "15", m, 2, 30l);
	}

	@Test
	public void testM3() throws Exception {
		Method m = clazz.getMethod("m3", int.class);
		checkUnmutated(2, 10., m, clazz);
		Mutation mutation = new Mutation(className, m.getName(), 16, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m, 2, 0.);
		check(mutation, "4.0", m, 2, 8.);
		check(mutation, "6.0", m, 2, 12.);
		check(mutation, "10", m, 2, 20.);
		check(mutation, "11", m, 2, 22.);
		check(mutation, "12", m, 2, 24.);
		check(mutation, "13", m, 2, 26.);
		check(mutation, "14", m, 2, 28.);
		check(mutation, "15", m, 2, 30.);
	}

	@Test
	public void testM4() throws Exception {
		Method m = clazz.getMethod("m4", int.class);
		checkUnmutated(2, 10.f, m, clazz);
		Mutation mutation = new Mutation(className, m.getName(), 21, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m, 2, 0.f);
		check(mutation, "4.0", m, 2, 8.f);
		check(mutation, "6.0", m, 2, 12.f);
		check(mutation, "10", m, 2, 20.f);
		check(mutation, "11", m, 2, 22.f);
		check(mutation, "12", m, 2, 24.f);
		check(mutation, "13", m, 2, 26.f);
		check(mutation, "14", m, 2, 28.f);
		check(mutation, "15", m, 2, 30.f);
	}

	@Test
	public void testM5() throws Exception {
		Method m = clazz.getMethod("m5", int.class);
		checkUnmutated(5, false, m, clazz);
		checkUnmutated(6, true, m, clazz);
		Mutation mutation = new Mutation(className, m.getName(), 26, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m, 6, false);
		check(mutation, "6", m, 5, true);
		check(mutation, "10", m, 5, true);
		check(mutation, "11", m, 5, true);
		check(mutation, "12", m, 5, true);
		check(mutation, "13", m, 5, true);
		check(mutation, "14", m, 5, true);
		check(mutation, "15", m, 5, true);
	}

	@Test
	public void testM6() throws Exception {
		Method m6 = clazz.getMethod("m6", double.class);
		checkUnmutated(2.1, 4.3, m6, clazz);
		Mutation mutation = new Mutation(className, m6.getName(), 31, 0,
				MutationType.REPLACE_CONSTANT);
		check(mutation, "0", m6, 2.1, 2.1);
		check(mutation, 2.2 - 1. + "", m6, 2.1, 3.3);
		check(mutation, 2.2 + 1. + "", m6, 2.1, 5.3);
	}
}
