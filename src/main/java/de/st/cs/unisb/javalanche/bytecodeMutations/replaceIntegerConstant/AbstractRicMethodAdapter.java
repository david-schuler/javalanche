package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.bytecodeMutations.AbstractMutationAdapter;

public abstract class AbstractRicMethodAdapter extends AbstractMutationAdapter {

	private final boolean forwardCalls;

	public AbstractRicMethodAdapter(MethodVisitor mv, String className,
			String methodName, boolean forwardCalls,
			Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
		this.forwardCalls = forwardCalls;
	}

	@Override
	public void visitInsn(int opcode) {
		if (mutationCode) {
			super.visitInsn(opcode);
			return;
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
		if (forwardCalls) {
			super.visitInsn(opcode);
		}
	}

	protected abstract void doubleConstant(int i);

	protected abstract void floatConstant(int i);

	protected abstract void longConstant(int i);

	protected abstract void intConstant(int i);

	protected abstract void biOrSiPush(int operand);

	protected abstract void ldc(Number constant);

	@Override
	public void visitLdcInsn(Object constant) {
		if (mutationCode) {
			super.visitLdcInsn(constant);
			return;
		}
		if (constant instanceof Number) {
			ldc((Number) constant);
			if (forwardCalls) {
				super.visitLdcInsn(constant);
			}
		} else {
			super.visitLdcInsn(constant);
		}

	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		if (mutationCode) {
			super.visitIntInsn(opcode, operand);
			return;
		}
		if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
			intConstant(operand);
			if (forwardCalls) {
				super.visitIntInsn(opcode, operand);
			}
		} else {
			super.visitIntInsn(opcode, operand);
		}
	}

}