/*
* Copyright (C) 2011 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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
			"org/apache/commons/lang/builder/ToStringBuilder",
			"xorg/apache/commons/lang/builder/ReflectionToStringBuilder",
			"xorg/apache/commons/lang/builder/ToStringBuilder");
	
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
