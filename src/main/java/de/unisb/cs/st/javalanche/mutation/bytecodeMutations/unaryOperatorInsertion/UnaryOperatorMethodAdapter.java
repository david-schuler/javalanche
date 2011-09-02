package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues.AbsoluteValueMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class UnaryOperatorMethodAdapter extends
		AbstractUnaryOperatorMethodAdapater {

	private final MutationManager mutationManager;

	private static final Logger logger = Logger
			.getLogger(UnaryOperatorMethodAdapter.class);

	public UnaryOperatorMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected void handleMutation(Mutation mutation, final Integer type) {
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
			}

		};

		List<MutationCode> mutated = new ArrayList<MutationCode>();
		mutation.setOperatorAddInfo(MINUS);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			final int negOpcode = getOpcode(Opcodes.INEG, type);
			MutationCode mutateMinus = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitInsn(negOpcode);
				}
			};
			mutated.add(mutateMinus);

		}

		mutation.setOperatorAddInfo(BITWISE_NEGATE);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			MutationCode mutateNegate = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					if (type == Opcodes.INTEGER) {
						mv.visitInsn(Opcodes.ICONST_M1);
						mv.visitInsn(Opcodes.IXOR);
					} else if (type == Opcodes.LONG) {
						mv.visitLdcInsn(Long.valueOf(-1l));
						mv.visitInsn(Opcodes.LXOR);
					}

				}
			};

			mutated.add(mutateNegate);
		}

		if (mutated.size() > 0) {
			BytecodeTasks.insertIfElse(mv, unMutated,
					mutated.toArray(new MutationCode[0]));
		} else {
			logger.debug("Not applying mutation");
		}
	}

	private int getOpcode(int opcode, Integer opcodeType) {
		Type type = AbsoluteValueMethodAdapter.getType(opcodeType);
		return type.getOpcode(opcode);
	}
}
