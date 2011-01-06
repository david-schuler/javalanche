package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class MutationASTVisitor extends ASTVisitor {

	private final String name;

	private final Document doc;

	private Map<IfStatement, IfStatementInfo> ifStatementMap = new HashMap<IfStatement, IfStatementInfo>();
	private List<IfStatementInfo> ifStatementInfos = new ArrayList<IfStatementInfo>();
	private List<MethodCallInfo> methodCallInfos = new ArrayList<MethodCallInfo>();
	private List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
	private final List<AssignmentInfo> assignmentInfos = new ArrayList<AssignmentInfo>();
	private final List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
	private final List<ReturnInfo> returnInfos = new ArrayList<ReturnInfo>();

	private Stack<TryStatement> tryBlocks = new Stack<TryStatement>();
	private int count = 0;

	private List<IfStatement> ifNodes = new ArrayList<IfStatement>();

	public MutationASTVisitor(Document doc, String name) {
		this.doc = doc;
		this.name = name;
	}

	@Override
	public boolean visit(IfStatement node) {
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);

		Statement elseStatement = node.getElseStatement();
		IfStatementInfo ifStatementInfo = null;
		if (elseStatement != null) {
			int elseStart = getStartLineNumber(elseStatement);
			ifStatementInfo = new IfStatementInfo(start, end, elseStart);

		} else {
			ifStatementInfo = new IfStatementInfo(start, end);
		}
		if (tryBlocks.size() > 0) {
			ifStatementInfo.setInTryBlock(true);
		}
		Collection<IfStatementInfo> values = ifStatementMap.values();
		for (IfStatementInfo info : values) {
			info.setInnerIf(true);
		}
		ifStatementMap.put(node, ifStatementInfo);
		ifNodes.add(node);
		ifStatementInfos.add(ifStatementInfo);
		return super.visit(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		ifNodes.remove(node);
		ifStatementMap.remove(node);
		super.endVisit(node);
	}

	private int getStartLineNumber(ASTNode node) {
		int pos = node.getStartPosition();
		int lineOfOffset = 0;
		try {
			lineOfOffset = doc.getLineOfOffset(pos);
		} catch (BadLocationException e) {
			throw new RuntimeException("Node Type " + node.getClass()
					+ "\nNode: " + node, e);
		}
		return lineOfOffset + 1;
	}

	private int getEndLineNumber(ASTNode node) {
		int pos = node.getStartPosition() + node.getLength();
		int lineOfOffset = 0;
		try {
			lineOfOffset = doc.getLineOfOffset(pos);
		} catch (BadLocationException e) {
			throw new RuntimeException("Node Type " + node.getClass()
					+ "\nNode: " + node, e);
		}
		return lineOfOffset + 1;
	}

	public List<IfStatementInfo> getIfStatementInfos() {
		return ifStatementInfos;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		count++;
		return count == 1;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		count++;
		return count == 1;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		int lineNumber = getStartLineNumber(node);
		String methodName = node.getName().toString();
		MethodCallInfo mci = new MethodCallInfo(lineNumber, methodName);
		methodCallInfos.add(mci);
		return super.visit(node);
	}

	public List<MethodCallInfo> getMethodCallInfos() {
		return methodCallInfos;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		int start = getStartLineNumber(node);
		int end = getEndLineNumber(node);
		String methodName = node.getName().toString();
		MethodInfo mci = new MethodInfo(methodName, start, end);
		methodInfos.add(mci);
		return super.visit(node);
	}

	public List<MethodInfo> getMethodInfos() {
		return methodInfos;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		handleAssignment(node);
		return super.visit(node);

	}

	@Override
	public boolean visit(ExpressionStatement node) {
		handleAssignment(node);
		return super.visit(node);
	}

	private void handleAssignment(ASTNode node) {
		int lineNumber = getStartLineNumber(node);
		AssignmentInfo info = new AssignmentInfo(lineNumber);
		assignmentInfos.add(info);
	}

	public List<AssignmentInfo> getAssignmentInfos() {
		return assignmentInfos;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		int line = getStartLineNumber(node);
		FieldInfo fiedInfo = new FieldInfo(line);
		fieldInfos.add(fiedInfo);
		return super.visit(node);
	}

	public List<FieldInfo> getFieldInfos() {
		return fieldInfos;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		int lineNumber = getStartLineNumber(node);
		ReturnInfo returnInfo = new ReturnInfo(lineNumber);
		returnInfos.add(returnInfo);
		return super.visit(node);
	}

	public List<ReturnInfo> getReturnInfos() {
		return returnInfos;
	}

	@Override
	public boolean visit(TryStatement node) {
		tryBlocks.push(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(TryStatement node) {
		TryStatement pop = tryBlocks.pop();
		if (!pop.equals(node)) {
			throw new RuntimeException("Expected other try statement");
		}
		super.endVisit(node);
	}

	@Override
	public boolean visit(BreakStatement node) {
		Collection<IfStatementInfo> values = ifStatementMap.values();

		for (IfStatementInfo info : values) {
			info.setBreak(true);
		}
		return super.visit(node);
	}
}
