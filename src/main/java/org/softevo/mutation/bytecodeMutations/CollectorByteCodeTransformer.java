package org.softevo.mutation.bytecodeMutations;

import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

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
