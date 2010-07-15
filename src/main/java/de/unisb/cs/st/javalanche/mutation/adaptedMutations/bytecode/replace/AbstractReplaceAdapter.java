/*
 * Copyriaght (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.replace;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import static org.objectweb.asm.Opcodes.*;

public abstract class AbstractReplaceAdapter extends AbstractMutationAdapter {

	int x = 2;

	public AbstractReplaceAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);

	}

	@Override
	public void visitVarInsn(final int opcode, final int var) {
		if (mutationCode || var == 0) {
			mv.visitVarInsn(opcode, var);
			return;
		}

		boolean insertCall = true;
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
				mv.visitVarInsn(opcode, var);
			}

		};
		if (opcode == ILOAD) {
			insertCall = addPosibility(Type.INT_TYPE, unMutated);
		}
		if (opcode == FLOAD) {
			insertCall = addPosibility(Type.FLOAT_TYPE, unMutated);
		}
		if (opcode == DLOAD) {
			insertCall = addPosibility(Type.DOUBLE_TYPE, unMutated);
		}
		if (opcode == LLOAD) {
			insertCall = addPosibility(Type.LONG_TYPE, unMutated);
		}
		if (opcode == ALOAD) {
			insertCall = addPosibility(Type.getObjectType("java.lang.Object"),
					unMutated);
		}

		if (opcode == ISTORE) {
			insertCall = addPosibilityStore(Type.INT_TYPE, unMutated);
		}
		if (opcode == FSTORE) {
			insertCall = addPosibilityStore(Type.FLOAT_TYPE, unMutated);
		}
		if (opcode == DSTORE) {
			insertCall = addPosibilityStore(Type.DOUBLE_TYPE, unMutated);
		}
		if (opcode == LSTORE) {
			insertCall = addPosibilityStore(Type.LONG_TYPE, unMutated);
		}
		if (opcode == ASTORE) {
			insertCall = addPosibilityStore(Type
					.getObjectType("java.lang.Object"), unMutated);
		}

		if (insertCall) {
			mv.visitVarInsn(opcode, var);
		}
	}

	private boolean addPosibilityStore(Type type, MutationCode unMutated) {
		return addPosibility(type, unMutated, false, true);
	}

	private boolean addPosibility(Type type, MutationCode unMutated) {
		return addPosibility(type, unMutated, false, false);
	}

	private boolean addPosibility(Type type, MutationCode unMutated,
			boolean fieldInsn, boolean store) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(),
				store ? Mutation.MutationType.ADAPTED_REPLACE_STORE
						: Mutation.MutationType.ADAPTED_REPLACE);
		addPossibilityForLine();
		return handleMutation(mutation, type, unMutated, fieldInsn, store);
	}

	// @Override
	// public void visitFieldInsn(final int opcode, final String owner,
	// final String name, final String desc) {
	// if (mutationCode || opcode == PUTSTATIC || opcode == PUTFIELD
	// || opcode == GETSTATIC) {
	// mv.visitFieldInsn(opcode, owner, name, desc);
	// return;
	// }
	// Type type = Type.getType(desc);
	// MutationCode unMutated = new MutationCode(null) {
	// @Override
	// public void insertCodeBlock(MethodVisitor mv) {
	// mv.visitFieldInsn(opcode, owner, name, desc);
	// }
	//
	// };
	// boolean insertCall = addPosibility(type, unMutated, true);
	// if (insertCall) {
	// mv.visitFieldInsn(opcode, owner, name, desc);
	// }
	//
	// }
	@Override
	public void visitFieldInsn(final int opcode, final String owner,
			final String name, final String desc) {
		if (mutationCode || opcode == GETSTATIC || opcode == PUTSTATIC) {
			mv.visitFieldInsn(opcode, owner, name, desc);
			return;
		}
		Type type = Type.getType(desc);
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
				mv.visitFieldInsn(opcode, owner, name, desc);
			}

		};
		boolean insertCall = false;
		if (opcode == GETFIELD) {
			insertCall = addPosibility(type, unMutated, true, false);
		}
		if (opcode == PUTFIELD) {
			insertCall = addPosibility(type, unMutated, true, true);
		}
		if (insertCall) {
			mv.visitFieldInsn(opcode, owner, name, desc);
		}

	}

	protected abstract boolean handleMutation(Mutation mutation, Type type,
			MutationCode unMutated, boolean fieldInsn, boolean store);

}
