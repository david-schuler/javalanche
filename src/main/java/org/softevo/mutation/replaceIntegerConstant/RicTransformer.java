package org.softevo.mutation.replaceIntegerConstant;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.mutation.mutationPossibilities.Mutations;

public class RicTransformer extends BytecodeTransformer {



	private Mutations mutations;

	public RicTransformer(Mutations mutations) {
		super();
		this.mutations = mutations;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new RicClassAdapter(cc,mutations);
	}
}
