/*
 * Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

public class RemoveMethodCallsPossibilityClassAdapter extends ClassAdapter {

	private String className;

	private final MutationPossibilityCollector mpc;

	private final Map<Integer, Integer> possibilityMap;

	public RemoveMethodCallsPossibilityClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector mpc,
			Map<Integer, Integer> possibilityMap) {
		super(cv);
		this.mpc = mpc;
		this.possibilityMap = possibilityMap;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);

		return new RemoveCallsPossibilitiesMethodAdapter(new MyAdviceAdapter(
				mv, access, name, desc), className, name, mpc, possibilityMap,
				desc);
	}
}
