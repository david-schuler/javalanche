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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import java.util.ArrayList;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Method Adapter that replaces arithmetic operations. The details for the
 * replacements can be found in {@link ReplaceMap}.
 * 
 * @see ReplaceMap
 * 
 * @author David Schuler
 * 
 */
public class ArithmeticReplaceMethodAdapter extends
		AbstractArithmeticMethodAdapter {

	protected MutationManager mutationManager;

	public ArithmeticReplaceMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	private static Logger logger = Logger
			.getLogger(ArithmeticReplaceMethodAdapter.class);

	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		logger.debug("Querying mutation " + mutation);
		List<Mutation> mutations = QueryManager.getMutations(
				className.replace('/', '.'), methodName + desc,
				mutation.getLineNumber(), mutation.getMutationForLine(),
				MutationType.ARITHMETIC_REPLACE);
		MutationCode unMutated = new SingleInsnMutationCode(null, opcode);
		List<MutationCode> mutationCode = new ArrayList<MutationCode>();
		for (Mutation m : mutations) {
			if (mutationManager.shouldApplyMutation(m)) {
				MutationCode mutated = new SingleInsnMutationCode(m,
						Integer.parseInt(m.getOperatorAddInfo()));
				mutationCode.add(mutated);
			}
		}
		if (mutationCode.size() > 0) {
			BytecodeTasks.insertIfElse(mv, unMutated,
					mutationCode.toArray(new MutationCode[0]));
		} else {
			mv.visitInsn(opcode);
		}
	}
}
