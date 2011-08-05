package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperator;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion.AbstractUnaryOperatorMethodAdapater.*;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperator.classes.Uo2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class UnaryOperator2Test extends BaseBytecodeTest {

	private Class<?> clazz;

	public UnaryOperator2Test() throws Exception {
		super(Uo2TEMPLATE.class);
		config.setMutationType(UNARY_OPERATOR, true);
		// verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		System.out.println(~1);
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(2, 0, m1, clazz);
		checkUnmutated(1, 2, m1, clazz);
		checkUnmutated(-1, 0, m1, clazz);

		List<Mutation> mutations = QueryManager.getMutations(className,
				UNARY_OPERATOR, 8);
		assertEquals(4, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			System.out.println(addInfo);
			if (addInfo.equals(MINUS)) {
				checkMutation(m, -1, 2, m1, clazz);
				checkMutation(m, 1, 0, m1, clazz);
			} else if (addInfo.equals(BITWISE_NEGATE)) {
				checkMutation(m, ~1, 2, m1, clazz);
				checkMutation(m, 1, 0, m1, clazz);
			} else {
				fail("Unexpected mutation " + m);
			}
		}
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 3, m2, clazz);
		checkUnmutated(1, 3, m2, clazz);
		checkUnmutated(-1, 0, m2, clazz);
		checkUnmutated(3, 0, m2, clazz);

		List<Mutation> mutations = QueryManager.getMutations(className,
				UNARY_OPERATOR, 17);
		assertEquals(8, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			System.out.println(addInfo);
			if (addInfo.equals(MINUS)) {
				if (m.getMutationForLine() < 2) {
					checkMutation(m, -1, 3, m2, clazz);
					checkMutation(m, 1, 0, m2, clazz);
				} else {
					checkMutation(m, -2, 3, m2, clazz);
					checkMutation(m, 2, 0, m2, clazz);
				}
			} else if (addInfo.equals(BITWISE_NEGATE)) {
				if (m.getMutationForLine() < 2) {
					checkMutation(m, ~1, 3, m2, clazz);
					checkMutation(m, 1, 0, m2, clazz);
				} else {
					checkMutation(m, ~2, 3, m2, clazz);
					checkMutation(m, 2, 0, m2, clazz);
				}
			} else {
				fail("Unexpected mutation " + m);
			}
		}
	}

}
