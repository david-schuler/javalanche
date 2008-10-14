package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;


import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class NegateJumpsTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(NegateJumpsTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		NegateJumpsClassAdapter njca = new NegateJumpsClassAdapter(cc);
		logger.info("return Class visitor" + njca);
		return njca;
	}

}
