package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * ClassAdapter for the replacement of arithmetic operatiorns.
 *
 * @see ArithmeticReplaceMethodAdapter
 *
 * @author David Schuler
 *
 */
public class ArithmeticReplaceClassAdapter extends ClassAdapter {

	/**
	 * The name of the class.
	 */
	private String className;
	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	/**
	 * Constructs a new {@link ArithmeticReplaceClassAdapter}
	 *
	 * @param cv
	 *            The {@link ClassVisitor} to which this adapter delegates
	 *            calls.
	 */
	public ArithmeticReplaceClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		return new ArithmeticReplaceMethodAdapter(super.visitMethod(access,
				name, desc, signature, exceptions), className, name, possibilities );
	}

}
