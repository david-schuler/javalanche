package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.bytecodeMutations.AbstractMutationAdapter;
import org.softevo.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;

public class NegateJumpsPossibilitiesMethodAdapter extends
		AbstractMutationAdapter {

	private int possibilitiesForLine = 0;

	private MutationPossibilityCollector mpc;

	private static Map<Integer, Integer> jumpReplacementMap = JumpReplacements
			.getReplacementMap();

	public NegateJumpsPossibilitiesMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc) {
		super(mv, className, methodName);
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
		Mutation mutation = new Mutation(className, getLineNumber(),
				possibilitiesForLine, Mutation.MutationType.NEGATE_JUMP);
		possibilitiesForLine++;
		mpc.addPossibility(mutation);
		CoverageData.insertCoverageCalls(mv, mutation);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		possibilitiesForLine = 0;
	}

}
