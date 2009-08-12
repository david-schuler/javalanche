package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * This class is used to determine the classes that are excluded from mutation
 * testing (tests, user specified classes).
 * 
 * 
 * @author David Schuler
 * 
 */
public class Excludes {

	// private static Logger logger = Logger.getLogger(Excludes.class);

	private static class SingletonHolder {
		private static final Excludes INSTANCE = new Excludes(
				MutationProperties.EXCLUDE_FILE);
	}
	
	private static class TestSingletonHolder {
		private static final Excludes INSTANCE = new Excludes(
				MutationProperties.TEST_EXCLUDE_FILE);
	}

	public static Excludes getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static Excludes getTestExcludesInstance() {
		return TestSingletonHolder.INSTANCE;
	}

	private Set<String> excludes;

	private Set<String> allClasses;

	public boolean shouldExclude(String classNameWithDots) {
		return excludes.contains(classNameWithDots);
	}

	private Excludes(File f) {
		excludes = new HashSet<String>();
		allClasses = new TreeSet<String>();
		if (f.exists()) {
			List<String> lines = Io
					.getLinesFromFile(f);
			for (String line : lines) {
				String trim = line.trim();
				if (!trim.startsWith("#")) {
					excludes.add(trim);
				} else {
					trim = trim.substring(1).trim();
				}
				allClasses.add(trim);
			}
		}
	}

	public void addClasses(List<String> classes) {
		allClasses.addAll(classes);
	}

	public void writeFile() {
		StringBuffer sb = new StringBuffer();
		for (String clazz : allClasses) {
			if (!excludes.contains(clazz)) {
				sb.append("# ");
				sb.append(clazz);
				sb.append('\n');
			} else {
				sb.append(clazz);
				sb.append('\n');
			}
		}
		Io.writeFile(sb.toString(), MutationProperties.EXCLUDE_FILE);
		Io.writeFile(sb.toString(), MutationProperties.TEST_EXCLUDE_FILE);
	}

	public void exclude(String testClass) {
		for (String className : allClasses) {
			if (className.contains(testClass)) {
				excludes.add(className);
			}
		}
		if (!excludes.contains(testClass)) {
			excludes.add(testClass);
		}
		if (!allClasses.contains(testClass)) {
			allClasses.add(testClass);
		}
	}

}
