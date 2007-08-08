package org.softevo.mutation.replaceIntegerConstant;

import java.util.logging.Logger;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.MutationManager;

public class RicMethodAdapter extends LineNumberAdapter {

	static Logger logger = Logger.getLogger(RicMethodAdapter.class.getName());

	Mutations mutationsToApply;

	public RicMethodAdapter(MethodVisitor mv, String className,
			String methodName, Mutations mutationsToApply) {
		super(mv, className.replace('/', '.'), methodName);
		this.mutationsToApply = mutationsToApply;
		logger.info("MethodName:" + methodName);

	}

	public void visitInsn(int opcode) {
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

	private static void insertPrintStatements(MethodVisitor mv, String message) {
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[MutationType] " + message);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
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

	private void insertPrintStatements(String message) {
		insertPrintStatements(mv, message);
	}

	private void intConstant(final int i) {
		logger.info("int constant for line: " + getLineNumber());
		Mutation mutation = new Mutation(className,getLineNumber(),MutationType.RIC_PLUS_1);
		if(MutationManager.shouldApplyMutation(mutation))
		{
			insertIfElse( mv,mutation, new MutationIfElse() {

				public void ifBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(i + 1));
				}

				public void elseBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(i));
				}

			});
			logger.info("Applying mutation constant + 1 in line: "
					+ getLineNumber());
		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			super.visitLdcInsn(new Integer(i));
		}
	}


	private static void insertIfElse(MethodVisitor mv, Mutation mutation,
			MutationIfElse mutationIfElse) {
		mv.visitLdcInsn(mutation.getMutationVariable());
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
				"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
		Label l1 = new Label();
		mv.visitJumpInsn(Opcodes.IFNULL, l1);
		Label l2 = new Label();
		mv.visitLabel(l2);
		insertPrintStatements(mv,"[MutationType] MutationType "
				+ mutation.getMutionId() + " is enabled");
		mutationIfElse.ifBlock(mv);
		Label l3 = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, l3);
		mv.visitLabel(l1);
		mutationIfElse.elseBlock(mv);
		mv.visitLabel(l3);
	}

}
