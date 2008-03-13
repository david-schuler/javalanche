package org.softevo.mutation.bytecodeMutations.integrateSuite;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class IntegrateTestSuiteMethodAdapter extends MethodAdapter {

	public IntegrateTestSuiteMethodAdapter(MethodVisitor mv) {
		super(mv);
	}

	@Override
	public void visitInsn(int opcode) {
		if (opcode == Opcodes.ARETURN) {
			mv
					.visitMethodInsn(
							INVOKESTATIC,
							"org/softevo/mutation/runtime/SelectiveTestSuite",
							"toSelectiveTestSuite",
							"(Ljunit/framework/TestSuite;)Lorg/softevo/mutation/runtime/SelectiveTestSuite;");

		}
		mv.visitInsn(opcode);
	}
}
