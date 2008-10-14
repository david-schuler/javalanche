package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

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
			Mutation mutationPlus1 = new Mutation(className, getLineNumber(),
					possibilitiesForLine, Mutation.MutationType.RIC_PLUS_1,
					isClassInit);
			Mutation mutationMinus1 = new Mutation(className, getLineNumber(),
					possibilitiesForLine, Mutation.MutationType.RIC_MINUS_1,
					isClassInit);

			addPossibilityForLine();
			mutationPossibilityCollector.addPossibility(mutationPlus1);
			mutationPossibilityCollector.addPossibility(mutationMinus1);
			if (i != 0) {
				Mutation mutationZero = new Mutation(className,
						getLineNumber(), possibilitiesForLine,
						Mutation.MutationType.RIC_ZERO, isClassInit);
				mutationPossibilityCollector.addPossibility(mutationZero);
				if (insertCoverageCalls) {
						CoverageData.insertCoverageCalls(mv, mutationZero);
				}
			}
			if (insertCoverageCalls) {
				CoverageData.insertCoverageCalls(mv, mutationPlus1);
				CoverageData.insertCoverageCalls(mv, mutationMinus1);

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
