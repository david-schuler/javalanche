package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationCode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

/**
 * Method Adapter that replaces arithmetic operations. The details for the
 * replacements can be found in {@link ReplaceMap}.
 * 
 * @see ReplaceMap
 * 
 * @author David Schuler
 * 
 */
public class ArithmeticReplaceMethodAdapter extends
		AbstractArithmeticMethodAdapter {

	public ArithmeticReplaceMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	private static Logger logger = Logger
			.getLogger(ArithmeticReplaceMethodAdapter.class);

	private static class SingleInsnMutationCode extends MutationCode {

		private int opc;

		public SingleInsnMutationCode(Mutation mutation, int opcode) {
			super(mutation);
			this.opc = opcode;
		}

		@Override
		public void insertCodeBlock(MethodVisitor mv) {
			mv.visitInsn(opc);
		}

	}

	@Override
	protected void handleMutation(Mutation mutation, int opcode) {
		logger.debug("Querying mutation " + mutation);
		if (MutationManager.shouldApplyMutation(mutation)) {
			Mutation mutationFromDB = QueryManager.getMutation(mutation);
			MutationCode unMutated = new SingleInsnMutationCode(null, opcode);
			MutationCode mutated = new SingleInsnMutationCode(mutationFromDB,
					replaceMap.get(opcode));
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			mv.visitInsn(opcode);
		}
	}
}
