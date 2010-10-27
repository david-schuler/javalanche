/*
* Copyright (C) 2010 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;

public abstract class AbstractRicMethodAdapter extends AbstractMutationAdapter {

	private final boolean forwardCalls;

	public AbstractRicMethodAdapter(MethodVisitor mv, String className,
			String methodName, boolean forwardCalls,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
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
