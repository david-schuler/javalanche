package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.monitor;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
abstract class AbstractMonitorMethodAdapter extends AbstractMutationAdapter {

	protected static Map<Integer, Integer> replaceMap = MonitorReplacements
			.getReplaceMap();

	public AbstractMonitorMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, possibilities, desc);
	}

	@Override
	public void visitInsn(int opcode) {
		if (opcode == Opcodes.MONITORENTER && !mutationCode) {
			mutate(opcode);
		} else if (opcode == Opcodes.MONITOREXIT && !mutationCode) {
			handleMonitorExit(opcode);
		} else {
			if (opcode == Opcodes.ATHROW) {
				handleAthrow();
			}
			super.visitInsn(opcode);
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	private void mutate(int opcode) {
		Mutation mutation = new Mutation(className, getMethodName(),
				getLineNumber(), getPossibilityForLine(),
				Mutation.MutationType.MONITOR_REMOVE);
		addPossibilityForLine();
		handleMutation(mutation, opcode);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode);

	protected void handleAthrow() {
		// subclassed
	}

	protected void handleMonitorExit(int opcode) {
		super.visitInsn(opcode);
	}

}
