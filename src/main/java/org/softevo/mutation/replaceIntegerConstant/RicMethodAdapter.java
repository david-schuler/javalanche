package org.softevo.mutation.replaceIntegerConstant;

import java.util.logging.Logger;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.mutationPossibilities.MutationPossibility;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class RicMethodAdapter extends LineNumberAdapter {

	Logger logger = Logger.getLogger(RicMethodAdapter.class.getName());

	Mutations mutationsToApply;

	public RicMethodAdapter(MethodVisitor mv, String className,
			String methodName, Mutations mutationsToApply) {
		super(mv, className.replace('/', '.'), methodName);
		this.mutationsToApply = mutationsToApply;
		logger.info("MethodName:" + methodName);

	}

	@Override
	public void visitCode() {
		super.visitCode();
		// super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
		// "Ljava/io/PrintStream;");
		// super.visitLdcInsn("[Mutation] Method " + methodName + " is called");
		// super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
		// "println", "(Ljava/lang/String;)V");
	}

	public void visitInsn(int opcode) {
		// super.visitInsn(opcode);
		switch (opcode) {
		case Opcodes.ICONST_M1:
			intConstant(-1);
			break;
		case Opcodes.ICONST_0:
			intConstant(0);
			break;
		case Opcodes.ICONST_1:
			intConstant(1);
			break;
		case Opcodes.ICONST_2:
			intConstant(2);
			break;
		case Opcodes.ICONST_3:
			intConstant(3);
			break;
		case Opcodes.ICONST_4:
			intConstant(4);
			break;
		case Opcodes.ICONST_5:
			intConstant(5);
			break;
		case Opcodes.LCONST_0:
			longConstant(0);
			break;
		case Opcodes.LCONST_1:
			longConstant(1);
			break;
		case Opcodes.FCONST_0:
			floatConstant(0);
			break;
		case Opcodes.FCONST_1:
			floatConstant(1);
			break;
		case Opcodes.FCONST_2:
			floatConstant(2);
			break;
		case Opcodes.DCONST_0:
			doubleConstant(0);
			break;
		case Opcodes.DCONST_1:
			doubleConstant(1);
			break;

		default:
			super.visitInsn(opcode);
			break;
		}
	}

	private void doubleConstant(int i) {
		super.visitLdcInsn(new Double(i));
		insertPrintStatements("Double");
	}

	private void insertPrintStatements(String message) {
		super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		super.visitLdcInsn("[Mutation] " + message);
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");
	}

	private void floatConstant(int i) {
		super.visitLdcInsn(new Float(i));
		insertPrintStatements("float");
	}

	private void longConstant(int i) {
		super.visitLdcInsn(new Long(i));
		insertPrintStatements("long");
	}

	private void intConstant(int i) {
		logger.info("int constant for line: " + getLineNumber());
		MutationPossibility mp = getMutation();
		if (mp != null) {

			super.visitLdcInsn(mp.getMutationVariable());
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
			Label l1 = new Label();
			super.visitJumpInsn(Opcodes.IFNULL, l1);
			Label l2 = new Label();
			super.visitLabel(l2);
			insertPrintStatements("[Mutation] Mutation " + mp.getMutionId()
					+ " is enabled");
			super.visitLdcInsn(new Integer(i + 1));
			Label l3 = new Label();
			super.visitJumpInsn(Opcodes.GOTO, l3);
			super.visitLabel(l1);
			super.visitLdcInsn(new Integer(i));
			super.visitLabel(l3);
			logger.info("Applying mutation constant + 1 in line: "
					+ getLineNumber());
		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			// super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
			// "out",
			// "Ljava/io/PrintStream;");
			// super.visitLdcInsn("[Mutation] Mutation for line " +
			// getLineNumber()
			// + " is not enabled");
			// super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
			// "java/io/PrintStream",
			// "println", "(Ljava/lang/String;)V");
			super.visitLdcInsn(new Integer(i));
		}
	}

	// @Override
	// protected void biOrSiPush(int operand) {
	// //mv.visitLdcInsn(operand);
	// }
	//
	// @Override
	// protected void doubleConstant(int i) {
	// //mv.visitLdcInsn(i);
	// }
	//
	// @Override
	// protected void floatConstant(int i) {
	// //mv.visitLdcInsn(i);
	// }
	//
	// @Override
	// protected void intConstant(int i) {
	// if (shouldApplyMutation()) {
	// logger.info("Applying mutation constant + 1 in line "
	// + getLineNumber());
	// //mv.visitLdcInsn(i);
	// } else {
	// //mv.visitLdcInsn(i);
	// }
	// }

	private MutationPossibility getMutation() {
		if (mutationsToApply.contains(className, getLineNumber())) {
			return mutationsToApply.get(className, getLineNumber());
		}
		return null;

	}

	// @Override
	// protected void ldc(Object constant) {
	// //mv.visitLdcInsn(constant);
	// }
	//
	// @Override
	// protected void longConstant(int i) {
	// //mv.visitLdcInsn(i);
	// }

}
