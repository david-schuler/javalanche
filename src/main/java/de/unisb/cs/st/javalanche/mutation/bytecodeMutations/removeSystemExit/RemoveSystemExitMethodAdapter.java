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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationMarker;

public class RemoveSystemExitMethodAdapter extends MethodAdapter {

	private static Logger logger = Logger
			.getLogger(RemoveSystemExitMethodAdapter.class);

	public RemoveSystemExitMethodAdapter(MethodVisitor mv) {
		super(mv);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		if (name.equals("exita") && owner.equals("java/lang/System")) {
			logger.info("Replacing System.exit ");

			Label mutationStartLabel = new Label();
			mutationStartLabel.info = new MutationMarker(true);
			mv.visitLabel(mutationStartLabel);

			mv.visitInsn(Opcodes.POP);
			mv.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn("Replaced System Exit");
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
					"java/lang/RuntimeException", "<init>",
					"(Ljava/lang/String;)V");
			mv.visitInsn(Opcodes.ATHROW);

			Label mutationEndLabel = new Label();
			mutationEndLabel.info = new MutationMarker(false);
			mv.visitLabel(mutationEndLabel);

		} else {
			super.visitMethodInsn(opcode, owner, name, desc);
		}
	}

}
