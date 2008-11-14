//package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.objectweb.asm.ClassAdapter;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.MethodVisitor;
//import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
//
//public class RemoveCallsPossibilitiesClassAdapter extends ClassAdapter {
//
//	private String className;
//
//	private final MutationPossibilityCollector mpc;
//
//	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();
//
//	public RemoveCallsPossibilitiesClassAdapter(ClassVisitor cv,
//			MutationPossibilityCollector mpc) {
//		super(cv);
//		this.mpc = mpc;
//	}
//
//	@Override
//	public void visit(int version, int access, String name, String signature,
//			String superName, String[] interfaces) {
//		className = name;
//		super.visit(version, access, name, signature, superName, interfaces);
//	}
//
//	@Override
//	public MethodVisitor visitMethod(int access, String name, String desc,
//			String signature, String[] exceptions) {
//		MethodVisitor superVisitor = super.visitMethod(access, name, desc,
//				signature, exceptions);
//		MethodVisitor actualAdapter = new RemoveCallsPossibilitiesMethodAdapter(
//				superVisitor, className, name, mpc,possibilities );
//		return actualAdapter;
//	}
//
//
//
//}
