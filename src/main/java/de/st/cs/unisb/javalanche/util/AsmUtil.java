package de.st.cs.unisb.javalanche.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * BYtecode related Utility class
 *
 * @author David Schuler
 *
 */
public class AsmUtil {

	/**
	 * Class should not be initialized.
	 */
	private AsmUtil() {
	}

	/**
	 * Returns a string representation of a class given as a byte array.
	 *
	 * @param classBytes
	 *            the bytes of the class
	 * @return a string representation of the class
	 */
	public static String classToString(byte[] classBytes) {
		ClassReader cr = new ClassReader(classBytes);
		StringWriter sw = new StringWriter();
		TraceClassVisitor tv = new TraceClassVisitor(new PrintWriter(sw));
		cr.accept(tv, 0);
		return sw.toString();
	}

}