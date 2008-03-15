package org.softevo.mutation.bytecodeMutations;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.mutation.properties.MutationProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class MutationTransformer extends BytecodeTransformer {

	

	private static Logger logger = Logger.getLogger(MutationTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cv = new CheckClassAdapter(cw);
		logger.info("Mutation Transformer");
		if (MutationProperties.DEBUG) {
			cv = new TraceClassVisitor(cv, new PrintWriter(System.out));
		}
		return new MutationsClassAdapter(cv);
	}

}
