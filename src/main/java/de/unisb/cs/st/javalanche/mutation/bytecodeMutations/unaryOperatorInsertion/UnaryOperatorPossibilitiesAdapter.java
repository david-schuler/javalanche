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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class UnaryOperatorPossibilitiesAdapter extends
		AbstractUnaryOperatorMethodAdapater {

	private final MutationPossibilityCollector mpc;

	public UnaryOperatorPossibilitiesAdapter(MethodVisitor mv,
			String className, String methodName,
			Map<Integer, Integer> possibilities, String desc,
			MutationPossibilityCollector mpc) {
		super(mv, className, methodName, possibilities, desc);
		this.mpc = mpc;
	}

	@Override
	protected void handleMutation(Mutation mutation, Integer type) {
		Mutation mUnaryMinus = Mutation.copyMutation(mutation);
		mUnaryMinus.setOperatorAddInfo(MINUS);
		mUnaryMinus.setAddInfo("Insert unary minus");
		mpc.addPossibility(mUnaryMinus);
		CoverageDataUtil.insertCoverageCalls(mv, mUnaryMinus);
		QueryManager.saveMutation(mUnaryMinus);
		Long id = mUnaryMinus.getId();

		Mutation mUnaryNegate = Mutation.copyMutation(mutation);
		mUnaryNegate.setOperatorAddInfo(BITWISE_NEGATE);
		mUnaryNegate.setAddInfo("Insert bitwise negation");
		mUnaryNegate.setBaseMutationId(id);
		QueryManager.saveMutation(mUnaryNegate);
		if (mUnaryNegate.getId() != null) {
			MutationCoverageFile.addDerivedMutation(id, mUnaryNegate.getId());
		}
		mpc.addPossibility(mUnaryNegate);

	}

}
