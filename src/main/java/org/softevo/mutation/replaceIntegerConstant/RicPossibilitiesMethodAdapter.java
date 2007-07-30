package org.softevo.mutation.replaceIntegerConstant;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.softevo.mutation.mutationPossibilities.MutationPossibility;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

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
		MutationPossibility mp = new MutationPossibility(className,
				getLineNumber(),
				MutationPossibility.Mutation.REPLACE_INTEGER_CONSTANT);
		mutationPossibilityCollector.addPossibility(mp);
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
		Number number = (Number) constant;
		MutationPossibility mp = new MutationPossibility(className,
				getLineNumber(),
				MutationPossibility.Mutation.REPLACE_INTEGER_CONSTANT);
		mutationPossibilityCollector.addPossibility(mp);
	}

	/**
	 * @return the possibilities
	 */
	public int getPossibilities() {
		return possibilities;
	}


}
