package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;

public class PossibilitiesRicMethodAdapter extends AbstractRicMethodAdapter {

	MutationPossibilityCollector mutationPossibilityCollector;

	public PossibilitiesRicMethodAdapter(MethodVisitor mv, String className,
			String methodName,
			MutationPossibilityCollector mutationPossibilityCollector,
			Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, true, possibilities);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
	}

	private void countMutation() {
		if (!mutationCode) {
			int possibilitiesForLine = getPossibilityForLine();
			Mutation mutationPlus1 = new Mutation(className, getLineNumber(),
					possibilitiesForLine, Mutation.MutationType.RIC_PLUS_1);
			Mutation mutationMinus1 = new Mutation(className, getLineNumber(),
					possibilitiesForLine, Mutation.MutationType.RIC_MINUS_1);
			Mutation mutationZero = new Mutation(className, getLineNumber(),
					possibilitiesForLine, Mutation.MutationType.RIC_ZERO);
			addPossibilityForLine();
			mutationPossibilityCollector.addPossibility(mutationPlus1);
			mutationPossibilityCollector.addPossibility(mutationMinus1);
			mutationPossibilityCollector.addPossibility(mutationZero);
			if (insertCoverageCalls) {
				CoverageData.insertCoverageCalls(mv, mutationPlus1);
				CoverageData.insertCoverageCalls(mv, mutationMinus1);
				CoverageData.insertCoverageCalls(mv, mutationZero);
			}
		}
	}

	@Override
	protected void biOrSiPush(int operand) {
		countMutation();
	}

	@Override
	protected void doubleConstant(int i) {
		countMutation();
	}

	@Override
	protected void floatConstant(int i) {
		countMutation();
	}

	@Override
	protected void longConstant(int i) {
		countMutation();
	}

	@Override
	protected void intConstant(int i) {
		countMutation();
	}

	@Override
	protected void ldc(Object constant) {
		countMutation();
	}

	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

}
