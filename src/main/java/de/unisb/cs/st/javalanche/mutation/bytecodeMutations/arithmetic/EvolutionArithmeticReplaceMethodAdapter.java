package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.evolutionary.StepInfo;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class EvolutionArithmeticReplaceMethodAdapter extends
		ArithmeticReplaceMethodAdapter {

	private static Logger logger = Logger
			.getLogger(EvolutionArithmeticReplaceMethodAdapter.class);

	StepInfo stepInfo = StepInfo.getFromDefaultLocation();

	public EvolutionArithmeticReplaceMethodAdapter(MethodVisitor mv,
			String className, String methodName,
			Map<Integer, Integer> possibilities, MutationManager mutationManager) {
		super(mv, className, methodName, possibilities, mutationManager);
	}

	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		logger.debug("Querying mutation " + mutation);
		if (mutationManager.shouldApplyMutation(mutation)) {
			Mutation mutationFromDB = QueryManager.getMutation(mutation);
			MutationCode unMutated = new SingleInsnMutationCode(null, opcode);

			String addInfo = stepInfo.getInfo(mutationFromDB.getId());

			int replaceOpcode = getOpcode(addInfo, opcode);
			MutationCode mutated = new SingleInsnMutationCode(mutationFromDB,
					replaceOpcode);
			logger.info("Replacing Opcode: " + opcode + " with "
					+ replaceOpcode);
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			mv.visitInsn(opcode);
		}
	}

	private int getOpcode(String addInfo, int originalOpcode) {
		int i = Integer.parseInt(addInfo);
		int result = 0;
		switch (i) {
		case 1:
			result = Opcodes.IADD;
			break;
		case 2:
			result = Opcodes.ISUB;
			break;
		case 3:
			result = Opcodes.IMUL;
			break;
		case 4:
			result = Opcodes.IDIV;
			break;
		default:
			throw new RuntimeException("Did not expect: " + result);
		}
		return result += (originalOpcode % 4);
	}

	public static void main(String[] args) {
		new InsnNode(Opcodes.IADD);
	}
}
