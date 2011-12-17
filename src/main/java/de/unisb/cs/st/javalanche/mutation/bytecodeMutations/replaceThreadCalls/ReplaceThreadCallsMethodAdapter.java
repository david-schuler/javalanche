package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public class ReplaceThreadCallsMethodAdapter extends
		AbstractReplaceThreadCallsAdapter {

	private static Logger logger = Logger
			.getLogger(ReplaceThreadCallsMethodAdapter.class);

	private MutationManager mutationManager;

	public ReplaceThreadCallsMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	@Override
	protected void handleMutation(Mutation mutation, final int opcode,
			final String owner, final String name, final String desc) {
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation dbMutation = QueryManager.getMutation(mutation);
			MutationCode unMutated = new MutationCode(null) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(opcode, owner, name, desc);
				}

			};

			String replacement = ThreadCallReplacements.getReplaceMap().get(
					owner + "." + name + "." + desc);
			final String[] replacementParts = replacement.split("\\.");
			MutationCode mutated = new MutationCode(dbMutation) {
				@Override
				public void insertCodeBlock(MethodVisitor mv) {
					mv.visitMethodInsn(opcode, replacementParts[0],
							replacementParts[1], desc);
				}
			};

			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			logger.debug("Not applying mutation");
			mv.visitMethodInsn(opcode, owner, name, desc);
		}
	}

}
