package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class ReturnInfo {

	private final int lineNumber;

	public ReturnInfo(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
