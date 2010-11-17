package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import static org.objectweb.asm.Opcodes.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.classes.ArithmeticTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class ReplaceMultipleTest extends BaseBytecodeTest {

	private static List<Integer> lineNumbers = Arrays.asList(27, 31, 35, 45,
			49, 53, 57, 61);
	private Class<?> clazz;

	public ReplaceMultipleTest() throws Exception {
		super(ArithmeticTEMPLATE.class);
		clazz = prepareTest();
	}

	@Override
	protected List<Mutation> scan(File classFile) throws IOException {

		List<Mutation> superResult = super.scan(classFile);
		List<Mutation> result = new ArrayList<Mutation>(superResult);

		for (Mutation mutation : superResult) {
			if (lineNumbers.contains(mutation.getLineNumber())
					&& mutation.getMutationType() == MutationType.ARITHMETIC_REPLACE
					&& mutation.getBaseMutationId() == null) {
				List<Mutation> additionalMutations = insertAdditionalMutations(mutation);
				result.addAll(additionalMutations);
			}
		}
		return result;

	}

	private static final int[] integerOpcodes = new int[] { IADD, ISUB, IMUL,
			IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR };

	private static final int[] longOpcodes = new int[] { LADD, LSUB, LMUL,
			LDIV, LREM, LAND, LOR, LXOR };

	private static final int[] longShiftOpcodes = new int[] { LSHL, LSHR, LUSHR };
	private static final int[] floatOpcodes = new int[] { FADD, FSUB, FMUL,
			FDIV, FREM };

	private static final int[] doubleOpcodes = new int[] { DADD, DSUB, DMUL,
			DDIV, DREM };

	private List<Mutation> insertAdditionalMutations(Mutation m) {
		List<Mutation> result = new ArrayList<Mutation>();
		String operatorAddInfo = m.getOperatorAddInfo();
		int operator = Integer.parseInt(operatorAddInfo);
		int[] replaceOperators = getReplaceOperators(operator);
		for (int op : replaceOperators) {
			Mutation m2 = new Mutation(m.getClassName(), m.getMethodName(),
					m.getLineNumber(), m.getMutationForLine(),
					m.getMutationType());
			m2.setOperatorAddInfo(op + "");
			QueryManager.saveMutation(m2);
			result.add(m2);
		}
		return result;
	}

	private int[] getReplaceOperators(int operator) {
		if (ArrayUtils.contains(integerOpcodes, operator)) {
			return integerOpcodes;
		}
		if (ArrayUtils.contains(longOpcodes, operator)) {
			return longOpcodes;
		}
		if (ArrayUtils.contains(longShiftOpcodes, operator)) {
			return longShiftOpcodes;
		}

		if (ArrayUtils.contains(floatOpcodes, operator)) {
			return floatOpcodes;
		}
		if (ArrayUtils.contains(doubleOpcodes, operator)) {
			return doubleOpcodes;
		}
		return new int[0];
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("m1", int.class);
		checkUnmutated(2, 4, m1, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), m1.getName(), 27,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(ISUB + "");
		checkMutation(m, 2, 0, m1, clazz);
		m.setOperatorAddInfo(IADD + "");
		checkMutation(m, 2, 4, m1, clazz);
		m.setOperatorAddInfo(IMUL + "");
		checkMutation(m, 3, 9, m1, clazz);
		m.setOperatorAddInfo(IDIV + "");
		checkMutation(m, 3, 1, m1, clazz);
		m.setOperatorAddInfo(IREM + "");
		checkMutation(m, 3, 0, m1, clazz);
		m.setOperatorAddInfo(ISHL + "");
		checkMutation(m, 1, 2, m1, clazz);

		m.setOperatorAddInfo(ISHR + "");
		checkMutation(m, 3, 0, m1, clazz);

		m.setOperatorAddInfo(IUSHR + "");
		checkMutation(m, 3, 0, m1, clazz);

		m.setOperatorAddInfo(IAND + "");
		checkMutation(m, 3, 3, m1, clazz);

		m.setOperatorAddInfo(IOR + "");
		checkMutation(m, 3, 3, m1, clazz);

		m.setOperatorAddInfo(IXOR + "");
		checkMutation(m, 3, 0, m1, clazz);

	}

	@Test
	public void testM2() throws Exception {
		Method me = clazz.getMethod("m2", int.class);
		checkUnmutated(2, 0, me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 31,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(ISUB + "");
		checkMutation(m, 2, 0, me, clazz);
		m.setOperatorAddInfo(IADD + "");
		checkMutation(m, 2, 4, me, clazz);
		m.setOperatorAddInfo(IMUL + "");
		checkMutation(m, 3, 9, me, clazz);
		m.setOperatorAddInfo(IDIV + "");
		checkMutation(m, 3, 1, me, clazz);
		m.setOperatorAddInfo(IREM + "");
		checkMutation(m, 3, 0, me, clazz);
	}

	@Test
	public void testM3() throws Exception {
		Method me = clazz.getMethod("m3", int.class);
		checkUnmutated(2, -2, me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 35,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(ISUB + "");
		checkMutation(m, 2, 3, me, clazz);
		m.setOperatorAddInfo(IADD + "");
		checkMutation(m, 2, 1, me, clazz);
		m.setOperatorAddInfo(IMUL + "");
		checkMutation(m, 3, -3, me, clazz);
		m.setOperatorAddInfo(IDIV + "");
		checkMutation(m, 3, -3, me, clazz);
		m.setOperatorAddInfo(IREM + "");
		checkMutation(m, 3, 0, me, clazz);
	}

	@Test
	public void testM6() throws Exception {
		Method me = clazz.getMethod("m6", double.class);
		checkUnmutated(2., 4., me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 49,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(DSUB + "");
		checkMutation(m, 2., 0., me, clazz);
		m.setOperatorAddInfo(DADD + "");
		checkMutation(m, 2., 4., me, clazz);
		m.setOperatorAddInfo(DMUL + "");
		checkMutation(m, 3., 9., me, clazz);
		m.setOperatorAddInfo(DDIV + "");
		checkMutation(m, 3., 1., me, clazz);
		m.setOperatorAddInfo(DREM + "");
		checkMutation(m, 3., 0., me, clazz);
	}

	@Test
	public void testM7() throws Exception {
		Method me = clazz.getMethod("m7", float.class);
		checkUnmutated(2.f, 4.f, me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 53,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(FSUB + "");
		checkMutation(m, 2.f, 0.f, me, clazz);
		m.setOperatorAddInfo(FADD + "");
		checkMutation(m, 2.f, 4.f, me, clazz);
		m.setOperatorAddInfo(FMUL + "");
		checkMutation(m, 3.f, 9.f, me, clazz);
		m.setOperatorAddInfo(FDIV + "");
		checkMutation(m, 3.f, 1.f, me, clazz);
		m.setOperatorAddInfo(FREM + "");
		checkMutation(m, 3.f, 0.f, me, clazz);
	}

	@Test
	public void testM8() throws Exception {
		Method me = clazz.getMethod("m8", long.class);
		checkUnmutated(2l, 4l, me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 57,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(LSUB + "");
		checkMutation(m, 2l, 0l, me, clazz);
		m.setOperatorAddInfo(LADD + "");
		checkMutation(m, 2l, 4l, me, clazz);
		m.setOperatorAddInfo(LMUL + "");
		checkMutation(m, 3l, 9l, me, clazz);
		m.setOperatorAddInfo(LDIV + "");
		checkMutation(m, 3l, 1l, me, clazz);
		m.setOperatorAddInfo(LREM + "");
		checkMutation(m, 3l, 0l, me, clazz);
	}

	@Test
	public void testM9() throws Exception {
		Method me = clazz.getMethod("m9", long.class);
		checkUnmutated(2l, 0l, me, clazz);
		Mutation m = new Mutation(clazz.getCanonicalName(), me.getName(), 61,
				0, MutationType.ARITHMETIC_REPLACE);
		m.setOperatorAddInfo(LSHL + "");
		checkMutation(m, 1l, 4l, me, clazz);
		m.setOperatorAddInfo(LSHR + "");
		checkMutation(m, 4l, 1l, me, clazz);
		m.setOperatorAddInfo(LUSHR + "");
		checkMutation(m, 4l, 1l, me, clazz);
	}

}
