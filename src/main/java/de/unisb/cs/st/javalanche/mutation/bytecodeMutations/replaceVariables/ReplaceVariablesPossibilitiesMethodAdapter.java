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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public final class ReplaceVariablesPossibilitiesMethodAdapter extends
		AbstractReplaceVariablesAdapter {

	private final MutationPossibilityCollector mpc;

	public ReplaceVariablesPossibilitiesMethodAdapter(MethodVisitor mv,

			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities, String desc,
			List<VariableInfo> staticVariables,
			List<VariableInfo> classVariables) {
		super(mv, className, methodName, possibilities, desc,
				staticVariables,
				classVariables);
		this.mpc = mpc;
	}

	@Override
	protected boolean handleMutation(Mutation mutation, int opcode,
			String owner, String name, String desc, String[] replaceNames) {
		// mpc.addPossibility(m);
		int mforLine = mutation.getMutationForLine();
		for (String replaceName : replaceNames) {
			Mutation m2 = new Mutation(mutation.getClassName(),
					mutation.getMethodName(), mutation.getLineNumber(),
					mforLine, mutation.getMutationType());
			m2.setAddInfo("Replace: " + name + " with " + replaceName);
			m2.setOperatorAddInfo(replaceName);
			mpc.addPossibility(m2);
			CoverageDataUtil.insertCoverageCalls(mv, m2);

			// mforLine++;
		}
		return true;
	}

	@Override
	protected boolean handleLocalMutation(Mutation mutation, int opcode,
			int var, List<Integer> replaceLocals) {
		int mforLine = mutation.getMutationForLine();
		for (Integer replaceVar : replaceLocals) {
			Mutation m2 = new Mutation(mutation.getClassName(),
					mutation.getMethodName(), mutation.getLineNumber(),
					mforLine, mutation.getMutationType());
			m2.setAddInfo("Replace: " + var + " with " + replaceVar);
			m2.setOperatorAddInfo(replaceVar + "");
			mpc.addPossibility(m2);
			CoverageDataUtil.insertCoverageCalls(mv, m2);
			// mforLine++;
		}
		return true;
	}

}
