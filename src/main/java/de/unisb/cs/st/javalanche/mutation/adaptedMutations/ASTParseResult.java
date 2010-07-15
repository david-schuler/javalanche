package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.util.List;

public class ASTParseResult {

	private final List<IfStatementInfo> ifStatementInfos;

	private final String className;

	private final List<MethodCallInfo> methodCallInfos;

	private final List<MethodInfo> methodInfos;

	private final List<AssignmentInfo> assignmentInfos;

	private final List<FieldInfo> fieldInfos;

	private final List<ReturnInfo> returnInfos;

	public ASTParseResult(String className,
			List<IfStatementInfo> ifStatementInfos,
			List<MethodCallInfo> methodCallInfos, List<MethodInfo> methodInfos,
			List<AssignmentInfo> assignmentInfos, List<FieldInfo> fieldInfos,
			List<ReturnInfo> returnInfos) {
		this.className = className;
		this.ifStatementInfos = ifStatementInfos;
		this.methodCallInfos = methodCallInfos;
		this.methodInfos = methodInfos;
		this.assignmentInfos = assignmentInfos;
		this.fieldInfos = fieldInfos;
		this.returnInfos = returnInfos;
	}

	public List<IfStatementInfo> getIfStatementInfos() {
		return ifStatementInfos;
	}

	public String getClassName() {
		return className;
	}

	public List<MethodCallInfo> getMethodCallInfos() {
		return methodCallInfos;
	}

	public List<MethodInfo> getMethodInfos() {
		return methodInfos;
	}

	public List<AssignmentInfo> getAssignmentInfos() {
		return assignmentInfos;
	}

	public List<FieldInfo> getFieldInfos() {
		return fieldInfos;
	}

	public List<ReturnInfo> getReturnInfos() {
		return returnInfos;
	}

}
