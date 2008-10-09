package org.softevo.mutation.bytecodeMutations.arithmetic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.softevo.mutation.bytecodeMutations.CollectorByteCodeTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class ArithmeticReplaceCollectorTransformer extends CollectorByteCodeTransformer {


	public ArithmeticReplaceCollectorTransformer(MutationPossibilityCollector mpc) {
		this.mpc = mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new PossibilitiesArithmeticReplaceClassAdapter(cc, mpc);
	}
}
