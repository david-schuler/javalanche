package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

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
		private static final Excludes INSTANCE = new Excludes();
	}

	public static Excludes getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private Set<String> excludes;

	private Set<String> allClasses;

	public boolean shouldExclude(String classNameWithDots) {
		return excludes.contains(classNameWithDots);
	}

	private Excludes() {
		excludes = new HashSet<String>();
		allClasses = new TreeSet<String>();
		if (MutationProperties.EXCLUDE_FILE.exists()) {
			List<String> lines = Io
					.getLinesFromFile(MutationProperties.EXCLUDE_FILE);
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
