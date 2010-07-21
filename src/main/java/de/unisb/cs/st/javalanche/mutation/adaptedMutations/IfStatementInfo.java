package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.util.List;

public class IfStatementInfo {

	private final int ifStart;
	private final int end;
	private final boolean hasElse;
	private final int elseStart;
	private boolean innerIf;
	private IfStatementInfo(int ifStart, int end, boolean hasElse, int elseStart) {
		super();
		this.ifStart = ifStart;
		this.end = end;
		this.hasElse = hasElse;
		this.elseStart = elseStart;
	}

	public IfStatementInfo(int start, int end) {
		this(start, end, false, -1);
	}

	public IfStatementInfo(int ifStart, int end, int elseStart) {
		this(ifStart, end, true, elseStart);
	}

	public int getStart() {
		return ifStart;
	}

	public int getEnd() {
		return end;
	}

	public int getIfStart() {
		return ifStart;
	}

	public boolean hasElse() {
		return hasElse;
	}

	public int getElseStart() {
		if (!hasElse) {
			return -1;
		}
		return elseStart;
	}

	public int getElseEnd() {
		if (!hasElse) {
			return -1;
		}
		return end;
	}

	public boolean hasInnerIf() {
		return innerIf;
	}

	public void setInnerIf(boolean innerIf) {
		this.innerIf = innerIf;
	}

}
