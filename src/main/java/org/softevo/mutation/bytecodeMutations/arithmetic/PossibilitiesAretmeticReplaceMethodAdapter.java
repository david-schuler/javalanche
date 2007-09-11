package org.softevo.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.softevo.mutation.bytecodeMutations.AbstractMutationAdapter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;

public class PossibilitiesAretmeticReplaceMethodAdapter extends
		AbstractMutationAdapter {

	private static Map replaceMap = ReplaceMap.getReplaceMap();

	private int possForLine;

	private MutationPossibilityCollector mpc;

	public PossibilitiesAretmeticReplaceMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc) {
		super(mv, className, className);
		this.mpc = mpc;
	}

	@Override
	public void visitInsn(int opcode) {
		super.visitInsn(opcode);
		if (replaceMap.containsKey(opcode)) {
			addPossibility();
		}
		// case Opcodes.SWAP: // TODO
		// break;
		// case Opcodes.IADD:
		// case Opcodes.LADD:
		// case Opcodes.FADD:
		// case Opcodes.DADD:
		// add(opcode);
		// break;
		// case Opcodes.ISUB:
		// case Opcodes.LSUB:
		// case Opcodes.FSUB:
		// case Opcodes.DSUB:
		// sub(opcode);
		// break;
		// case Opcodes.IMUL:
		// case Opcodes.LMUL:
		// case Opcodes.FMUL:
		// case Opcodes.DMUL:
		// mul(opcode);
		// break;
		// case Opcodes.IDIV:
		// case Opcodes.LDIV:
		// case Opcodes.FDIV:
		// case Opcodes.DDIV:
		// div(opcode);
		// break;
		// case Opcodes.IREM:
		// case Opcodes.LREM:
		// case Opcodes.FREM:
		// case Opcodes.DREM:
		// rem(opcode);
		// break;
		// case Opcodes.INEG:
		// case Opcodes.LNEG:
		// case Opcodes.FNEG:
		// case Opcodes.DNEG:
		// neg(opcode);
		// break;
		// case Opcodes.ISHL:
		// case Opcodes.LSHL:
		// shl(opcode);
		// break;
		// case Opcodes.ISHR:
		// case Opcodes.LSHR:
		// shr(opcode);
		// break;
		// case Opcodes.IUSHR:
		// case Opcodes.LUSHR:
		// uhr(opcode);
		// break;
		// case Opcodes.IAND:
		// case Opcodes.LAND:
		// and(opcode);
		// break;
		// case Opcodes.IOR:
		// case Opcodes.LOR:
		// or(opcode);
		// break;
		// case Opcodes.IXOR:
		// case Opcodes.LXOR:
		// xor(opcode);
		// break;
		// case Opcodes.LCMP:
		// case Opcodes.FCMPL:
		// case Opcodes.FCMPG:
		// case Opcodes.DCMPL:
		// case Opcodes.DCMPG:
		// cmp(opcode);
		// break;
		// default:
		// break;
		// }

	}

	private void addPossibility() {
		Mutation m = new Mutation(className, getLineNumber(), possForLine,
				Mutation.MutationType.ARITHMETIC_REPLACE);
		mpc.addPossibility(m);
		possForLine++;
	}
}
