package de.unisb.cs.st.javalanche.tracer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class TraceTransformer implements ClassFileTransformer {	
	
	public TraceTransformer() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				shutdown();
			}
		});
	}
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (loader != ClassLoader.getSystemClassLoader()) {
			return classfileBuffer;
		}
		
		// whitelist - only trace packages of that domain
		
		if (!className.startsWith(MutationProperties.PROJECT_PREFIX.replace('.', '/'))) {
			//System.err.println("Not on whitelist: " + className);
			return classfileBuffer;
		}
		
		// blacklist: can't trace yourself and don't instrument tests (better performance)
		
		if (className.contains("/test/") || className.contains("/tests/")) {
			//System.err.println("Blacklisted: " + className);
			return classfileBuffer;
		}		
		//System.out.println("Changed: " + className);
		
		byte[] result = classfileBuffer;
		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
		ClassAdapter adapter = new TracerClassAdapter(writer, className);
		reader.accept(adapter, 0);
		result = writer.toByteArray();
		return result;
	}
	
	private void shutdown() {
	}
}