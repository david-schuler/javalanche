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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class PossibilitiesRicMethodAdapter extends AbstractRicMethodAdapter {

	MutationPossibilityCollector mutationPossibilityCollector;

	public PossibilitiesRicMethodAdapter(MethodVisitor mv, String className,
			String methodName,
			MutationPossibilityCollector mutationPossibilityCollector,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, true, possibilities, desc);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
	}

	private void countMutation(int i) {
		if (!mutationCode) {
			int possibilitiesForLine = getPossibilityForLine();

			Mutation mutationPlus1 = new Mutation(className, getMethodName(),
					getLineNumber(), possibilitiesForLine,
					Mutation.MutationType.REPLACE_CONSTANT);
			mutationPlus1.setOperatorAddInfo((i + 1) + "");
			mutationPlus1.setAddInfo("Replace " + i + " with " + (i + 1));
			QueryManager.saveMutation(mutationPlus1);
			Long id = mutationPlus1.getId();
			if (id == null) {
				id = QueryManager.getMutation(mutationPlus1).getId();
			}

			Mutation mutationMinus1 = new Mutation(className, getMethodName(),
					getLineNumber(), possibilitiesForLine,
					Mutation.MutationType.REPLACE_CONSTANT);

			mutationMinus1.setOperatorAddInfo((i - 1) + "");
			mutationMinus1.setAddInfo("Replace " + i + " with " + (i - 1));
			mutationMinus1.setBaseMutationId(id);

			QueryManager.saveMutation(mutationMinus1);
			if (mutationMinus1.getId() != null) {
				MutationCoverageFile.addDerivedMutation(id,
						mutationMinus1.getId());
			}
			addPossibilityForLine();
			mutationPossibilityCollector.addPossibility(mutationPlus1);
			mutationPossibilityCollector.addPossibility(mutationMinus1);
			if (i != 0 && i != 1 && i != -1) {
				Mutation mutationZero = new Mutation(className,
						getMethodName(), getLineNumber(), possibilitiesForLine,
						Mutation.MutationType.REPLACE_CONSTANT);
				mutationZero.setAddInfo("Replace " + i + " with " + 0);
				mutationZero.setBaseMutationId(id);
				mutationZero.setOperatorAddInfo("0");
				QueryManager.saveMutation(mutationZero);
				mutationPossibilityCollector.addPossibility(mutationZero);
				if (mutationZero.getId() != null) {
					MutationCoverageFile.addDerivedMutation(id,
							mutationZero.getId());
				}
				// if (insertCoverageCalls) {
				// CoverageDataUtil.insertCoverageCalls(mv, mutationZero);
				// }
			}
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutationPlus1);
				// CoverageDataUtil.insertCoverageCalls(mv, mutationMinus1);
			}
		}
	}

	@Override
	protected void biOrSiPush(int operand) {
		countMutation(operand);
	}

	@Override
	protected void doubleConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void floatConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void longConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void intConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void ldc(Number constant) {
		countMutation(constant.intValue());
	}

	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

}
