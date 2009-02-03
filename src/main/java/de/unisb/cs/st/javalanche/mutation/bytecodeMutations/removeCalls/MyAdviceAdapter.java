package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Class Adapter that checks if a super call for a constructor has already been
 * seen.
 *
 * @author David Schuler
 *
 */
public class MyAdviceAdapter extends AdviceAdapter {


	private boolean superCallSeen;

	public MyAdviceAdapter(MethodVisitor mv, int access, String name,
			String desc) {
		super(mv, access, name, desc);
		// Preconditions.checkArgument(name.equals("<init>"), "Expect that this
		// method adapter is only used for constructors" );
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		superCallSeen = true;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodExit(int)
	 */
	@Override
	protected void onMethodExit(int opcode) {
	}

	/**
	 * @return true, if the super call for this constructor was already
	 *         processed.
	 */
	boolean isSuperCallSeen() {
		return superCallSeen;
	}

}