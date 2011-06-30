package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperator;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion.AbstractUnaryOperatorMethodAdapater.*;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperator.classes.UnaryOperatorTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class UnaryOperatorTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public UnaryOperatorTest() throws Exception {
		super(UnaryOperatorTEMPLATE.class);
		config.setMutationType(UNARY_OPERATOR, true);
		// verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(1, 1, m1, clazz);
		checkUnmutated(-21, -21, m1, clazz);
		List<Mutation> mutations = QueryManager.getMutations(className,
				UNARY_OPERATOR, 6);
		assertEquals(2, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			if (addInfo.equals(MINUS)) {
				checkMutation(m, 1, -1, m1, clazz);
			} else if (addInfo.equals(BITWISE_NEGATE)) {
				checkMutation(m, 1, ~1, m1, clazz);
			} else {
				fail("Unexpected mutation " + m);
			}
		}
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", long.class);
		checkUnmutated(1l, 1l, m2, clazz);
		checkUnmutated(-21l, -21l, m2, clazz);
		List<Mutation> mutations = QueryManager.getMutations(className,
				UNARY_OPERATOR, 10);
		assertEquals(2, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			if (addInfo.equals(MINUS)) {
				checkMutation(m, 1l, -1l, m2, clazz);
			} else if (addInfo.equals(BITWISE_NEGATE)) {
				checkMutation(m, ~1l, 1l, m2, clazz);
			} else {
				fail("Unexpected mutation " + m);
			}
		}
	}


}
