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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Method Adapter that replaces arithmetic operations. The details for the
 * replacements can be found in {@link ReplaceMap}.
 * 
 * @see ReplaceMap
 * 
 * @author David Schuler
 * 
 */
public abstract class AbstractArithmeticMethodAdapter extends
		AbstractMutationAdapter {

	protected static Map<Integer, Integer> replaceMap = ReplaceMap
			.getReplaceMap();

	public AbstractArithmeticMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
	}

	@Override
	public void visitInsn(int opcode) {
		if (replaceMap.containsKey(opcode) && !mutationCode) {
			mutate(opcode);
		} else {
			super.visitInsn(opcode);
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	private void mutate(int opcode) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(),
				getPossibilityForLine(),
				Mutation.MutationType.ARITHMETIC_REPLACE);
		mutation.setAddInfo("Replace " + opcode + " with "
				+ ReplaceMap.getReplaceMap().get(opcode) + "");
		mutation.setOperatorAddInfo(ReplaceMap.getReplaceMap().get(opcode) + "");
		addPossibilityForLine();
		handleMutation(mutation, opcode);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode);

}
