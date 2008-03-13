package org.softevo.mutation.objectInspector;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.softevo.mutation.objectInspector.asmAdapters.ObjectInspectorClassAdapter;
import org.softevo.mutation.objectInspector.testClasses.ObjectsForMethod;

public class ObjectInspectorTest {

	// private static final String CLASSNAME =
	// "./target/test-classes/org/softevo/mutation/objectInspector/testClasses/ObjectsForMethod.class";

	private static final String RCLASSNAME = "org/softevo/mutation/objectInspector/testClasses/ObjectsForMethod"
			.replace('/', '.');

	private static class TestingClassLoader extends ClassLoader {

		public TestingClassLoader() throws IOException {
		}

		@SuppressWarnings("unchecked")
		public Class loadClass(final String name) throws ClassNotFoundException {
			if (name.equals(RCLASSNAME)) {
				try {
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES
							| ClassWriter.COMPUTE_MAXS);
					CheckClassAdapter cc = new CheckClassAdapter(cw);
					TraceClassVisitor tcv = new TraceClassVisitor(cc,
							new PrintWriter(System.out));
					ClassReader cr = new ClassReader(getClass()
							.getResourceAsStream(
									"/" + name.replace('.', '/') + ".class"));
					cr
							.accept(
									new ObjectInspectorClassAdapter(
											tcv), ClassReader.EXPAND_FRAMES);
					byte[] bytecode = cw.toByteArray();
					return super
							.defineClass(name, bytecode, 0, bytecode.length);
				} catch (IOException ex) {
					throw new ClassNotFoundException("Load error: "
							+ ex.toString(), ex);
				}
			}
			return super.loadClass(name);
		}

	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public void testInspectObjects() {

		Class ob = ObjectsForMethod.class;
		System.err.println();
		try {
			new TestingClassLoader().loadClass(RCLASSNAME);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Constructor<ObjectsForMethod> constructor = ob.getConstructors()[0];
		try {
			ObjectsForMethod obm = constructor.newInstance(new Object[0]);
			obm.method1();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Exception {
		TestingClassLoader tcl = new TestingClassLoader();
		Class cc = tcl.loadClass(RCLASSNAME);
		Method m1 = cc.getMethod("method1");
		Method m2 = cc.getMethod("method2");
		Method m3 = cc.getMethod("method3");
		try {
			Object o = m1.invoke(null, new Object[0]);
			Assert.assertEquals(new Integer(1), o);
			o = m2.invoke(null, new Object[0]);
			Assert.assertEquals(new Integer(1), o);
			o = m3.invoke(null, new Object[0]);
		} catch (InvocationTargetException e) {
			throw (Exception) e.getTargetException();
		}
	}
}
