package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

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
