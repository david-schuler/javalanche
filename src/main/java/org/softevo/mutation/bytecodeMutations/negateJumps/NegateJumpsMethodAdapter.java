package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.util.HashMap;
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

	private static final int[][] replacements = {
			{ Opcodes.IFEQ, Opcodes.IFNE }, { Opcodes.IFNE, Opcodes.IFEQ },
			{ Opcodes.IFGE, Opcodes.IFLT }, { Opcodes.IFGT, Opcodes.IFLE },
			{ Opcodes.IFLE, Opcodes.IFGT }, { Opcodes.IFLT, Opcodes.IFGE },
			{ Opcodes.IFNULL, Opcodes.IFNONNULL },
			{ Opcodes.IFNONNULL, Opcodes.NULL },
			{ Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE },
			{ Opcodes.IF_ACMPNE, Opcodes.IF_ACMPEQ },
			{ Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE },
			{ Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLT },
			{ Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE },
			{ Opcodes.IF_ICMPLE, Opcodes.IF_ICMPGT },
			{ Opcodes.IF_ICMPLT, Opcodes.IF_ICMPGE },
			{ Opcodes.IF_ICMPNE, Opcodes.IF_ICMPEQ } };

	private static Map<Integer, Integer> jumpReplacmentMap = new HashMap<Integer, Integer>();

	static {
		for (int i = 0; i < replacements.length; i++) {
			jumpReplacmentMap.put(replacements[i][0], replacements[i][1]);
		}
	}

	public NegateJumpsMethodAdapter(MethodVisitor mv, String className,
			String methodName) {
		super(mv, className, methodName);
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
		logger.info("Jump instruction");
		Mutation mutationFromDB = QueryManager.getMutation(queryMutation);
		if (MutationManager.shouldApplyMutation(queryMutation)) {
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(opcode, label);
				}

			};

			MutationCode mutated = new MutationCode(mutationFromDB) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitJumpInsn(jumpReplacmentMap.get(opcode), label);
				}
			};
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		possibilitiesForLine = 0;
	}
}
