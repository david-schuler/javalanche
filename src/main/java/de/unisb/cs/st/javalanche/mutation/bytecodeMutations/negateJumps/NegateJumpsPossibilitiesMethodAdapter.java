package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class NegateJumpsPossibilitiesMethodAdapter extends
		AbstractMutationAdapter {


	private MutationPossibilityCollector mpc;

	private static Map<Integer, Integer> jumpReplacementMap = JumpReplacements
			.getReplacementMap();

	public NegateJumpsPossibilitiesMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName,possibilities);
		this.mpc = mpc;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (mutationCode) {
			super.visitJumpInsn(opcode, label);
			return;
		}
		if (jumpReplacementMap.containsKey(opcode)) {
			addJumpMutationPossibility();
		}
		super.visitJumpInsn(opcode, label);
	}

	private void addJumpMutationPossibility() {
		if (!mutationCode) {
			Mutation mutation = new Mutation(className, getLineNumber(),
					getPossibilityForLine(), Mutation.MutationType.NEGATE_JUMP,isClassInit);
			addPossibilityForLine();
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

}
