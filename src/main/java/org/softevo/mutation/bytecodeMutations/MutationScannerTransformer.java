package org.softevo.mutation.bytecodeMutations;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class MutationScannerTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(MutationScannerTransformer.class);

	private final MutationPossibilityCollector mpc;

	public MutationScannerTransformer(MutationPossibilityCollector mpc) {
		this.mpc = mpc;

	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		//new TraceClassVisitor(cc, new PrintWriter(System.out))
		return new MutationsCollectorClassAdapter(cc, mpc);
	}
}
