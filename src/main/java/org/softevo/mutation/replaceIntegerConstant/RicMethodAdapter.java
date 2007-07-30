package org.softevo.mutation.replaceIntegerConstant;

import java.util.logging.Logger;

import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class RicMethodAdapter extends AbstractRicMethodAdapter {

	Logger logger = Logger.getLogger(RicMethodAdapter.class.getName());

	Mutations mutationsToApply;

	public RicMethodAdapter(MethodVisitor mv, String className,
			String methodName, Mutations mutationsToApply) {
		super(mv, className, methodName, false);
		this.mutationsToApply = mutationsToApply;
	}

	@Override
	protected void biOrSiPush(int operand) {
		super.visitLdcInsn(operand);
	}

	@Override
	protected void doubleConstant(int i) {
		super.visitLdcInsn(i);
	}

	@Override
	protected void floatConstant(int i) {
		super.visitLdcInsn(i);
	}

	@Override
	protected void intConstant(int i) {
		if (shouldApplyMutation()) {
			logger.info("Applying mutation constant + 1 in line "
					+ getLineNumber());
			super.visitLdcInsn(i + 1);
		}else{
			super.visitLdcInsn(i);
		}
	}

	private boolean shouldApplyMutation() {
		if (mutationsToApply.contains(className, getLineNumber())) {
			return true;
		}
		return false;

	}

	@Override
	protected void ldc(Object constant) {
		super.visitLdcInsn(constant);
	}

	@Override
	protected void longConstant(int i) {
		super.visitLdcInsn(i);
	}

}
