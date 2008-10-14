package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.mutationCoverage.CoverageData;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class PossibilitiesArithmeticReplaceMethodAdapter extends
		AbstractMutationAdapter {

	private static Map<Integer, Integer> replaceMap = ReplaceMap
			.getReplaceMap();

	private MutationPossibilityCollector mpc;

	public PossibilitiesArithmeticReplaceMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			MutationPossibilityCollector mpc, Map<Integer, Integer> possibilities) {
		super(mv, className, className, possibilities);
		this.mpc = mpc;
	}

	@Override
	public void visitInsn(int opcode) {
		if (replaceMap.containsKey(opcode)) {
			addPossibility();
		}
		super.visitInsn(opcode);
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

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	private void addPossibility() {
		if (!mutationCode) {
			Mutation mutation = new Mutation(className, getLineNumber(),
					getPossibilityForLine(),
					Mutation.MutationType.ARITHMETIC_REPLACE,isClassInit);
			mpc.addPossibility(mutation);
			addPossibilityForLine();
			if (insertCoverageCalls) {
				CoverageData.insertCoverageCalls(mv, mutation);
			}
		}
	}
}
