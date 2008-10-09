package de.st.cs.unisb.javalanche.objectInspector.asmAdapters;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class ObjectInspectorTransformer extends BytecodeTransformer {

	private static Logger logger = Logger
			.getLogger(ObjectInspectorTransformer.class);

	public ObjectInspectorTransformer() {
		logger.debug("new ObjectInspectionTransformer");
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new ObjectInspectorClassAdapter(cc);
	}
}
