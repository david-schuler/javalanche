/*
 ** Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import java.util.ArrayList;

public class VariableScannerAdapter extends ClassAdapter {

	private String className;

	private List<VariableInfo> staticVariables = new ArrayList<VariableInfo>();

	private List<VariableInfo> classVariables = new ArrayList<VariableInfo>();

	public VariableScannerAdapter(ClassVisitor cv) {
		super(cv);

	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if ((access & Opcodes.ACC_STATIC) != 0) {
			staticVariables.add(new VariableInfo(name, desc));
		} else {
			classVariables.add(new VariableInfo(name, desc));
		}
		return super.visitField(access, name, desc, signature, value);
	}

	public String getClassName() {
		return className;
	}

	public List<VariableInfo> getStaticVariables() {
		return staticVariables;
	}

	public List<VariableInfo> getClassVariables() {
		return classVariables;
	}
}
