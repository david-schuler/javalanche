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

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.AbstractVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public abstract class AbstractNegateJumpsAdapter extends
		AbstractMutationAdapter {

	public static final int POP_ONCE_TRUE = -2;
	public static final int POP_ONCE_FALSE = -3;
	public static final int POP_TWICE_TRUE = -4;
	public static final int POP_TWICE_FALSE = -5;

	private static final Logger logger = Logger
			.getLogger(AbstractNegateJumpsAdapter.class);

	protected static Map<Integer, Integer> jumpReplacementMap = JumpReplacements
			.getReplacementMap();

	public AbstractNegateJumpsAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);

	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (mutationCode) {
			mv.visitJumpInsn(opcode, label);
			return;
		}
		if (jumpReplacementMap.containsKey(opcode)) {
			addJumpMutationPossibility(label, opcode);
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}

	private void addJumpMutationPossibility(Label label, int opcode) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(),
				Mutation.MutationType.NEGATE_JUMP);
		int replaceOpcode = JumpReplacements.getReplacementMap().get(opcode);
		generateAddInfo(mutation, opcode, replaceOpcode);
		addPossibilityForLine();
		logger.debug("Found possibility for line " + getLineNumber() + "("
				+ getPossibilityForLine() + ")");
		handleMutation(mutation, label, opcode);
	}

	public static void generateAddInfo(Mutation mutation, int opcode,
			int replaceOpcode) {
		mutation.setOperatorAddInfo("" + replaceOpcode);
		String replaceString;
		if (replaceOpcode > 0) {
			replaceString = AbstractVisitor.OPCODES[replaceOpcode];
		} else {
			String add = "false";
			if (replaceOpcode == POP_ONCE_TRUE
					|| replaceOpcode == POP_TWICE_TRUE) {
				add = "true";
			}
			replaceString = "Always " + add;
		}
		mutation.setAddInfo("Replace " + AbstractVisitor.OPCODES[opcode]
				+ " with  " + replaceString);
	}

	protected abstract void handleMutation(Mutation mutation, Label label,
			int opcode);

}
