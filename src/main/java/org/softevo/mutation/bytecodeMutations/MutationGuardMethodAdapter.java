package org.softevo.mutation.bytecodeMutations;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;

public class MutationGuardMethodAdapter extends AbstractMutationAdapter {

	public MutationGuardMethodAdapter(MethodVisitor mv, String className, String methodName) {
		super(mv, className, methodName);
	}
	
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub
		super.visitAttribute(attr);
	}
	
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		// TODO Auto-generated method stub
		return super.visitAnnotationDefault();
	}
	@Override
	public void visitCode() {
		// TODO Auto-generated method stub
		super.visitCode();
	}
	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

}
