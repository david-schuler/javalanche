package org.softevo.mutation.bytecodeMutations.negateJumps;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;


public class NegateJumpsCollectorTransformer extends BytecodeTransformer {

	MutationPossibilityCollector mutationPossibilityCollector;

	public NegateJumpsCollectorTransformer(MutationPossibilityCollector mpc) {
		mutationPossibilityCollector = mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		//cc = new TraceClassVisitor(cc,new PrintWriter(System.out));
		return new NegateJumpsPossibilitiesClassAdapter(cc, mutationPossibilityCollector);
	}
}
