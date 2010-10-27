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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

/**
 * MethodAdapter to find possible locations to aplly a mutation that replaces an
 * arithmetic operator.
 * 
 */
public class PossibilitiesArithmeticReplaceMethodAdapter extends
		AbstractArithmeticMethodAdapter {

	private MutationPossibilityCollector mpc;

	public PossibilitiesArithmeticReplaceMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mpc = mpc;
	}

	/**
	 * Adds a mutation posibility to the {@link MutationPossibilityCollector}.
	 */
	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		if (!mutationCode) {
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
		mv.visitInsn(opcode);
	}
}
