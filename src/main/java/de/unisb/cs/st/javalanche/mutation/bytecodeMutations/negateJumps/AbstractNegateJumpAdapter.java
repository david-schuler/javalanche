package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public abstract class AbstractNegateJumpAdapter extends AbstractMutationAdapter {

	protected static Map<Integer, Integer> jumpReplacementMap = JumpReplacements
			.getReplacementMap();

	public AbstractNegateJumpAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);

	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (mutationCode) {
			mv.visitJumpInsn(opcode, label);
			return;
		}
		if (jumpReplacementMap.containsKey(opcode)) {
			addJumpMutationPossibility(label, opcode);
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}

	private void addJumpMutationPossibility(Label label, int opcode) {
		Mutation mutation = new Mutation(className, getLineNumber(),
				getPossibilityForLine(), Mutation.MutationType.NEGATE_JUMP,
				isClassInit);
		addPossibilityForLine();
		handleMutation(mutation, label, opcode);
	}

	protected abstract void handleMutation(Mutation mutation, Label label,
			int opcode);

}
