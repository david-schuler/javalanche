package de.unisb.cs.st.javalanche.mutation.util.sufficient;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.AbstractArithmeticMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AddOffutt96Sufficient;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.DeleteTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.util.sufficient.classes.UoiTEMPLATE;

public class DeleteTest extends BaseBytecodeTest {

	private Class<?> clazz;

	private static final int[] lineNumbers = { 6, 11, 16 };

	public DeleteTest() throws Exception {
		super(DeleteTEMPLATE.class);
		config.setMutationType(UNARY_OPERATOR, true);
		config.setMutationType(ABSOLUTE_VALUE, true);
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
		List<Mutation> pos = scan(classFile);

		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.REMOVE_CALL, 6);
		assertEquals(1, mutations.size());
		List<Mutation> mutations11 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 11);
		assertEquals(1, mutations11.size());

		String prefix = config.getProjectPrefix();
		config.setProjectPrefix(className);
		AddOffutt96Sufficient.generateOffutt96Sufficient();

		config.setProjectPrefix(prefix);
		redefineMutations(className, config);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		ConfigurationLocator.setJavalancheConfiguration(configBack);
		return clazz;
	}

	public void addCoverageData(int[] lineNumbers) {
		Map<Long, Set<String>> coverageMap = new HashMap<Long, Set<String>>();
		for (int line : lineNumbers) {
			List<Mutation> m = QueryManager.getMutations(className,
					MutationType.REPLACE_CONSTANT, line);
			Long id = m.get(0).getId();
			coverageMap.put(id, new HashSet<String>(Arrays.asList("test1")));
		}
		MutationCoverageFile.saveCoverageData(coverageMap);
	}

	@Test
	public void testMutationsLine6() throws Exception {
		List<Mutation> mutations = QueryManager.getMutations(className,
				MutationType.REMOVE_CALL, 6);
		assertEquals(0, mutations.size());
	}

	@Test
	public void testMutationsLine11() throws Exception {
		List<Mutation> mutations11 = QueryManager.getMutations(className,
				MutationType.ARITHMETIC_REPLACE, 11);
		assertEquals(0, mutations11.size());
	}


}