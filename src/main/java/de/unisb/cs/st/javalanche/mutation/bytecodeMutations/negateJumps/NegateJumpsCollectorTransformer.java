package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.CollectorByteCodeTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;


public class NegateJumpsCollectorTransformer extends CollectorByteCodeTransformer{


	public NegateJumpsCollectorTransformer(MutationPossibilityCollector mpc) {
		this.mpc= mpc;
	}

	@Override
	protected ClassVisitor classVisitorFactory(ClassWriter cw) {
		ClassVisitor cc = new CheckClassAdapter(cw);
		return new NegateJumpsPossibilitiesClassAdapter(cc, mpc);
	}
}
