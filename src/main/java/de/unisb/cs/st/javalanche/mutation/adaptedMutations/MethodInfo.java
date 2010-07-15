package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class MethodInfo {

	private final String methodName;
	private final int end;
	private final int start;

	public MethodInfo(String methodName, int start, int end) {
		assert start <= end;
		this.start = start;
		this.end = end;
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	@Override
	public String toString() {
		return "MethodInfo [end=" + end + ", methodName=" + methodName
				+ ", start=" + start + "]";
	}

	public int getEnd() {
		return end;
	}

	public int getStart() {
		return start;
	}

}
