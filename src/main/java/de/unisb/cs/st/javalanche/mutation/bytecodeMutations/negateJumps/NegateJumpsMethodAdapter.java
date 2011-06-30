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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class NegateJumpsMethodAdapter extends AbstractNegateJumpsAdapter {

	public static final int POP_ONCE_TRUE = -2;
	public static final int POP_ONCE_FALSE = -3;
	public static final int POP_TWICE_TRUE = -4;
	public static final int POP_TWICE_FALSE = -5;

	private static Logger logger = Logger
			.getLogger(NegateJumpsMethodAdapter.class);

	private final MutationManager mutationManager;

	public NegateJumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected void handleMutation(Mutation mutation, final Label label,
			final int opcode) {
		if (mutationManager.shouldApplyMutation(mutation)) {
			logger.debug("Applying mutation for line: " + getLineNumber());

			Mutation dbMutation = QueryManager.getMutation(mutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}

			};
			List<Mutation> mutations = QueryManager.getMutations(
					mutation.getClassName(), mutation.getMethodName(),
					mutation.getLineNumber(), mutation.getMutationForLine(),
					MutationType.NEGATE_JUMP);
			List<MutationCode> mutationCode = new ArrayList<MutationCode>();
			for (final Mutation m : mutations) {
				MutationCode mutated = new MutationCode(m) {
					@Override
					public void insertCodeBlock(MethodVisitor mv) {
						int insertOpcode = Integer.parseInt(m
								.getOperatorAddInfo());
						if (insertOpcode == POP_ONCE_TRUE) {
							mv.visitInsn(Opcodes.POP);
							mv.visitJumpInsn(Opcodes.GOTO, label);
						} else if (insertOpcode == POP_ONCE_FALSE) {
							mv.visitInsn(Opcodes.POP);
						} else if (insertOpcode == POP_TWICE_TRUE) {
							mv.visitInsn(Opcodes.POP2);
							mv.visitJumpInsn(Opcodes.GOTO, label);
						} else if (insertOpcode == POP_TWICE_FALSE) {
							mv.visitInsn(Opcodes.POP2);
						} else {
							mv.visitJumpInsn(insertOpcode, label);
						}
					}
				};
				mutationCode.add(mutated);
			}

			BytecodeTasks.insertIfElse(mv, unMutated,
					mutationCode.toArray(new MutationCode[0]));
			// MutationCode mutated = new MutationCode(dbMutation) {
			// @Override
			// public void insertCodeBlock(MethodVisitor mv) {
			// if (jumpReplacementMap.containsKey(opcode)) {
			// int insertOpcode = jumpReplacementMap.get(opcode);
			// mv.visitJumpInsn(insertOpcode, label);
			// } else {
			// throw new RuntimeException(
			// "Invalid opcode key for jump Map");
			// }
			// }
			// };
			// BytecodeTasks.insertIfElse(mv, unMutated,
			// new MutationCode[] { mutated });
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}
}
