package org.softevo.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationTransformer extends BytecodeTransformer {

	private static Logger logger = Logger.getLogger(MutationTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		logger.info("Mutation Transformer");
		return new MutationsClassAdapter(new TraceClassVisitor(cc, new PrintWriter(System.out)));
	}

}
