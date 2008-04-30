package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.softevo.mutation.bytecodeMutations.BytecodeTasks;
import org.softevo.mutation.bytecodeMutations.AbstractMutationAdapter;
import org.softevo.mutation.bytecodeMutations.MutationCode;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.MutationManager;
import org.softevo.mutation.results.persistence.QueryManager;

public class NegateJumpsMethodAdapter extends AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(AbstractMutationAdapter.class);

	private int possibilitiesForLine = 0;

	private static Map<Integer, Integer> jumpReplacmentMap = JumpReplacements.getReplacementMap();


	public NegateJumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		if (mutationCode) {
			super.visitJumpInsn(opcode, label);
			return;
		}
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
			insertMutationJump(opcode, label);
			break;
		default:
			super.visitJumpInsn(opcode, label);
			break;
		}
	}

	private void insertMutationJump(final int opcode, final Label label) {
		Mutation queryMutation = new Mutation(className, getLineNumber(),
				possibilitiesForLine, Mutation.MutationType.NEGATE_JUMP);
		possibilitiesForLine++;
		logger.info("Jump instruction in line: " + getLineNumber());
		if (MutationManager.shouldApplyMutation(queryMutation)) {
			logger.info("Applying mutation for line: " + getLineNumber());
			Mutation mutationFromDB = QueryManager.getMutation(queryMutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}

			};

			MutationCode mutated = new MutationCode(mutationFromDB) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (jumpReplacmentMap.containsKey(opcode)) {
						int insertOpcode = jumpReplacmentMap.get(opcode);
						mv.visitJumpInsn(insertOpcode, label);
					} else {
						throw new RuntimeException(
								"Invalid opcode key for jump Map");
					}
				}
			};
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			mv.visitJumpInsn(opcode, label);
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		possibilitiesForLine = 0;
	}
}
