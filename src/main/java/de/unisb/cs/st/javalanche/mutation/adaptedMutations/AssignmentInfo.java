package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class AssignmentInfo {
	private final int lineNumber;

	public AssignmentInfo(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
