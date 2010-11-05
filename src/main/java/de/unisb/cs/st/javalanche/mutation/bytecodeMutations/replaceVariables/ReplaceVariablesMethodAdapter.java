package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public final class ReplaceVariablesMethodAdapter extends
		AbstractReplaceVariablesAdapter {

	private static final Logger logger = Logger
			.getLogger(ReplaceVariablesMethodAdapter.class);
	private MutationManager mutationManager;

	public ReplaceVariablesMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			String desc, MutationManager mutationManager,
			List<VariableInfo> staticVariables,
			List<VariableInfo> classVariables) {
		super(mv, className, methodName, possibilities, desc, staticVariables,
				classVariables);
		this.mutationManager = mutationManager;
	}

	@Override
	protected boolean handleMutation(Mutation m, final int opcode,
			final String owner, final String name, final String desc,
			final String[] replaceNames) {

		int mforLine = m.getMutationForLine();
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
				mv.visitFieldInsn(opcode, owner, name, desc);
			}

		};
		List<MutationCode> mutated = new ArrayList<MutationCode>();
		for (final String replaceName : replaceNames) {
			Mutation m2 = new Mutation(m.getClassName(), m.getMethodName(),
					m.getLineNumber(), mforLine, m.getMutationType());
			mforLine++;
			if (mutationManager.shouldApplyMutation(m2)) {
				logger.warn("Mutation applied");
				Mutation dbMutation = QueryManager.getMutation(m2);
				MutationCode mutatedC = new MutationCode(dbMutation) {
					@Override
					public void insertCodeBlock(MethodVisitor mv) {
						mv.visitFieldInsn(opcode, owner, replaceName, desc);
					}
				};
				mutated.add(mutatedC);
			}
		}
		if (mutated.size() > 0) {
			BytecodeTasks.insertIfElse(mv, unMutated,
					mutated.toArray(new MutationCode[0]));
		} else {
			logger.debug("Not applying mutation");
			mv.visitFieldInsn(opcode, owner, name, desc);
		}
		return false;
	}

	@Override
	protected boolean handleLocalMutation(Mutation m, final int opcode,
			final int var, List<Integer> replaceLocals) {
		int mforLine = m.getMutationForLine();
		MutationCode unMutated = new MutationCode(null) {
			@Override
			public void insertCodeBlock(MethodVisitor mv) {
				mv.visitVarInsn(opcode, var);
			}
		};

		List<MutationCode> mutated = new ArrayList<MutationCode>();
		for (final Integer replaceLocal : replaceLocals) {

			Mutation m2 = new Mutation(m.getClassName(), m.getMethodName(),
					m.getLineNumber(), mforLine, m.getMutationType());
			mforLine++;
			if (mutationManager.shouldApplyMutation(m2)) {
				logger.warn("Mutation applied");
				Mutation dbMutation = QueryManager.getMutation(m2);
				MutationCode mutatedC = new MutationCode(dbMutation) {
					@Override
					public void insertCodeBlock(MethodVisitor mv) {
						mv.visitVarInsn(opcode, replaceLocal);
					}
				};
				mutated.add(mutatedC);
			}
		}
		if (mutated.size() > 0) {
			BytecodeTasks.insertIfElse(mv, unMutated,
					mutated.toArray(new MutationCode[0]));
		} else {
			logger.debug("Not applying mutation");
			mv.visitVarInsn(opcode, var);
		}
		return false;
	}
}