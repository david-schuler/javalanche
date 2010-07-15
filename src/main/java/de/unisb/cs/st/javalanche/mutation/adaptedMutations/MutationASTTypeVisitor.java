package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

public class MutationASTTypeVisitor extends ASTVisitor {

	private static Logger logger = Logger
			.getLogger(MutationASTTypeVisitor.class);

	private final String name;

	private final Document doc;

	private Map<String, ASTParseResult> resultMap = new HashMap<String, ASTParseResult>();

	private String packageName;

	private String compUnitName;

	private int anonymousCount = 1;

	public MutationASTTypeVisitor(Document doc, String name) {
		this.doc = doc;
		this.name = name;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		PackageDeclaration packageDecl = node.getPackage();
		logger.info("Getting package from " + packageDecl + " - " + name);
		if (packageDecl != null) {
			packageName = packageDecl.getName().getFullyQualifiedName();
		} else {
			packageName = "";
		}

		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		String name = node.getName().getFullyQualifiedName();
		if (packageName == null) {
			throw new IllegalStateException();
		}
		String className;
		if (packageName.length() > 1) {
			className = packageName + "." + name;
		} else {
			className = name;
		}
		if (!node.isMemberTypeDeclaration() && !node.isLocalTypeDeclaration()) {
			compUnitName = className;
		}
		if (node.isMemberTypeDeclaration()) {
			className = compUnitName + "$" + name;
		}
		MutationASTVisitor visitor = new MutationASTVisitor(doc, className);
		node.accept(visitor);
		putResult(className, visitor);
		return super.visit(node);
	}

	private ASTParseResult putResult(String className,
			MutationASTVisitor visitor) {
		List<IfStatementInfo> statementInfos = visitor.getIfStatementInfos();
		List<MethodCallInfo> methodCallInfos = visitor.getMethodCallInfos();
		List<MethodInfo> methodInfos = visitor.getMethodInfos();
		List<AssignmentInfo> assignmentInfos = visitor.getAssignmentInfos();
		List<FieldInfo> fieldInfos = visitor.getFieldInfos();
		List<ReturnInfo> returnInfos = visitor.getReturnInfos();
		ASTParseResult res = new ASTParseResult(className, statementInfos,
				methodCallInfos, methodInfos, assignmentInfos, fieldInfos,
				returnInfos);
		resultMap.put(className, res);
		return res;
	}

	public Map<String, ASTParseResult> getParseResults() {
		return resultMap;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		String className = compUnitName + "$" + anonymousCount;
		anonymousCount++;
		MutationASTVisitor visitor = new MutationASTVisitor(doc, className);
		node.accept(visitor);
		putResult(className, visitor);
		return super.visit(node);
	}

}
