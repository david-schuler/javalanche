package de.st.cs.unisb.javalanche.bytecodeMutations.negateJumps;

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
