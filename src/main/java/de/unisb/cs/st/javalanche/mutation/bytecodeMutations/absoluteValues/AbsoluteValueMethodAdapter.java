package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class AbsoluteValueMethodAdapter extends AbstractAbsoluteValueAdapter {

	private static final Logger logger = Logger
			.getLogger(AbsoluteValueMethodAdapter.class);

	private MutationManager mutationManager;

	public AbsoluteValueMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected void handleMutation(Mutation mutation, Integer typeOpcode) {
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
			}

		};

		List<MutationCode> mutated = new ArrayList<MutationCode>();
		mutation.setOperatorAddInfo(ABSOLUTE);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			final String signature = getSignature(typeOpcode);
			MutationCode mutatedAbsolute = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs",
							signature);
				}
			};
			mutated.add(mutatedAbsolute);
		}
		mutation.setOperatorAddInfo(ABSOLUTE_NEGATIVE);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			final String signature = getSignature(typeOpcode);
			MutationCode mutatedMinusAbsolut = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(INVOKESTATIC,
							"java/lang/CoverageDataRuntime", "absMinus1",
							signature);
				}
			};
			mutated.add(mutatedMinusAbsolut);
		}
		mutation.setOperatorAddInfo(FAIL_ON_ZERO);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			final String signature = getSignature(typeOpcode);
			MutationCode mutatedFailOnZero = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(INVOKESTATIC,
							"java/lang/CoverageDataRuntime", "failOnZero",
							signature);
				}
			};
			mutated.add(mutatedFailOnZero);
		}

		if (mutated.size() > 0) {
			BytecodeTasks.insertIfElse(mv, unMutated,
					mutated.toArray(new MutationCode[0]));
		} else {
			logger.debug("Not applying mutations for base mutation " + mutation);
		}
	}

	public static Type getType(Integer type) {
		if (type == INTEGER) {
			return Type.INT_TYPE;
		}
		if (type == LONG) {
			return Type.LONG_TYPE;
		}
		if (type == FLOAT) {
			return Type.FLOAT_TYPE;
		}
		if (type == DOUBLE) {
			return Type.DOUBLE_TYPE;
		}
		throw new IllegalArgumentException("Unexpected argument for type: "
				+ type);
	}

	private String getSignature(Integer type) {
		if (type == INTEGER) {
			return "(I)I";
		}
		if (type == LONG) {
			return "(J)J";
		}
		if (type == FLOAT) {
			return "(F)F";
		}
		if (type == DOUBLE) {
			return "(D)D";
		}
		throw new IllegalArgumentException("Unexpected argument for type: "
				+ type);
	}

}
