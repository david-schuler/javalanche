package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class RemoveCallsPossibilitiesMethodAdapter extends
		AbstractMutationAdapter {

	private static Logger logger = Logger
			.getLogger(RemoveCallsPossibilitiesMethodAdapter.class);

	private final MutationPossibilityCollector mpc;
	private MyAdviceAdapter myAdviceAdapter;

	public RemoveCallsPossibilitiesMethodAdapter(MyAdviceAdapter mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
		myAdviceAdapter = mv;
		this.mpc = mpc;
	}

	public void visitMethodInsn(final int opcode, final String owner,
			final String name, final String desc) {
		if (name.equals("<init>")) {
			logger.debug("Ignoring constructor calls");
		} else {
			logger.debug("Found possibility: " + methodName);
			if (myAdviceAdapter.isSuperCallSeen()) {
				logger.debug("Adding possibility");
				Mutation m = new Mutation(className, getLineNumber(),
						getPossibilityForLine(),
						Mutation.MutationType.REMOVE_CALL, isClassInit);
				mpc.addPossibility(m);
				addPossibilityForLine();
				if (insertCoverageCalls) {
					CoverageDataUtil.insertCoverageCalls(mv, m);
				}
			}
		}
		super.visitMethodInsn(opcode, owner, name, desc);
	}

}
