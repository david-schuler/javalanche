/*
* Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public abstract class AbstractUnaryOperatorMethodAdapater extends
		AbstractMutationAdapter {

	public static final String MINUS = "MINUS";
	public static final String BITWISE_NEGATE = "BITWISE_NEGATE";

	public AbstractUnaryOperatorMethodAdapater(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
	}

	public void visitVarInsn(int opcode, int var) {
		Integer type = null;
		if (opcode == ILOAD) {
			type = INTEGER;
		} else if (opcode == LLOAD) {
			type = LONG;
		} else if (opcode == FLOAD) {
			type = FLOAT;
		} else if (opcode == DLOAD) {
			type = DOUBLE;
		}
		super.visitVarInsn(opcode, var);
		if (type != null) {
			mutateLocal(type);
		}
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {

		Integer typeOp = null;
		if (opcode == GETSTATIC || opcode == GETFIELD) {
			Type type = Type.getType(desc);
			if (type.equals(Type.INT_TYPE)) {
				typeOp = INTEGER;
			} else if (type.equals(Type.LONG_TYPE)) {
				typeOp = LONG;
			} else if (type.equals(Type.FLOAT_TYPE)) {
				typeOp = FLOAT;
			} else if (type.equals(Type.DOUBLE_TYPE)) {
				typeOp = DOUBLE;
			}
		}
		super.visitFieldInsn(opcode, owner, name, desc);
		if (typeOp != null) {
			mutateLocal(typeOp);
		}

	}

	private void mutateLocal(Integer type) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(), UNARY_OPERATOR);
		addPossibilityForLine();
		handleMutation(mutation, type);
	}

	protected abstract void handleMutation(Mutation mutation, Integer type);

}
