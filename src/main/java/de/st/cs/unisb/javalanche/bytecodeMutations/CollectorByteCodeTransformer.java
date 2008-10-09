package de.st.cs.unisb.javalanche.bytecodeMutations;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;
import de.st.cs.unisb.javalanche.mutationPossibilities.MutationPossibilityCollector;

public abstract class CollectorByteCodeTransformer extends BytecodeTransformer {

	protected MutationPossibilityCollector mpc;

	/**
	 * @return the mpc
	 */
	public MutationPossibilityCollector getMpc() {
		return mpc;
	}

	/**
	 * @param mpc
	 *            the mpc to set
	 */
	public void setMpc(MutationPossibilityCollector mpc) {
		this.mpc = mpc;
	}

}
