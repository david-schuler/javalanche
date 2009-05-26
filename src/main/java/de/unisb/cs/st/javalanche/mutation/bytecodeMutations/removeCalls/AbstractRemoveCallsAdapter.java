package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public abstract class AbstractRemoveCallsAdapter extends AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(AbstractRemoveCallsAdapter.class);

	public AbstractRemoveCallsAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	// TODO Ignore StringBuilder StringBuffer?
	@Override
	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		if (mutationCode || name.equals("<init>")) {
			mv.visitMethodInsn(opcode, owner, name, desc);
		} else {
			mutate(opcode, owner, name, desc);
		}

	}

	private void mutate(final int opcode, final String owner,
			final String name, final String desc) {
		Mutation queryMutation = new Mutation(className, getLineNumber(),
				getPossibilityForLine(), REMOVE_CALL, isClassInit);
		logger.debug("Found possibility for line " + getLineNumber());
		addPossibilityForLine();
		handleMutation(queryMutation, opcode, owner, name, desc);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode,
			String owner, String name, String desc);

}
