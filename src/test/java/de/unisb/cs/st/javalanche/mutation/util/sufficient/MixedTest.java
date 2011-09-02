package de.unisb.cs.st.javalanche.mutation.util.sufficient;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.AbstractArithmeticMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.AbstractNegateJumpsAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion.AbstractUnaryOperatorMethodAdapater;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AddOffutt96Sufficient;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.MixedTEMPLATE;

public class MixedTest extends BaseBytecodeTest {

	private Class<?> clazz;

	private static final int[] lineNumbers = { 13, 14, 15 };

	public MixedTest() throws Exception {
		super(MixedTEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Override
	public Class<?> prepareTest() throws Exception {
		JavalancheConfiguration configBack = ConfigurationLocator
				.getJavalancheConfiguration();

		ConfigurationLocator.setJavalancheConfiguration(config);
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		config.setMutationType(MutationType.UNARY_OPERATOR, true);
		config.setMutationType(MutationType.ABSOLUTE_VALUE, true);

		List<Mutation> mutations = QueryManager.getMutationsForClass(className);
		assertEquals(0, mutations.size());
		List<Mutation> pos = scan(classFile);
		List<Mutation> mts2 = QueryManager.getMutationsForClass(className);
		assertTrue(mts2.size() > 0);

		addCoverageData(lineNumbers);
		String prefix = config.getProjectPrefix();
		config.setProjectPrefix("de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.Mixed");
		AddOffutt96Sufficient.main(new String[0]);
		config.setProjectPrefix(prefix);
		redefineMutations(className, config);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		ConfigurationLocator.setJavalancheConfiguration(configBack);
		return clazz;
	}

	public void addCoverageData(int[] lineNumbers) {
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		List<Mutation> mutations = QueryManager.getMutationsForClass(className);
		for (Mutation mutation : mutations) {
			Long id = mutation.getId();
			coverageMap.put(id, new HashSet<String>(Arrays.asList("test1")));
		}
		MutationCoverageFile.saveCoverageData(coverageMap);
	}

	@Test
	public void testMutationsLine6() throws Exception {
		Method m1 = clazz.getMethod("m1", List.class, int.class);
		List<Mutation> mutations = QueryManager.getMutationsForClass(className);
		filterLine(mutations, 15);
		assertEquals(19, mutations.size());
		for (Mutation m : mutations) {

			MutationType type = m.getMutationType();
			if (type.equals(MutationType.NEGATE_JUMP)) {
				int addInfo = Integer.parseInt(m.getOperatorAddInfo());
				// Assuming that >= is compiled into IF_ICMPLT
				if (addInfo == Opcodes.IF_ICMPEQ) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (addInfo == Opcodes.IF_ICMPGE) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (addInfo == Opcodes.IF_ICMPGT) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (addInfo == Opcodes.IF_ICMPNE) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(2) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 2, m1, clazz);
				} else if (addInfo == Opcodes.IF_ICMPLE) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 1, m1, clazz);
				} else if (addInfo == AbstractNegateJumpsAdapter.POP_TWICE_TRUE) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 1, m1, clazz);
				} else if (addInfo == AbstractNegateJumpsAdapter.POP_TWICE_FALSE) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else {
					fail("Did not expect mutation " + m);
				}
			} else if (type.equals(MutationType.REPLACE_CONSTANT)) {
				int addInfo = Integer.parseInt(m.getOperatorAddInfo());
				if (addInfo == ~0) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, ~0, m1, clazz);
				} else if (addInfo == -0) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, -0, m1, clazz);
				} else {
					fail("Did not expect mutation " + m);
				}
			} else if (type == MutationType.ABSOLUTE_VALUE) {
				int addInfo = Integer.parseInt(m.getOperatorAddInfo());
				int mLine = m.getMutationForLine();
				if (mLine == 0 && addInfo == -1) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 1, m1, clazz);
				} else if (mLine == 0 && addInfo == 0) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(0) };
					checkUnmutated(input, 0, m1, clazz);
					try {
						checkMutation(m, input, 1, m1, clazz);
						fail("Expected Exception");
					} catch (InvocationTargetException e) {
						assertEquals("Variable is zero - Mutation detected", e
								.getCause().getMessage());
					}
				} else if (mLine == 0 && addInfo == 1) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (mLine == 1 && addInfo == -1) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (mLine == 1 && addInfo == 0) {
					Object[] input = new Object[] { Arrays.asList(),
							Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					try {
						checkMutation(m, input, 1, m1, clazz);
						fail("Expected Exception");
					} catch (InvocationTargetException e) {
						assertEquals("Variable is zero - Mutation detected", e
								.getCause().getMessage());
					}
				} else if (mLine == 1 && addInfo == 1) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(1) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else {
					fail("Did not expect mutation " + m);
				}

			} else if (type.equals(MutationType.UNARY_OPERATOR)) {
				String addInfo = m.getOperatorAddInfo();
				int mLine = m.getMutationForLine();
				if (mLine == 0
						&& addInfo
								.equals(AbstractUnaryOperatorMethodAdapater.MINUS)) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(7) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 7, m1, clazz);
				} else if (mLine == 0
						&& addInfo
								.equals(AbstractUnaryOperatorMethodAdapater.BITWISE_NEGATE)) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(7) };
					checkUnmutated(input, 0, m1, clazz);
					checkMutation(m, input, 7, m1, clazz);
				} else if (mLine == 1
						&& addInfo
								.equals(AbstractUnaryOperatorMethodAdapater.MINUS)) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else if (mLine == 1
						&& addInfo
								.equals(AbstractUnaryOperatorMethodAdapater.BITWISE_NEGATE)) {
					Object[] input = new Object[] {
							Arrays.asList(new Object()), Integer.valueOf(-1) };
					checkUnmutated(input, -1, m1, clazz);
					checkMutation(m, input, 0, m1, clazz);
				} else {
					fail("Did not expect mutation " + m);
				}
			} else {
				fail("Did not expect mutation " + m);
			}

		}
	}

	private void filterLine(List<Mutation> mutations, int i) {
		for (Iterator<Mutation> iterator = mutations.iterator(); iterator
				.hasNext();) {
			Mutation mutation = iterator.next();
			if (mutation.getLineNumber() != i) {
				iterator.remove();
			}
		}

	}

	// @Test
	// public void testMutationsLine11() throws Exception {
	// Method m2 = clazz.getMethod("m2", double.class);
	// List<Mutation> mutations = QueryManager.getMutations(className,
	// MutationType.REPLACE_CONSTANT, 11);
	// assertEquals(1, mutations.size());
	// for (Mutation mutation : mutations) {
	// double addInfo = Double.parseDouble(mutation.getOperatorAddInfo());
	// if (addInfo == -2d) {
	// Double[] input = new Double[] { 5d };
	// checkUnmutated(input, (Double) 10d, m2, clazz);
	// checkMutation(mutation, input, (Double) (-10d), m2, clazz);
	// } else {
	// fail("Did not expect mutation " + mutation);
	// }
	// }
	// }

}
