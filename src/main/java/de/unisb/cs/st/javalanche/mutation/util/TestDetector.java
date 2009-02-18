package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Join;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class TestDetector {

	private static Logger logger = Logger.getLogger(TestDetector.class);

	private static interface Heuristic {

		boolean matches(File f, String content);

	}

	private static class InTetsDir implements Heuristic {
		public boolean matches(File f, String content) {
			String parent = f.getParent();
			return parent.contains("test/") || parent.contains("tests/");
		}

	}

	private static class ContainsJunitImport implements Heuristic {
		public boolean matches(File f, String content) {
			return content.contains("import junit.framework.");
		}
	}

	private static class ExtendsTest implements Heuristic {
		public boolean matches(File f, String content) {
			return content.contains("extends Test");
		}
	}

	public static void main(String[] args) throws IOException {
		scanForTests();
	}

	private static void scanForTests() throws IOException {
		Collection<File> filesByExtension = DirectoryFileSource
				.getFilesByExtension(new File("."), "java");
		Heuristic[] heuristics = new Heuristic[] { new InTetsDir(),
				new ContainsJunitImport(), new ExtendsTest() };
		Map<String,Integer> map = new HashMap<String, Integer>();
		for (File file : filesByExtension) {
			List<String> linesFromFile = Io.getLinesFromFile(file);
			String join = Join.join(" ", linesFromFile);
			int matches = 0;
			for (Heuristic h : heuristics) {
				if (h.matches(file, join)) {
					matches++;
				}
			}
			if (matches > 0) {
				String name = getClassName(file);
				map.put(name, matches);
				logger.info(name  + "  " +   matches);
			}
		}
		logger.info("Found " + map.size() + " test files");
		XmlIo.toXML(map, new File("testname-map.xml"));
	}

	private static String getClassName(File file) {
		String name = file.getAbsolutePath().replace('/', '.');
		int index = name.indexOf(MutationProperties.PROJECT_PREFIX);
		if (index >= 0 && name.toLowerCase().endsWith("java")) {
			name = name.substring(index, name.length() - 5);
		}
		return name;
	}



}
