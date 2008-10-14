package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

/**
 * Class used to mark mutated statements in the bytecode
 *
 * @author David Schuler
 *
 */
public class MutationMarker {

	private boolean isStart;

	public MutationMarker(boolean isStart) {
		this.isStart = isStart;
	}

	/**
	 * @return the isStart
	 */
	public boolean isStart() {
		return isStart;
	}

}
