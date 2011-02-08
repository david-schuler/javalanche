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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

public class MutationTransformer extends BytecodeTransformer {


	private MutationManager mm = new MutationManager();

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cv = cw;
		if (MutationProperties.TRACE_BYTECODE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(
					MutationPreMain.sysout));
		}
		return new MutationsClassAdapter(cv, mm);

	}


}
