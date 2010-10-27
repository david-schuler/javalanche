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
package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public class JumpReplacements {

	private static final int[][] replacements = {
			{ Opcodes.IFEQ, Opcodes.IFNE }, { Opcodes.IFNE, Opcodes.IFEQ },
			{ Opcodes.IFGE, Opcodes.IFLT }, { Opcodes.IFGT, Opcodes.IFLE },
			{ Opcodes.IFLE, Opcodes.IFGT }, { Opcodes.IFLT, Opcodes.IFGE },
			{ Opcodes.IFNULL, Opcodes.IFNONNULL },
			{ Opcodes.IFNONNULL, Opcodes.IFNULL },
			{ Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE },
			{ Opcodes.IF_ACMPNE, Opcodes.IF_ACMPEQ },
			{ Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE },
			{ Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLT },
			{ Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE },
			{ Opcodes.IF_ICMPLE, Opcodes.IF_ICMPGT },
			{ Opcodes.IF_ICMPLT, Opcodes.IF_ICMPGE },
			{ Opcodes.IF_ICMPNE, Opcodes.IF_ICMPEQ } };

	public static Map<Integer, Integer> getReplacementMap() {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < replacements.length; i++) {
			result.put(replacements[i][0], replacements[i][1]);
		}
		return Collections.unmodifiableMap(result);
	}

}
