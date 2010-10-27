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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public abstract class AbstractJumpsAdapter extends AbstractMutationAdapter {

	protected static Map<Integer, Integer> jumpReplacementMap = JumpReplacements
			.getReplacementMap();

	public AbstractJumpsAdapter(MethodVisitor mv, String className,
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
				Mutation.MutationType.ADAPTED_JUMP);
		addPossibilityForLine();
		handleMutation(mutation, label, opcode);
	}

	protected abstract void handleMutation(Mutation mutation, Label label,
			int opcode);

}
