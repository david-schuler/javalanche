package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.coverage.distance.ClassNode;
import de.unisb.cs.st.javalanche.coverage.distance.ConnectionData;
import de.unisb.cs.st.javalanche.coverage.distance.DistanceClassAdapter;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class DistanceTransformer implements ClassFileTransformer {

	public static class ClassEntry {

		String name;

		Set<String> supers;

		private ClassEntry(String name, Set<String> supers) {
			super();
			this.name = name;
			this.supers = supers;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClassEntry other = (ClassEntry) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public String getName() {
			return name;
		}

		public Set<String> getSupers() {
			return supers;
		}

	}

	private final class SuperClassAdapter extends ClassAdapter {
		Set<String> supers = new HashSet<String>();

		private SuperClassAdapter(ClassVisitor cv) {
			super(cv);
		}

		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {

			supers.add(superName);
			for (String i : interfaces) {
				supers.add(i);
			}
			super
					.visit(version, access, name, signature, superName,
							interfaces);
		}

		public Set<String> getSupers() {
			return supers;
		}
	}

	private ConnectionData data = new ConnectionData();

	public DistanceTransformer() {
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread() {
			public void run() {
				data.save();
				
				XmlIo.toXML(classes, MutationProperties.INHERITANCE_DATA_FILE);
			}
		});
	}

	Set<ClassEntry> classes = new HashSet<ClassEntry>();

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		Set<String> supers = getSuper(classfileBuffer);
		classes.add(new ClassEntry(className, supers));
		if (classNameWithDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassWriter cw = new ClassWriter(0);
			ClassVisitor cv = new DistanceClassAdapter(cw, data);
			cr.accept(cv, ClassReader.SKIP_FRAMES);
		}
		return classfileBuffer;
	}

	private Set<String> getSuper(byte[] classfileBuffer) {
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(0);
		SuperClassAdapter cv = new SuperClassAdapter(cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		return cv.getSupers();
	}

}
