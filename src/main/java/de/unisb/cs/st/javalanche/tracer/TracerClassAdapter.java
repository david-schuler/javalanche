package de.unisb.cs.st.javalanche.tracer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;


public class TracerClassAdapter extends ClassAdapter {

	private String className;
	
	public TracerClassAdapter(ClassVisitor visitor, String theClass) {
		super(visitor);
		this.className = theClass;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public MethodVisitor visitMethod(int arg, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(arg, name,	descriptor,	signature, exceptions);
		MethodAdapter ma = new TracerMethodAdapter(mv, className, name, descriptor);
		return ma;
	}

}
