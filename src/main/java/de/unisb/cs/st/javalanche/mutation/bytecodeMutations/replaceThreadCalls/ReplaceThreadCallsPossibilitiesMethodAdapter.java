package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import java.util.Map;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public final class ReplaceThreadCallsPossibilitiesMethodAdapter extends
		AbstractReplaceThreadCallsAdapter {

	private final MutationPossibilityCollector mpc;

	private ReplaceAdviceAdapter myAdviceAdapter;

	public ReplaceThreadCallsPossibilitiesMethodAdapter(ReplaceAdviceAdapter mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
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
