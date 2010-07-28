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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public class ReplaceMap {

	static int[][] replaceArray = new int[][] {
			// Add -> Sub
			{ Opcodes.IADD, Opcodes.ISUB },
			{ Opcodes.LADD, Opcodes.LSUB },
			{ Opcodes.FADD, Opcodes.FSUB },
			{ Opcodes.DADD, Opcodes.DSUB },
			// Sub -> Add
			{ Opcodes.ISUB, Opcodes.IADD },
			{ Opcodes.LSUB, Opcodes.LADD },
			{ Opcodes.FSUB, Opcodes.FADD },
			{ Opcodes.DSUB, Opcodes.DADD },
			// Mul -> Add
			{ Opcodes.IMUL, Opcodes.IADD },
			{ Opcodes.LMUL, Opcodes.LADD },
			{ Opcodes.FMUL, Opcodes.FADD },
			{ Opcodes.DMUL, Opcodes.DADD },
			// Div -> Mul
			{ Opcodes.IDIV, Opcodes.IMUL },
			{ Opcodes.LDIV, Opcodes.LMUL },
			{ Opcodes.FDIV, Opcodes.FMUL },
			{ Opcodes.DDIV, Opcodes.DMUL },
			// Rem -> Div
			{ Opcodes.IREM, Opcodes.IDIV },
			{ Opcodes.LREM, Opcodes.LDIV },
			{ Opcodes.FREM, Opcodes.FDIV },
			{ Opcodes.DREM, Opcodes.DDIV },
			// Shift left <-> Shift right
			{ Opcodes.ISHL, Opcodes.ISHR }, { Opcodes.LSHL, Opcodes.LSHR },
			{ Opcodes.ISHR, Opcodes.ISHL },
			{ Opcodes.LSHR, Opcodes.LSHL },
			{ Opcodes.IUSHR, Opcodes.ISHR },
			{ Opcodes.LUSHR, Opcodes.LSHR },
			// And <-> Or
			{ Opcodes.IAND, Opcodes.IOR }, { Opcodes.LAND, Opcodes.LOR },
			{ Opcodes.IOR, Opcodes.IAND }, { Opcodes.LOR, Opcodes.LAND },
			{ Opcodes.IXOR, Opcodes.IOR }, { Opcodes.LXOR, Opcodes.LOR },
			// Negation -> No op
			{ Opcodes.INEG, Opcodes.NOP }, { Opcodes.LNEG, Opcodes.NOP },
			{ Opcodes.FNEG, Opcodes.NOP }, { Opcodes.DNEG, Opcodes.NOP },

	/*
	 * The following operators only differ in their treatment of NoN, and there
	 * would be no source code equivalent for this mutation
	 */
	// , Opcodes.LCMP { Opcodes.FCMPL, Opcodes.FCMPG },
	// { Opcodes.FCMPG, Opcodes.FCMPL }, { Opcodes.DCMPL, Opcodes.DCMPG },
	// { Opcodes.DCMPG, Opcodes.DCMPL }
	};

	private static Map<Integer, Integer> replaceMap;

	private ReplaceMap() {
	}

	public static Map<Integer, Integer> getReplaceMap() {
		if (replaceMap != null) {
			return replaceMap;
		}
		replaceMap = new HashMap<Integer, Integer>();
		for (int[] replace : replaceArray) {
			assert replace.length >= 2;
			replaceMap.put(replace[0], replace[1]);
		}
		return replaceMap;

	}
}
