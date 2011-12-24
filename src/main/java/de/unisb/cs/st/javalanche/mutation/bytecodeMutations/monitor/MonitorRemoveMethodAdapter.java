package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
public class MonitorRemoveMethodAdapter extends AbstractMonitorMethodAdapter {

	private static LinkedList<Long> monitorEnterMutantIds = new LinkedList<Long>();

	private static boolean hasMonitorExitSeen;

	private MutationManager mutationManager;

	private Mutation actualMutation;

	private boolean hasMonitorEnterSeen;

	public MonitorRemoveMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities,
			MutationManager mutationManager, String desc) {
		super(mv, className, methodName, possibilities, desc);
		this.mutationManager = mutationManager;
	}

	private static Logger logger = Logger
			.getLogger(MonitorRemoveMethodAdapter.class);

	private static class SingleInsnMutationCode extends MutationCode {

		private int opc;

		public SingleInsnMutationCode(Mutation mutation, int opcode) {
			super(mutation);
			this.opc = opcode;
		}

		@Override
		public void insertCodeBlock(MethodVisitor mv) {
			mv.visitInsn(opc);
		}
	}

	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		logger.debug("Querying mutation " + mutation);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation mutationFromDB = QueryManager.getMutation(mutation);
			actualMutation = mutationFromDB;
			hasMonitorEnterSeen = true;
			if (opcode == Opcodes.MONITORENTER) {
				monitorEnterMutantIds.addLast(mutationFromDB.getId());
			}
			MutationCode unMutated = new SingleInsnMutationCode(null, opcode);
			MutationCode mutated = new SingleInsnMutationCode(mutationFromDB,
					replaceMap.get(opcode));
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			mv.visitInsn(opcode);
		}
	}

	@Override
	protected void handleMonitorExit(int opcode) {
		if (opcode == Opcodes.MONITOREXIT && hasMonitorEnterSeen) {
			assert actualMutation != null;
			hasMonitorExitSeen = true;
			MutationCode unMutated = new SingleInsnMutationCode(null, opcode);
			MutationCode mutated = new SingleInsnMutationCode(actualMutation,
					replaceMap.get(opcode));
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		}else{
			super.handleMonitorExit(opcode);
		}
	}

	@Override
	protected void handleAthrow() {
		if (hasMonitorExitSeen) {
			monitorEnterMutantIds.removeLast();
			hasMonitorExitSeen = false;
			hasMonitorEnterSeen = false;
		}
	}

}