/*
* Copyright (C) 2009 Saarland University
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

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class NegateJumpsMethodAdapter extends AbstractNegateJumpsAdapter {

	private static Logger logger = Logger
			.getLogger(NegateJumpsMethodAdapter.class);
	
	public NegateJumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	@Override
	protected void handleMutation(Mutation mutation, final Label label,
			final int opcode) {
		if (MutationManager.shouldApplyMutation(mutation)) {
			logger.debug("Applying mutation for line: " + getLineNumber());
			
			Mutation dbMutation = QueryManager.getMutation(mutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}

			};

			MutationCode mutated = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (jumpReplacementMap.containsKey(opcode)) {
						int insertOpcode = jumpReplacementMap.get(opcode);
						mv.visitJumpInsn(insertOpcode, label);
					} else {
						throw new RuntimeException(
								"Invalid opcode key for jump Map");
					}
				}
			};
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}
}
