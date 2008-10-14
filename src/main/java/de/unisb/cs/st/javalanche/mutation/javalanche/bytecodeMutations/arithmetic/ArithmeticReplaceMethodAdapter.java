package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
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
public class ArithmeticReplaceMethodAdapter extends AbstractMutationAdapter {

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

	private static Map<Integer, Integer> replaceMap = ReplaceMap
			.getReplaceMap();


	private Logger logger = Logger
			.getLogger(ArithmeticReplaceMethodAdapter.class);

	public ArithmeticReplaceMethodAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv, className, methodName, possibilities);
	}

	@Override
	public void visitInsn(int opcode) {
		if (replaceMap.containsKey(opcode) && !mutationCode) {
			mutate(opcode);
		} else {
			super.visitInsn(opcode);
		}
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	private void mutate(int opcode) {
		Mutation queryMutation = new Mutation(className, getLineNumber(),
				getPossibilityForLine(), Mutation.MutationType.ARITHMETIC_REPLACE,isClassInit);
		addPossibilityForLine();
		logger.debug("Querying mutation " + queryMutation);
		if (MutationManager.shouldApplyMutation(queryMutation)) {
			Mutation mutationFromDB = QueryManager.getMutation(queryMutation);
			MutationCode unMutated = new SingleInsnMutationCode(null, opcode);
			MutationCode mutated = new SingleInsnMutationCode(mutationFromDB,
					replaceMap.get(opcode));
			BytecodeTasks.insertIfElse(mv, unMutated,
					new MutationCode[] { mutated });
		} else {
			super.visitInsn(opcode);
		}
	}
}
