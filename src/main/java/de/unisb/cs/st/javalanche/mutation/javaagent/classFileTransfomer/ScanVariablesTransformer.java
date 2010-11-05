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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.ProjectVariables;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.VariableScannerAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * 
 */
public class ScanVariablesTransformer implements ClassFileTransformer {

	ProjectVariables p = new ProjectVariables();

	public ScanVariablesTransformer() {
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread() {
			public void run() {
				write();
			}


		});
	}

	private static int count = 0;

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		if (classNameWithDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
			scanClass(className, classfileBuffer);
		}
		return classfileBuffer;
	}

	public void scanClass(String className, byte[] classfileBuffer) {
		ClassReader cr = new ClassReader(classfileBuffer);
		scanClass(className, cr);
	}

	public void scanClass(String className, ClassReader cr) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		VariableScannerAdapter vsa = new VariableScannerAdapter(cw);
		cr.accept(vsa, ClassReader.SKIP_FRAMES);
		p.addClassVariables(className, vsa.getClassVariables());
		p.addStaticVariables(className, vsa.getStaticVariables());
	}

	public void write() {
		p.write();
	}
}
