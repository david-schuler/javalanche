package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

/**
 * Class used to mark mutated statements in the bytecode. When mutated code is
 * inserted a marker is added at the beginning and the end of the inserted
 * method.
 * 
 * @author David Schuler
 * 
 */
public class MutationMarker {

	/**
	 * Flag that indicates whether this marker marks the start (true) or the end
	 * (false) of a inserted code block.
	 */
	private boolean isStart;

	public MutationMarker(boolean isStart) {
		this.isStart = isStart;
	}

	/**
	 * @return true, if the marker marks the start, else false.
	 */
	public boolean isStart() {
		return isStart;
	}

}
