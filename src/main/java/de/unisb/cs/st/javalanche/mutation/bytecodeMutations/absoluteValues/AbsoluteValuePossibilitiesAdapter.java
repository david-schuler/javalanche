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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class AbsoluteValuePossibilitiesAdapter extends
		AbstractAbsoluteValueAdapter {

	private static final Logger logger = Logger
			.getLogger(AbsoluteValuePossibilitiesAdapter.class);
	private final MutationPossibilityCollector mpc;

	public AbsoluteValuePossibilitiesAdapter(MethodVisitor mv,
			String className, String methodName,
			Map<Integer, Integer> possibilities, String desc,
			MutationPossibilityCollector mpc) {
		super(mv, className, methodName, possibilities, desc);
		this.mpc = mpc;

	}

	@Override
	protected void handleMutation(Mutation mutation, Integer type) {
		Mutation mAbsolute = new Mutation(mutation.getClassName(),
				mutation.getMethodName(), mutation.getLineNumber(),
				mutation.getMutationForLine(), mutation.getMutationType());
		mAbsolute.setOperatorAddInfo(ABSOLUTE);
		mAbsolute.setAddInfo("Replace with absolute value");
		mpc.addPossibility(mAbsolute);
		CoverageDataUtil.insertCoverageCalls(mv, mAbsolute);
		QueryManager.saveMutation(mAbsolute);
		Long id = mAbsolute.getId();
		Mutation mAbsoluteNeg = new Mutation(mutation.getClassName(),
				mutation.getMethodName(), mutation.getLineNumber(),
				mutation.getMutationForLine(), mutation.getMutationType());
		mAbsoluteNeg.setOperatorAddInfo(ABSOLUTE_NEGATIVE);
		mAbsoluteNeg.setAddInfo("Replace with negative absolute value");
		mAbsoluteNeg.setBaseMutationId(id);
		QueryManager.saveMutation(mAbsoluteNeg);
		if (mAbsoluteNeg.getId() != null) {
			MutationCoverageFile.addDerivedMutation(id, mAbsoluteNeg.getId());
		}
		mpc.addPossibility(mAbsoluteNeg);

		// CoverageDataUtil.insertCoverageCalls(mv, mAbsoluteNeg);

		Mutation mFailOnZero = new Mutation(mutation.getClassName(),
				mutation.getMethodName(), mutation.getLineNumber(),
				mutation.getMutationForLine(), mutation.getMutationType());
		mFailOnZero.setOperatorAddInfo(FAIL_ON_ZERO);
		mFailOnZero.setAddInfo("Fail on zero mutation");
		mFailOnZero.setBaseMutationId(id);
		QueryManager.saveMutation(mFailOnZero);
		if (mFailOnZero.getId() != null) {
			MutationCoverageFile.addDerivedMutation(id, mFailOnZero.getId());
		}
		mpc.addPossibility(mFailOnZero);
		// CoverageDataUtil.insertCoverageCalls(mv, mFailOnZero);

	}

}
