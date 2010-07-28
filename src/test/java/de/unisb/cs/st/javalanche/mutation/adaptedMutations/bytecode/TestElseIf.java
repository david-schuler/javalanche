package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.mapping.Join;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class TestElseIf extends BaseTestJump {

	private static Class<?> classUnderTest = ElseIfTEMPLATE.class;

	public TestElseIf() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();

		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		// Method method = clazz.getMethod("m1", int.class);
		// assertNotNull(method);
		// checkUnmutated(10, 40, method, clazz);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
	}

	@Test
	public void test2() throws Exception {

		Class<?> clazz = prepareTest();

		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		// Method method = clazz.getMethod("m1", int.class);
		// assertNotNull(method);
		// checkUnmutated(10, 40, method, clazz);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
	}

	protected void transform(File classFile) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(classFile);
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		MutationsClassAdapter cv = new MutationsClassAdapter(cw, BytecodeInfo
				.read());
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] result = cw.toByteArray();
		String transformed = AsmUtil.checkClass(result);
		// String transformed = AsmUtil.classToString(result);
		FileUtils.writeByteArrayToFile(classFile, result);
		System.out.println("---------------------------------------");
		System.out.println(transformed);
	}
}