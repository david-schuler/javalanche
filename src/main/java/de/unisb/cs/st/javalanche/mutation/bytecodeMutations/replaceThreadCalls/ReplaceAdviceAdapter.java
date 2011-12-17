package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceThreadCalls;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 
 * @author Vilas Jagannath (vbangal2@illinois.edu)
 * @author Milos Gligoric (milos.gligoric@gmail.com)
 * 
 */
public class ReplaceAdviceAdapter extends AdviceAdapter {

	private boolean superCallSeen;

	public ReplaceAdviceAdapter(MethodVisitor mv, int access, String name,
			String desc) {
		super(mv, access, name, desc);
	}

	@Override
	protected void onMethodEnter() {
		superCallSeen = true;
	}

	@Override
	protected void onMethodExit(int opcode) {
	}

	boolean superCallSeen() {
		return superCallSeen;
	}

}