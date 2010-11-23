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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;

import static de.unisb.cs.st.javalanche.mutation.properties.TestProperties.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;

public class NegateJumpsPossibilitiesTest {

	@Test
	public void testPossibilities() throws Exception {
		byte[] clasBytes = ADVICE_CLASS.getClassBytes();
		ClassReader cr = new ClassReader(clasBytes);
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		NegateJumpsPossibilitiesClassAdapter njpcv = new NegateJumpsPossibilitiesClassAdapter(
				cw, mpc);
		cr.accept(njpcv, 0);
		assertTrue(mpc.size() > 40);
	}


}
