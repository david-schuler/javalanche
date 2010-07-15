package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class AdaptedMutationDescription {

	private final MutationType type;
	private final MutationType subType;
	private final int lineNumber;
	private final int jumpLine;
	private final String className;
	private final int endLine;

	public AdaptedMutationDescription(MutationType type, MutationType subType,
			String className, int lineNumber, int jumpLine) {
		this(type, subType, className, lineNumber, jumpLine, -1);
	}

	public AdaptedMutationDescription(MutationType type, MutationType subType,
			String className, int lineNumber, int jumpLine, int endLine) {
		super();
		this.type = type;
		this.subType = subType;
		this.className = className;
		this.lineNumber = lineNumber;
		this.jumpLine = jumpLine;
		this.endLine = endLine;
	}

	public AdaptedMutationDescription(MutationType type, MutationType subType,
			String className, int line) {
		this(type, subType, className, line, line);
	}


	public MutationType getType() {
		return type;
	}

	public MutationType getSubType() {
		return subType;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getJumpLine() {
		return jumpLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return "AdaptedMutationDescription [className=" + className
				+ ", endLine=" + endLine + ", jumpLine=" + jumpLine
				+ ", lineNumber=" + lineNumber + ", subType=" + subType
				+ ", type=" + type + "]";
	}

}
