/**
 *
 */
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class MyAdviceAdapter extends AdviceAdapter {

	private static Logger logger = Logger.getLogger(MyAdviceAdapter.class);

	private final String name;

	private boolean superCallSeen;

	public MyAdviceAdapter(MethodVisitor mv, int access, String name,
			String desc) {
		super(mv, access, name, desc);
		this.name = name;
		logger.debug("Method name " + name + " " + this);
	}





	@Override
	protected void onMethodEnter() {
		superCallSeen = true;
		logger.debug("Method name " + name + " " + this);
	}

	@Override
	protected void onMethodExit(int opcode) {
	}

	/**
	 * @return the superCallSeen
	 */
	public boolean isSuperCallSeen() {
		logger.debug(superCallSeen + " - Method name " + name + " " + this);
		return superCallSeen;
	}

	/**
	 * @param superCallSeen
	 *            the superCallSeen to set
	 */
	public void setSuperCallSeen(boolean superCallSeen) {
		this.superCallSeen = superCallSeen;
	}

}