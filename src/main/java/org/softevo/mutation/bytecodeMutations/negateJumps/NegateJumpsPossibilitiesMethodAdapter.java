package org.softevo.mutation.bytecodeMutations.negateJumps;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.bytecodeMutations.LineNumberAdapter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;

public class NegateJumpsPossibilitiesMethodAdapter extends LineNumberAdapter {

	private int possibilitiesForLine = 0;

	private MutationPossibilityCollector mpc;

	public NegateJumpsPossibilitiesMethodAdapter(MethodVisitor mv,String className, String methodName, MutationPossibilityCollector mpc) {
		super(mv,className, methodName);
		this.mpc = mpc;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		switch (opcode) {
		case Opcodes.IFEQ:


		case Opcodes.IFNE:

		case Opcodes.IFGE:

		case Opcodes.IFGT:

		case Opcodes.IFLE:

		case Opcodes.IFLT:

		case Opcodes.IFNULL:

		case Opcodes.IFNONNULL:

		case Opcodes.IF_ACMPEQ:

		case Opcodes.IF_ACMPNE:

		case Opcodes.IF_ICMPEQ:

		case Opcodes.IF_ICMPGE:

		case Opcodes.IF_ICMPGT:

		case Opcodes.IF_ICMPLE:

		case Opcodes.IF_ICMPLT:

		case Opcodes.IF_ICMPNE:
			negateJumpMutation();
		default:
			break;
		}
	}

	private void negateJumpMutation() {
		Mutation mutation = new Mutation(className, getLineNumber(),
				possibilitiesForLine, Mutation.MutationType.NEGATE_JUMP);
		possibilitiesForLine++;
		mpc.addPossibility(mutation);
	}


	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		possibilitiesForLine = 0;
	}

}


