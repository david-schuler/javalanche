package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class IntegrateSuiteTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(IntegrateSuiteTransformer.class);
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

	public static BytecodeTransformer getIntegrateSelectiveTestSuiteTransformer() {
		String targetClass = "de/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite";
		String integrationMethod = "toMutationTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lde/unisb/cs/st/javalanche/mutation/runtime/testsuites/MutationTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateRandomPermutationTestSuiteTransformer() {
		String targetClass = "de/unisb/cs/st/javalanche/mutation/runtime/testsuites/RandomPermutationTestSuite";
		String integrationMethod = "toRandomPermutationTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lde/unisb/cs/st/javalanche/mutation/runtime/testsuites/RandomPermutationTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateScanAndCoverageTestSuiteTransformer() {
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
}
