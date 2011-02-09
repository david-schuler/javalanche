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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class RemoveMethodCallsMethodAdapter extends AbstractRemoveCallsAdapter {

	private final MutationManager mutationManager;
	
	private static final class RemoveCall extends MutationCode {
		private final String name;
		private final String desc;
		private final int opcode;

		private RemoveCall(Mutation mutation, String name, String desc,
				int opcode) {
			super(mutation);
			this.name = name;
			this.desc = desc;
			this.opcode = opcode;
		}

		@Override
		public void insertCodeBlock(MethodVisitor mv) {
			if (name.equals("<init>")) {
				mv.visitInsn(POP);
				mv.visitInsn(POP);
				mv.visitInsn(ACONST_NULL);
			} else {
				popArguments(desc, mv);
				popThisReference(opcode, mv);
				pushDefaultValue(desc, mv, name);
			}
		}

		private void pushDefaultValue(final String desc, MethodVisitor mv,
				String name) {
			Type returnType = Type.getReturnType(desc);
			if (returnType.equals(Type.BOOLEAN_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.BYTE_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.CHAR_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.DOUBLE_TYPE)) {
				mv.visitInsn(DCONST_0);
			} else if (returnType.equals(Type.FLOAT_TYPE)) {
				mv.visitInsn(FCONST_0);
			} else if (returnType.equals(Type.INT_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.LONG_TYPE)) {
				mv.visitInsn(LCONST_0);
			} else if (returnType.equals(Type.SHORT_TYPE)) {
				mv.visitInsn(ICONST_0);
			} else if (returnType.equals(Type.VOID_TYPE)) {
			} else {
				mv.visitInsn(ACONST_NULL);
			}
		}

		private void popThisReference(final int opcode, MethodVisitor mv) {
			if (!isStaticCall(opcode)) {
				mv.visitInsn(POP);
			}
		}

		private static boolean isStaticCall(int opcode) {
			return opcode == INVOKESTATIC;
		}

		private void popArguments(final String desc, MethodVisitor mv) {
			Type[] argumentsTypes = Type.getArgumentTypes(desc);
			for (int i = argumentsTypes.length - 1; i >= 0; i--) {
				Type argumentType = argumentsTypes[i];

				if (argumentType.getSize() == 1) {
					mv.visitInsn(POP);
				} else {
					mv.visitInsn(POP2);
				}
			}
		}
	}

	private static Logger logger = Logger
			.getLogger(RemoveMethodCallsMethodAdapter.class);
	
	public RemoveMethodCallsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}


	@Override
	protected void handleMutation(Mutation mutation, final int opcode,
			final String owner, final String name, final String desc) {
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(opcode, owner, name, desc);
				}

			};
			MutationCode mutated = new RemoveCall(dbMutation, name, desc,
					opcode);
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			logger.debug("Not applying mutation");
			mv.visitMethodInsn(opcode, owner, name, desc);
		}
	}

}
