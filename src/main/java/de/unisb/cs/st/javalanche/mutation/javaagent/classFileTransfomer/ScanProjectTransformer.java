package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * Transformer that collects all classes with the current mutation prefix, and
 * saves them to a file. This file can later be used to exclude classes from
 * mutation testing.
 * 
 */
public class ScanProjectTransformer implements ClassFileTransformer {

	private List<String> classes = new ArrayList<String>();

	public ScanProjectTransformer() {
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread() {

			public void run() {
				Excludes.getInstance().addClasses(classes);
				Excludes.getInstance().writeFile();
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');

		if (classNameWithDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
			classes.add(classNameWithDots);
		}
		return classfileBuffer;

	}

}
