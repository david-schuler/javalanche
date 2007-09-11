package org.softevo.mutation.bytecodeMutations.arithmetic;


import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;

public class ArithmeticTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(ArithmeticTransformer.class);

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
	ArithmeticReplaceClassAdapter arca = new ArithmeticReplaceClassAdapter(cc);
		logger.info("return Class visitor" + arca);
		return arca;
	}

}
