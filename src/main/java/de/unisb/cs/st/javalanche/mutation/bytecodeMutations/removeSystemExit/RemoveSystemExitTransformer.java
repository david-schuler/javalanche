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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTransformer;

public class RemoveSystemExitTransformer extends BytecodeTransformer {

	public static class RemoveSystemExitClassAdapter extends ClassAdapter {

		public RemoveSystemExitClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {

			MethodVisitor mv = super.visitMethod(access, name, desc, signature,
					exceptions);
			return new RemoveSystemExitMethodNode(access, signature, signature, signature, exceptions, mv);
		}

	}

	private static Logger logger = Logger
			.getLogger(RemoveSystemExitTransformer.class);


	public RemoveSystemExitTransformer() {
		logger.debug("new MutationScannerTransformer");
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new RemoveSystemExitClassAdapter(cc);
	}
}
