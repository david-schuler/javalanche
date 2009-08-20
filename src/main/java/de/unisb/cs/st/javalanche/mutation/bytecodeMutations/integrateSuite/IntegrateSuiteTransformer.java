package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class IntegrateSuiteTransformer extends BytecodeTransformer {

	private String targetClass;

	private String integrationMethod;

	private String integrationMethodSignature;

	private static class IntegrateSuiteClassAdapter extends ClassAdapter {

		private String targetClass;

		private String integrationMethod;

		private String integrationMethodSignature;

		public IntegrateSuiteClassAdapter(ClassVisitor cv, String targetClass,
				String integrationMethod, String integrationMethodSignature) {
			super(cv);
			this.targetClass = targetClass;
			this.integrationMethod = integrationMethod;
			this.integrationMethodSignature = integrationMethodSignature;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			if (name.equals("suite")) {
				mv = new IntegrateTestSuiteMethodAdapter(mv, targetClass,
						integrationMethod, integrationMethodSignature);
			}
			return mv;
		}
	}

	public IntegrateSuiteTransformer(String targetClass,
			String integrationMethod, String integrationMethodSignature) {
		super();
		this.targetClass = targetClass;
		this.integrationMethod = integrationMethod;
		this.integrationMethodSignature = integrationMethodSignature;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		if (MutationProperties.TRACE_BYTECODE) {
			cc = new TraceClassVisitor(cc, new PrintWriter(System.out));
		}
		return new IntegrateSuiteClassAdapter(cc, targetClass,
				integrationMethod, integrationMethodSignature);
	}

	// private static BytecodeTransformer
	// getIntegrateSelectiveTestSuiteTransformer() {
	// String targetClass =
	// "de/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite";
	// String integrationMethod = "toMutationTestSuite";
	// String integrationMethodSignature =
	// "(Ljunit/framework/TestSuite;)Lde/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite;";
	// return new IntegrateSuiteTransformer(targetClass, integrationMethod,
	// integrationMethodSignature);
	// }

	public static BytecodeTransformer getIntegrateTransformer() {
		String targetClass = "de/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite";
		String integrationMethod = "toMutationTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lde/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateCheckNamesTransformer() {
		String targetClass = "de/unisb/cs/st/javalanche/mutation/runtime/testsuites/CheckNamesTestSuite";
		String integrationMethod = "toCheckNamesTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lde/unisb/cs/st/javalanche/mutation/runtime/testsuites/CheckNamesTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static byte[] modifyJunit4Adapter(byte[] bytecode) {
		ClassReader cr = new ClassReader(bytecode);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
		CheckClassAdapter cc = new CheckClassAdapter(cw);
		ClassVisitor cv = new ModifyJunit4Adaper(cc);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		return cw.toByteArray();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		URL systemResource = ClassLoader
				.getSystemResource("junit/framework/JUnit4TestAdapter.class");
		Class<?> forName = Class.forName("junit.framework.JUnit4TestAdapter");
		System.out.println(forName);
		InputStream openStream = systemResource.openStream();

		ClassReader cr = new ClassReader(openStream);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
		CheckClassAdapter cc = new CheckClassAdapter(cw);
		ClassVisitor cv = new ModifyJunit4Adaper(cc);
		cr.accept(cv, ClassReader.SKIP_FRAMES);

	}

}
