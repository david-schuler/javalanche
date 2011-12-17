package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public class MonitorRemovePossibilitiesMethodAdapter extends
		AbstractMonitorMethodAdapter {

	private static int numOfMonitors;

	public static int getNumOfMonitors() {
		return numOfMonitors;
	}

	private MutationPossibilityCollector mpc;

	public MonitorRemovePossibilitiesMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, className, possibilities, desc);
		this.mpc = mpc;
	}

	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		if (!mutationCode) {
			if (opcode == Opcodes.MONITOREXIT) {
				numOfMonitors++;
			}
			mpc.addPossibility(mutation);
			if (insertCoverageCalls) {
				CoverageDataUtil.insertCoverageCalls(mv, mutation);
			}
		}
		mv.visitInsn(opcode);
	}

}
