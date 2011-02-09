/*
 * Copyright (C) 2011 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.coverage;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * @author Bernhard Gruen
 * 
 */
public class CoverageTransformer implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(CoverageTransformer.class);

	private static final Excludes e = Excludes.getTestExcludesInstance();

	public CoverageTransformer() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (className == null) {
				return classfileBuffer;
			}
			if (loader != ClassLoader.getSystemClassLoader()) {
				return classfileBuffer;
			}

			// whitelist - only trace packages of that domain

			if (!className.startsWith(MutationProperties.PROJECT_PREFIX
					.replace('.', '/'))) {
				// System.err.println("Not on whitelist: " + className);
				return classfileBuffer;
			}

			// blacklist: can't trace yourself and don't instrument tests
			// (better performance)

			if (e.shouldExclude(className.replace('/', '.'))) {
				// System.err.println("Blacklisted: " + className);
				return classfileBuffer;
			}
			// System.out.println("Changed: " + className);
			logger.debug("Adding coverage calls for " + className);
			byte[] result = classfileBuffer;
			ClassReader reader = new ClassReader(classfileBuffer);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			ClassVisitor cv = writer;
			if (MutationProperties.TRACE_BYTECODE) {
				cv = new TraceClassVisitor(cv, new PrintWriter(
						MutationPreMain.sysout));
			}
			cv = new CoverageClassAdapter(cv, className);
			reader.accept(cv, ClassReader.SKIP_FRAMES);
			result = writer.toByteArray();

			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			String message = "Exception thrown during instrumentation";
			logger.error(message, t);
			System.err.println(message);
			System.exit(1);
		}
		throw new RuntimeException("Should not be reached");
	}

	private void shutdown() {
	}
}
