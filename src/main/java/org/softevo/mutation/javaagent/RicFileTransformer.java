package org.softevo.mutation.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.replaceIntegerConstant.RicTransformer;

public class RicFileTransformer implements ClassFileTransformer {

	Logger logger = Logger.getLogger(RicFileTransformer.class.getName());

	private static Mutations mutations = Mutations.fromXML();

	private static RicTransformer ricTransformer = new RicTransformer(mutations);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		System.out.println("Transforming: " + className);
		if (mutations.containsClass(className)) {
			return ricTransformer.transformBytecode(classfileBuffer);
		}
		return classfileBuffer;
	}

}
