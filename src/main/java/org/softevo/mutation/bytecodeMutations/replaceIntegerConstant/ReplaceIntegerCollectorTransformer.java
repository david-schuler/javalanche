package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;


public class ReplaceIntegerCollectorTransformer extends BytecodeTransformer {

	MutationPossibilityCollector mutationPossibilityCollector;

	public ReplaceIntegerCollectorTransformer(MutationPossibilityCollector mpc) {
		mutationPossibilityCollector = mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		cc = new TraceClassVisitor(cc,new PrintWriter(System.out));
		return new PossibilitiesRicClassAdapter(cc, mutationPossibilityCollector);
	}
}
