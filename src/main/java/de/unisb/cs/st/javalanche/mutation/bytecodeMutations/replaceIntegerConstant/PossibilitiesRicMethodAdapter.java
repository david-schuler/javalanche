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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class PossibilitiesRicMethodAdapter extends AbstractRicMethodAdapter {

	MutationPossibilityCollector mutationPossibilityCollector;

	public PossibilitiesRicMethodAdapter(MethodVisitor mv, String className,
			String methodName,
			MutationPossibilityCollector mutationPossibilityCollector,
			Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, true, possibilities);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
	}

	private void countMutation(int i) {
		if (!mutationCode) {
			int possibilitiesForLine = getPossibilityForLine();
			Mutation mutationPlus1 = new Mutation(className, methodName,
					getLineNumber(), possibilitiesForLine,
					Mutation.MutationType.RIC_PLUS_1, isClassInit);
			Mutation mutationMinus1 = new Mutation(className, methodName,
					getLineNumber(), possibilitiesForLine,
					Mutation.MutationType.RIC_MINUS_1, isClassInit);

			addPossibilityForLine();
			mutationPossibilityCollector.addPossibility(mutationPlus1);
			mutationPossibilityCollector.addPossibility(mutationMinus1);
			if (i != 0) {
				Mutation mutationZero = new Mutation(className, methodName,
						getLineNumber(), possibilitiesForLine,
						Mutation.MutationType.RIC_ZERO, isClassInit);
				mutationPossibilityCollector.addPossibility(mutationZero);
				if (insertCoverageCalls) {
					CoverageDataUtil.insertCoverageCalls(mv, mutationZero);
				}
			}
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutationPlus1);
				CoverageDataUtil.insertCoverageCalls(mv, mutationMinus1);

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
