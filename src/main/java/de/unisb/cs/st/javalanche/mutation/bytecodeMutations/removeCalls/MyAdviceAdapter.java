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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Class Adapter that checks if a super call for a constructor has already been
 * seen.
 *
 * @author David Schuler
 *
 */
public class MyAdviceAdapter extends AdviceAdapter {


	private boolean superCallSeen;

	public MyAdviceAdapter(MethodVisitor mv, int access, String name,
			String desc) {
		super(mv, access, name, desc);
		// Preconditions.checkArgument(name.equals("<init>"), "Expect that this
		// method adapter is only used for constructors" );
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodEnter()
	 */
	@Override
	protected void onMethodEnter() {
		superCallSeen = true;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.commons.AdviceAdapter#onMethodExit(int)
	 */
	@Override
	protected void onMethodExit(int opcode) {
	}

	/**
	 * @return true, if the super call for this constructor was already
	 *         processed.
	 */
	boolean superCallSeen() {
		return superCallSeen;
	}

}
