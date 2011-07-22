package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValue.classes.AbsoluteValueTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues.AbstractAbsoluteValueAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;

public class AbsoluteValueTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public AbsoluteValueTest() throws Exception {
		super(AbsoluteValueTEMPLATE.class);
		config.setMutationType(ABSOLUTE_VALUE, true);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(1, true, m1, clazz);
		checkUnmutated(-21, false, m1, clazz);
		List<Mutation> mutations = QueryManager.getMutations(className,
				ABSOLUTE_VALUE, 7);
		assertEquals(3, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE)) {
				checkMutation(m, -1, true, m1, clazz);
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE_NEGATIVE)) {
				checkMutation(m, 1, false, m1, clazz);
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.FAIL_ON_ZERO)) {
				try {
					checkMutation(m, 0, false, m1, clazz);
					fail();
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					String message = cause.getMessage();
					assertThat(message,
							startsWith("Variable is zero - Mutation detected"));
				}
			}
		}
	}

	@Test
	public void testM2() throws Exception {
		Method m2 = clazz.getMethod("m2", double.class);
		checkUnmutated(2.2, 4.4, m2, clazz);
		checkUnmutated(-2.2, -4.4, m2, clazz);
		List<Mutation> mutations = QueryManager.getMutations(className,
				ABSOLUTE_VALUE, 11);
		assertEquals(3, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE)) {
				checkMutation(m, -2.2, 4.4, m2, clazz);
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE_NEGATIVE)) {
				checkMutation(m, 2.2, -4.4, m2, clazz);
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.FAIL_ON_ZERO)) {
				try {
					checkMutation(m, 0, 4.4, m2, clazz);
					fail();
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					String message = cause.getMessage();
					assertThat(message,
							startsWith("Variable is zero - Mutation detected"));
				}
			}
		}
	}

	@Test
	public void testM3() throws Exception {
		Method m3 = clazz.getMethod("m3", long.class);
		checkUnmutated(2l, -4l, m3, clazz);
		checkUnmutated(-2l, 4l, m3, clazz);
		List<Mutation> mutations = QueryManager.getMutations(className,
				ABSOLUTE_VALUE, 17);
		assertEquals(6, mutations.size());
		for (Mutation m : mutations) {
			String addInfo = m.getOperatorAddInfo();
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE)) {
				if (m.getMutationForLine() == 0) {
					checkMutation(m, 2l, 4l, m3, clazz);
				}
				if (m.getMutationForLine() == 1) {
					checkMutation(m, -2l, -4l, m3, clazz);
				}
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.ABSOLUTE_NEGATIVE)) {
				if (m.getMutationForLine() == 0) {
					checkMutation(m, 2l, -4l, m3, clazz);
				}
				if (m.getMutationForLine() == 1) {
					checkMutation(m, 2l, 4l, m3, clazz);
				}
			}
			if (addInfo.equals(AbstractAbsoluteValueAdapter.FAIL_ON_ZERO)) {
				if (m.getMutationForLine() == 0) {
					checkMutation(m, 1l, -2l, m3, clazz);
				}
				if (m.getMutationForLine() == 1) {
					try {
						checkMutation(m, 0, 4.4, m3, clazz);
						fail();
					} catch (InvocationTargetException e) {
						Throwable cause = e.getCause();
						String message = cause.getMessage();
						assertThat(
								message,
								startsWith("Variable is zero - Mutation detected"));
					}
				}
			}
		}
	}
}
