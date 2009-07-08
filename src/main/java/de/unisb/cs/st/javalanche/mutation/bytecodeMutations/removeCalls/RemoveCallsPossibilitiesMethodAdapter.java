package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.util.Map;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public final class RemoveCallsPossibilitiesMethodAdapter extends
		AbstractRemoveCallsAdapter {

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

	@Override
	protected void handleMutation(Mutation mutation, int opcode, String owner,
			String name, String desc) {
		if (myAdviceAdapter.superCallSeen()) {
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

}
