package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.AbstractMutationAdapter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Method Adapter that replaces arithmetic operations. The details for the
 * replacements can be found in {@link ReplaceMap}.
 * 
 * @see ReplaceMap
 * 
 * @author David Schuler
 * 
 */
public abstract class AbstractArithmeticMethodAdapter extends AbstractMutationAdapter {

	
	protected static Map<Integer, Integer> replaceMap = ReplaceMap
			.getReplaceMap();

	public AbstractArithmeticMethodAdapter(MethodVisitor mv, String className,
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
		Mutation mutation = new Mutation(className, getLineNumber(),
				getPossibilityForLine(),
				Mutation.MutationType.ARITHMETIC_REPLACE, isClassInit);
		addPossibilityForLine();
		handleMutation(mutation, opcode);
	}

	protected abstract void handleMutation(Mutation mutation, int opcode);
	
}
