package de.st.cs.unisb.javalanche.bytecodeMutations.replaceIntegerConstant;

import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import de.st.cs.unisb.javalanche.bytecodeMutations.CollectorByteCodeTransformer;
import de.st.cs.unisb.javalanche.mutationPossibilities.MutationPossibilityCollector;

public class RicCollectorTransformer extends CollectorByteCodeTransformer {

	public RicCollectorTransformer(MutationPossibilityCollector mpc) {
		this.mpc = mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		cc = new TraceClassVisitor(cc, new PrintWriter(System.out));
		return new PossibilitiesRicClassAdapter(cc, mpc);
	}
}
