package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class NegateJumpsPossibilitiesMethodAdapter extends
		AbstractNegateJumpsAdapter {

	private MutationPossibilityCollector mpc;

	public NegateJumpsPossibilitiesMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
		this.mpc = mpc;
	}

	@Override
	protected void handleMutation(Mutation mutation, Label label, int opcode) {
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
			mv.visitJumpInsn(opcode, label);
	}

}
