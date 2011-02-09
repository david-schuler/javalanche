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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.integrateSuite;

import static org.objectweb.asm.Opcodes.*;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IntegrateTestSuiteMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger
			.getLogger(IntegrateTestSuiteMethodAdapter.class);

	private String targetClass;

	private String integrationMethod;

	private String integrationMethodSignature;

	public IntegrateTestSuiteMethodAdapter(MethodVisitor mv,
			String targetClass, String integrationMethod,
			String integrationMethodSignature) {
		super(mv);
		this.targetClass = targetClass;
		this.integrationMethod = integrationMethod;
		this.integrationMethodSignature = integrationMethodSignature;
	}

	@Override
	public void visitInsn(int opcode) {
		if (opcode == Opcodes.ARETURN) {
			logger.info("Integrating Testsuite+ " + targetClass  + "    "  + integrationMethod);
			mv.visitMethodInsn(INVOKESTATIC, targetClass, integrationMethod,
					integrationMethodSignature);

		}

		mv.visitInsn(opcode);
	}

}
