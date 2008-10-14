package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class IntegrateTestSuiteMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger
			.getLogger(IntegrateTestSuiteMethodAdapter.class);

	private String targetClass;

	private String integrationMethod;

	private String integrationMethodSignature;

	public IntegrateTestSuiteMethodAdapter(MethodVisitor mv,
			String targetClass, String integrationMethod,
			String integrationMethodSignature) {
		super(mv);
		this.targetClass = targetClass;
		this.integrationMethod = integrationMethod;
		this.integrationMethodSignature = integrationMethodSignature;
	}

	@Override
	public void visitInsn(int opcode) {
		if (opcode == Opcodes.ARETURN) {
			logger.info("Integrating Testsuite+ " + targetClass  + "    "  + integrationMethod);
			mv.visitMethodInsn(INVOKESTATIC, targetClass, integrationMethod,
					integrationMethodSignature);

		}
		mv.visitInsn(opcode);
	}

}
