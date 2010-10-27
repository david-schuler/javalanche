/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class IsTestVisitor extends ClassAdapter {

	private static Logger logger = Logger.getLogger(IsTestVisitor.class);

	private boolean isTest;

	public IsTestVisitor(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		// TODO longer inheritance chains
		if (superName.startsWith("junit/framework")) {
			logger.warn(name + " identified as test class");
			isTest = true;
		} else {
			logger.debug(name + " no test class" + superName);
		}
		super.visit(version, access, name, signature, superName, interfaces);

	}

	public boolean isTest() {
		return isTest;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		if (access == ACC_PUBLIC + ACC_STATIC && "suite".equals(name)
				&& "()Ljunit/framework/Test;".equals(desc)) {
			isTest = true;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}
