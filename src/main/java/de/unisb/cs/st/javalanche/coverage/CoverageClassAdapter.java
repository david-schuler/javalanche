package de.unisb.cs.st.javalanche.coverage;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;

/**
 * @author Bernhard Gruen
 * 
 */

public class CoverageClassAdapter extends ClassAdapter {

	private static final List<String> EXCLUDES = Arrays.asList(
			"org/apache/commons/lang/builder/ReflectionToStringBuilder",
			"org/apache/commons/lang/builder/ToStringBuilder");
	
	private String className;
	private int classAccess;

	private Excludes e = Excludes.getTestExcludesInstance();
	
	private boolean exclude;

	public CoverageClassAdapter(ClassVisitor visitor, String className) {
		super(visitor);
		this.className = className;
		String classNameWithDots = className.replace('/', '.');
		if (e.shouldExclude(classNameWithDots) || EXCLUDES.contains(className)) {
			exclude = true;
		} else {
			exclude = false;
		}
	}

	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.classAccess = access;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String[])
	 */
	public MethodVisitor visitMethod(int methodAccess, String name,
			String descriptor, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(methodAccess, name, descriptor,
				signature, exceptions);
		if (!exclude) {
			mv = new CoverageMethodAdapter(mv, className, name, descriptor,
					classAccess, methodAccess);
		}
		return mv;
	}

}
