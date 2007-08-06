package org.softevo.mutation.replaceIntegerConstant;

import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;

public class RicPossibilitiesMethodAdapter extends AbstractRicMethodAdapter {


	private int possibilities = 0;

	MutationPossibilityCollector mutationPossibilityCollector;

	public RicPossibilitiesMethodAdapter(MethodVisitor mv, String className,
			String methodName,
			MutationPossibilityCollector mutationPossibilityCollector) {
		super(mv, className, methodName,true);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
	}


	private void countMutation() {
		Mutation mutation = new Mutation(className,
				getLineNumber(),
				Mutation.MutationType.REPLACE_INTEGER_CONSTANT);
		mutationPossibilityCollector.addPossibility(mutation);
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
		Mutation mp = new Mutation(className,
				getLineNumber(),
				Mutation.MutationType.REPLACE_INTEGER_CONSTANT);
		mutationPossibilityCollector.addPossibility(mp);
	}

	/**
	 * @return the possibilities
	 */
	public int getPossibilities() {
		return possibilities;
	}


}
