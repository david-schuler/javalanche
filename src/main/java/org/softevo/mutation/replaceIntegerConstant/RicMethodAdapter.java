package org.softevo.mutation.replaceIntegerConstant;

import java.util.logging.Logger;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.MutationManager;
import org.softevo.mutation.results.persistence.QueryManager;

public class RicMethodAdapter extends LineNumberAdapter {

	static Logger logger = Logger.getLogger(RicMethodAdapter.class.getName());

	public RicMethodAdapter(MethodVisitor mv, String className,
			String methodName) {
		super(mv, className.replace('/', '.'), methodName);
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

	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Integer) {
			Integer integerConstant = (Integer) cst;
			intConstant(integerConstant);
		} else if (cst instanceof Float) {
			Float floatConstant = (Float) cst;
			floatConstant(floatConstant);

		} else if (cst instanceof Long) {
			Long longConstant = (Long) cst;
			longConstant(longConstant);
		} else if (cst instanceof Double) {
			Double doubleConstant = (Double) cst;
			doubleConstant(doubleConstant);
		}
	}

	private void doubleConstant(final double doubleConstant) {
		logger.info("double constant for line: " + getLineNumber());
		Mutation mutation = new Mutation(className, getLineNumber(),
				MutationType.RIC_PLUS_1);
		Mutation mutationFromDB = QueryManager.getMutation(mutation);
//		if (MutationManager.shouldApplyMutation(mutation)) {
//			insertIfElse(mv, mutationFromDB, new MutationIfElse() {
//
//				public void ifBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Double(doubleConstant + 1.));
//				}
//
//				public void elseBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Double(doubleConstant));
//				}
//
//			});
//			logger.info("Applying mutation double constant + 1 in line: "
//					+ getLineNumber());
//		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			super.visitLdcInsn(new Double(doubleConstant));
//		}
	}

	private static void insertPrintStatements(MethodVisitor mv, String message) {
		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "err",
				"Ljava/io/PrintStream;");
		mv.visitLdcInsn("[RIC] " + message);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V");
	}

	private void floatConstant(final float f) {
		logger.info("float constant for line: " + getLineNumber());
		Mutation mutation = new Mutation(className, getLineNumber(),
				MutationType.RIC_PLUS_1);
		Mutation mutationFromDB = QueryManager.getMutation(mutation);
//		if (MutationManager.shouldApplyMutation(mutation)) {
//			insertIfElse(mv, mutationFromDB, new MutationIfElse() {
//
//				public void ifBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Float(f + 1.));
//				}
//
//				public void elseBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Float(f));
//				}
//			});
//			logger.info("Applying mutation float constant + 1 in line: "
//					+ getLineNumber());
//		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			super.visitLdcInsn(new Float(f));
//		}
	}

	private void longConstant(final long longConstant) {
		logger.info("long constant for line: " + getLineNumber());
		Mutation mutation = new Mutation(className, getLineNumber(),
				MutationType.RIC_PLUS_1);
		Mutation mutationFromDB = QueryManager.getMutation(mutation);
//		if (MutationManager.shouldApplyMutation(mutation)) {
//			insertIfElse(mv, mutationFromDB, new MutationIfElse() {
//
//				public void ifBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Long(longConstant + 1l));
//				}
//
//				public void elseBlock(MethodVisitor mv) {
//					mv.visitLdcInsn(new Long(longConstant));
//				}
//			});
//			logger.info("Applying mutation long constant + 1 in line: "
//					+ getLineNumber());
//		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			super.visitLdcInsn(new Long(longConstant));
//		}
	}

	private void insertPrintStatements(String message) {
		insertPrintStatements(mv, message);
	}

	private void intConstant(final int i) {
		logger.info("int constant for line: " + getLineNumber());
		Mutation mutation = new Mutation(className, getLineNumber(),
				MutationType.RIC_PLUS_1);

		Mutation mutationMinus = new Mutation(className, getLineNumber(),
				MutationType.RIC_MINUS_1);
		QueryManager.saveMutation(mutationMinus);

		Mutation mutationZero = new Mutation(className, getLineNumber(),
				MutationType.RIC_ZERO);
		QueryManager.saveMutation(mutationZero);

		Mutation mutationFromDB = QueryManager.getMutation(mutation);
		if (MutationManager.shouldApplyMutation(mutation)) {
			insertIfElse(mv, new MutationCode(null) {

				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(i));
				}

			}, new MutationCode[] { new MutationCode(mutationFromDB) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(i + 1));
				}

			}, new MutationCode(mutationMinus) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(i - 1));
				}

			}, new MutationCode(mutationZero) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitLdcInsn(new Integer(0));
				}

			}

			}

			);
			logger.info("Applying ric int mutations line: "
					+ getLineNumber());
		} else {
			logger.info("Applying no mutation for line: " + getLineNumber());
			super.visitLdcInsn(new Integer(i));
		}
	}

	private static void insertIfElse(MethodVisitor mv, MutationCode unMutated,
			MutationCode[] mutations) {
		Label endLabel = new Label();
		for (MutationCode mutationCode : mutations) {
			Mutation mutation = mutationCode.getMutation();
			mv.visitLdcInsn(mutation.getMutationVariable());
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.IFNULL, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			insertPrintStatements(mv, "Mutation "
					+ mutation.getMutationVariable() + " - "+ mutation.getMutationType() + "is enabled");
			mutationCode.insertCodeBlock(mv);
			mv.visitJumpInsn(Opcodes.GOTO, endLabel);
			mv.visitLabel(l1);
		}
		unMutated.insertCodeBlock(mv);
		mv.visitLabel(endLabel);
	}

}
