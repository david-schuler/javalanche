package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public abstract class AbstractReplaceThreadCallsAdapter extends
		AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(AbstractReplaceThreadCallsAdapter.class);

	public AbstractReplaceThreadCallsAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
	}

	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		if (!ThreadCallReplacements.getReplaceMap().containsKey(
				owner + "." + name + "." + desc)) {
			mv.visitMethodInsn(opcode, owner, name, desc);
		} else {
			mutate(opcode, owner, name, desc);
		}
	}

	private void mutate(final int opcode, final String owner,
			final String name, final String desc) {
		Mutation queryMutation = new Mutation(className, methodName,
				getLineNumber(), getPossibilityForLine(), REPLACE_THREAD_CALL);
		logger.debug("Found possibility for line " + getLineNumber());
		addPossibilityForLine();
		handleMutation(queryMutation, opcode, owner, name, desc);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode,
			String owner, String name, String desc);

}
