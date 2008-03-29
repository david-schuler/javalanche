package org.softevo.mutation.bytecodeMutations.integrateSuite;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
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
		return new IntegrateSuiteClassAdapter(cc, targetClass,
				integrationMethod, integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateSelectiveTestSuiteTransformer() {
		String targetClass = "org/softevo/mutation/runtime/SelectiveTestSuite";
		String integrationMethod = "toSelectiveTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lorg/softevo/mutation/runtime/SelectiveTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateRandomPermutationTestSuiteTransformer() {
		String targetClass = "org/softevo/mutation/runtime/RandomPermutationTestSuite";
		String integrationMethod = "toRandomPermutationTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lorg/softevo/mutation/runtime/RandomPermutationTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}

	public static BytecodeTransformer getIntegrateScanAndCoverageTestSuiteTransformer() {
		String targetClass = "org/softevo/mutation/runtime/ScanAndCoverageTestSuite";
		String integrationMethod = "toScanAndCoverageTestSuite";
		String integrationMethodSignature = "(Ljunit/framework/TestSuite;)Lorg/softevo/mutation/runtime/ScanAndCoverageTestSuite;";
		return new IntegrateSuiteTransformer(targetClass, integrationMethod,
				integrationMethodSignature);
	}
}
