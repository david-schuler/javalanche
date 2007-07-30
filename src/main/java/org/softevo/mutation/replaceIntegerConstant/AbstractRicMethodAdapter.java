package org.softevo.mutation.replaceIntegerConstant;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.softevo.mutation.mutationPossibilities.MutationPossibility;

public abstract class AbstractRicMethodAdapter extends LineNumberAdapter {

	private final boolean forwardCalls;

	public AbstractRicMethodAdapter(MethodVisitor mv, String className,
			String methodName, boolean forwardCalls) {
		super(mv, className, methodName);
		this.forwardCalls = forwardCalls;
	}

	@Override
	public void visitInsn(int opcode) {
		if (forwardCalls) {
			super.visitInsn(opcode);
		}
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
			break;
		}
	}

	protected abstract void doubleConstant(int i);

	protected abstract void floatConstant(int i);

	protected abstract void longConstant(int i);

	protected abstract void intConstant(int i);

	protected abstract void biOrSiPush(int operand);

	protected abstract void ldc(Object constant);

	@Override
	public void visitLdcInsn(Object constant) {
		if (forwardCalls) {
			super.visitLdcInsn(constant);
		}
		if (constant instanceof Type) {
			super.visitLdcInsn(constant);
//			Type type = (Type) constant;
		}
		if (constant instanceof Number) {
			ldc(constant);
		}
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		if (forwardCalls) {
			super.visitIntInsn(opcode, operand);
		}
		if (opcode == Opcodes.SIPUSH) {
			biOrSiPush(operand);
		} else if (opcode == Opcodes.SIPUSH) {
			biOrSiPush(operand);
		}

	}

}