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

import java.util.Map;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public final class RemoveCallsPossibilitiesMethodAdapter extends
		AbstractRemoveCallsAdapter {

	private final MutationPossibilityCollector mpc;

	private MyAdviceAdapter myAdviceAdapter;

	public RemoveCallsPossibilitiesMethodAdapter(MyAdviceAdapter mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
		myAdviceAdapter = mv;
		this.mpc = mpc;
	}

	@Override
	protected void handleMutation(Mutation mutation, int opcode, String owner,
			String name, String desc) {
		if (myAdviceAdapter.superCallSeen()) {
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

}
