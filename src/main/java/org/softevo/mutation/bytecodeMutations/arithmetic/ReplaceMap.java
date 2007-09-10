package org.softevo.mutation.bytecodeMutations.arithmetic;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public class ReplaceMap {

//	TODO {Opcodes.LCMP	{Opcodes.INEG,},
//	{Opcodes.LNEG,},
//	{Opcodes.FNEG,},
//	{Opcodes.DNEG, SWAP

	static int[][] replaceArray = new int[][] { { Opcodes.IADD, Opcodes.ISUB },
			{ Opcodes.LADD, Opcodes.LSUB }, { Opcodes.FADD, Opcodes.FSUB },
			{ Opcodes.DADD, Opcodes.DSUB }, { Opcodes.ISUB, Opcodes.IADD },
			{ Opcodes.LSUB, Opcodes.LADD }, { Opcodes.FSUB, Opcodes.FADD },
			{ Opcodes.DSUB, Opcodes.DADD }, { Opcodes.IMUL, Opcodes.IADD },
			{ Opcodes.LMUL, Opcodes.LADD }, { Opcodes.FMUL, Opcodes.FADD },
			{ Opcodes.DMUL, Opcodes.DADD }, { Opcodes.IDIV, Opcodes.IMUL },
			{ Opcodes.LDIV, Opcodes.LMUL }, { Opcodes.FDIV, Opcodes.FMUL },
			{ Opcodes.DDIV, Opcodes.DMUL }, { Opcodes.IREM, Opcodes.IDIV },
			{ Opcodes.LREM, Opcodes.LDIV }, { Opcodes.FREM, Opcodes.FDIV },
			{ Opcodes.DREM, Opcodes.DDIV }, { Opcodes.ISHL, Opcodes.ISHR },
			{ Opcodes.LSHL, Opcodes.LSHR }, { Opcodes.ISHR, Opcodes.ISHL },
			{ Opcodes.LSHR, Opcodes.LSHL }, { Opcodes.IUSHR, Opcodes.ISHR },
			{ Opcodes.LUSHR, Opcodes.LSHR }, { Opcodes.IAND, Opcodes.IOR },
			{ Opcodes.LAND, Opcodes.LOR }, { Opcodes.IOR, Opcodes.IAND },
			{ Opcodes.LOR, Opcodes.LAND }, { Opcodes.IXOR, Opcodes.IOR },
			{ Opcodes.LXOR, Opcodes.LOR }, { Opcodes.FCMPL, Opcodes.FCMPG },
			{ Opcodes.FCMPG, Opcodes.FCMPL }, { Opcodes.DCMPL, Opcodes.DCMPG },
			{ Opcodes.DCMPG, Opcodes.DCMPL } };

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
