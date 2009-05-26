package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

/**
 * MethodAdapter to find possible locations to aplly a mutation that replaces an
 * arithmetic operator.
 * 
 */
public class PossibilitiesArithmeticReplaceMethodAdapter extends
		AbstractArithmeticMethodAdapter {

	private MutationPossibilityCollector mpc;

	public PossibilitiesArithmeticReplaceMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities) {
		super(mv, className, className, possibilities);
		this.mpc = mpc;
	}

	/**
	 * Adds a mutation posibility to the {@link MutationPossibilityCollector}.
	 */
	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		if (!mutationCode) {
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
		mv.visitInsn(opcode);
	}
}
