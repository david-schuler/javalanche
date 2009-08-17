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
import de.unisb.cs.st.javalanche.mutation.testDetector.TestInfo;

/**
 * @author Bernhard Gruen
 * 
 */
public class CoverageTransformer implements ClassFileTransformer {

	private static Logger logger = Logger.getLogger(CoverageTransformer.class);

	private static final Excludes e = Excludes.getTestExcludesInstance(); 
	
	public CoverageTransformer() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				shutdown();
			}
		});
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
	try{
		if (loader != ClassLoader.getSystemClassLoader()) {
			return classfileBuffer;
		}

		// whitelist - only trace packages of that domain

		if (!className.startsWith(MutationProperties.PROJECT_PREFIX.replace('.', '/'))) {
			//System.err.println("Not on whitelist: " + className);
			return classfileBuffer;
		}

		// blacklist: can't trace yourself and don't instrument tests (better performance)

		if (e.shouldExclude(className.replace('/', '.'))) {
			//System.err.println("Blacklisted: " + className);
			return classfileBuffer;
		}
		//System.out.println("Changed: " + className);

		byte[] result = classfileBuffer;
		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
		ClassVisitor cv = writer;
		if (MutationProperties.TRACE_BYTECODE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(
					MutationPreMain.sysout));
		}
		cv = new CoverageClassAdapter(cv, className);
		reader.accept(cv, ClassReader.SKIP_FRAMES);
		result = writer.toByteArray();

		return result;
		}catch(Throwable t){
			t.printStackTrace();
			String message="Exception thrown during instrumentation";
			logger.error(message , t);
			System.err.println(message);
			System.exit(1);
		}
		throw new RuntimeException("Should not be reached");
	}

	private void shutdown() {
	}
}