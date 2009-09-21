package de.unisb.cs.st.javalanche.coverage;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class InstrumentExclude {

	private static class ExcludeData {

		Set<String> lineExclude = new CopyOnWriteArraySet<String>();

		Set<String> returnExclude = new CopyOnWriteArraySet<String>();

	}

	private static String INSTRUMENT_EXCLUDE_FILE = CoverageProperties.TRACE_RESULT_DIR
			+ "instrument-exclude.xml";

	private static final ExcludeData INSTANCE = getInstance();

	private static ExcludeData getInstance() {
		File f = new File(INSTRUMENT_EXCLUDE_FILE);
		ExcludeData excludeData = null;
		if (f.exists()) {
			excludeData = XmlIo.get(f);
		} else {
			excludeData = new ExcludeData();
		}
		return excludeData;
	}

	public static boolean shouldExcludeLines(String className, String methodName) {
		return shouldExcludeLines(getKey(className, methodName));
	}

	public static boolean shouldExcludeLines(String name) {
		return INSTANCE.lineExclude.contains(name);
	}

	private static String getKey(String className, String methodName) {
		return className + "@" + methodName;
	}

	public static boolean shouldExcludeReturns(String className,
			String methodName) {
		return shouldExcludeReturns(getKey(className, methodName));
	}

	public static boolean shouldExcludeReturns(String name) {
		return INSTANCE.returnExclude.contains(name);
	}

	public static boolean shouldExclude(String name) {
		return shouldExcludeLines(name) || shouldExcludeReturns(name);
	}

	public static void save() {
		XmlIo.toXML(INSTANCE, INSTRUMENT_EXCLUDE_FILE);
	}

	public static void addExcludeReturn(String className, String methodName) {
		INSTANCE.returnExclude.add(getKey(className, methodName));
	}

	public static void addExcludeReturn(String name) {
		INSTANCE.returnExclude.add(name);
	}

	public static void addExcludeLine(String name) {
		INSTANCE.lineExclude.add(name);
	}

	public static int numberOfExlusions() {
		// Set<String> all = new HashSet<String>();
		// all.addAll(INSTANCE.lineExclude);
		// all.addAll(INSTANCE.returnExclude);
		// return all.size();
		return INSTANCE.lineExclude.size() + INSTANCE.returnExclude.size();
	}
}
