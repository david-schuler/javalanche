package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

public class MethodCallInfo {

	public int line;

	private final String methodName;

	public MethodCallInfo(int line, String methodName) {
		super();
		this.line = line;
		this.methodName = methodName;
	}

	public int getLine() {
		return line;
	}


	public String getMethodName() {
		return methodName;
	}

	@Override
	public String toString() {
		return "MethodCallInfo [line=" + line + ", methodName=" + methodName
				+ "]";
	}

}
