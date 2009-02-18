package de.unisb.cs.st.javalanche.mutation.testDetector;

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

/**
 * Class that scans all subdirectories for JUnit tests using several heuristics.
 *
 * @author David Schuler
 *
 */
public class TestDetector {

	private static final String JAVALANCHE_TEST_BASE_DIR = "javalanche.test.base.dir";

	static final String TEST_MAP_FILENAME = "testname-map.xml";

	private static Logger logger = Logger.getLogger(TestDetector.class);

	private static interface Heuristic {

		boolean matches(File f, String content);

	}

	private static class InTetsDir implements Heuristic {
		public boolean matches(File f, String content) {
			String parent = f.getParent();
			return parent.contains("test");
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
		String property = System.getProperty(JAVALANCHE_TEST_BASE_DIR);
		String baseDir = property != null ? property : ".";
		if (property != null) {
			logger.info("Got property");
		}
		scanForTests(baseDir);
	}

	private static void scanForTests(String baseDir) throws IOException {
		Collection<File> filesByExtension = DirectoryFileSource
				.getFilesByExtension(new File(baseDir), "java");
		Heuristic[] heuristics = new Heuristic[] { new InTetsDir(),
				new ContainsJunitImport(), new ExtendsTest() };
		Map<String, Integer> map = new HashMap<String, Integer>();
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
				// logger.info(name + " " + matches);
			}
		}
		logger.info("Found " + map.size() + " test files");
		XmlIo.toXML(map, new File(TEST_MAP_FILENAME));
	}

	private static String getClassName(File file) {
		String fileName = file.getAbsolutePath();
		try {
			fileName = file.getCanonicalPath();
			logger.info(file.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String name = fileName.replace('/', '.');
		int index = name.lastIndexOf(MutationProperties.PROJECT_PREFIX);
		if (index >= 0 && name.toLowerCase().endsWith("java")) {
			name = name.substring(index, name.length() - 5);
		} else if (name.lastIndexOf('.') >= 0
				&& name.toLowerCase().endsWith("java")) {
			name = fileName.substring(fileName.lastIndexOf('/') + 1, fileName
					.length() - 5);
		}
		logger.info(name);
		return name;
	}

}
