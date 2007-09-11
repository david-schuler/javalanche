package org.softevo.mutation.bytecodeMutations.arithmetic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.bytecodeMutations.negateJumps.NegateJumpsPossibilitiesClassAdapter;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class ArithmeticReplaceCollectorTransformer extends BytecodeTransformer {

	private MutationPossibilityCollector mutationPossibilityCollector;

	public ArithmeticReplaceCollectorTransformer(MutationPossibilityCollector mpc) {
		mutationPossibilityCollector = mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new PossibilitiesAretmeticReplaceClassAdapter(cc, mutationPossibilityCollector);
	}
}
