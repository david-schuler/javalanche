package org.softevo.mutation.testDetector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;

/**
 * @author David Schuler
 *
 */
@SuppressWarnings("unchecked")
public class DetectTestClassAdapter extends ClassAdapter {

	private static Logger logger = Logger
			.getLogger(DetectTestClassAdapter.class);

	private String className;

	private boolean isTestCase;

	public DetectTestClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name.replace('/', '.');
		try {
			Class c = Class.forName(className);
			Set<Class> classes = getAllClassesAndInterfaces(c);
			// logger.info(c + " - " + classes);
			if (isTestCase(classes)) {
				isTestCase = true;
				logger.info(c + " is a TestCase");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}


	private boolean isTestCase(Set<Class> classes) {
		return classes.contains(TestCase.class);
	}

	/**
	 * Gets all interfaces and classes the given class inherits from. But they
	 * have to be on the classpath.
	 *
	 * @param c
	 *            Class to get all interfaces and classes for.
	 * @return A set of all implemented interfaces and super classes.
	 */
	private static Set<Class> getAllClassesAndInterfaces(Class c) {
		Set<Class> result = new HashSet<Class>();
		result.addAll(Arrays.asList(c.getInterfaces()));
		Class superClass = c.getSuperclass();
		result.add(superClass);
		if (superClass != null) {
			result.addAll(getAllClassesAndInterfaces(superClass));
		}
		return result;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the isTestCase
	 */
	public boolean isTestCase() {
		return isTestCase;
	}
}
