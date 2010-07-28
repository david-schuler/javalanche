/*
 * Copyright (C) 2009 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.testDetector;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * Class that scans all subdirectories for JUnit tests using several heuristics.
 * 
 * @author David Schuler
 * 
 */
public class TestDetector {

	private static final String JAVALANCHE_TEST_BASE_DIR = "javalanche.test.base.dir";

	private static Logger logger = Logger.getLogger(TestDetector.class);

	private static interface Heuristic {

		boolean matches(File f, String content);

	}

	private static class InTetsDir implements Heuristic {
		public boolean matches(File f, String content) {
			String parent = f.getParent();
			return parent.contains("/test/") || parent.contains("/tests/");
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

	private static class TestAnnotation implements Heuristic {
		public boolean matches(File f, String content) {
			return content.contains("@Test");
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
		Collection<File> javaFiles = getFiles(baseDir);
		Heuristic[] heuristics = new Heuristic[] { new InTetsDir(),
				new ContainsJunitImport(), new ExtendsTest(),
				new TestAnnotation() };
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (File file : javaFiles) {
			List<String> linesFromFile = Io.getLinesFromFile(file);
			String join = Joiner.on(" ").join(linesFromFile);
			int matches = 0;
			for (Heuristic h : heuristics) {
				if (h.matches(file, join)) {
					matches++;
				}
			}
			if (matches > 0) {
				String name = getClassName(file);
				map.put(name, matches);
			}
		}
		String message = "Found " + map.size() + " test files.";
		logger.info(message);
		System.out.println(message);
		updateExcludeFiel(map);
		// XmlIo.toXML(map, MutationProperties.TEST_MAP_FILE);
	}

	private static Collection<File> getFiles(String baseDir) throws IOException {
		File dir = new File(baseDir);
		File[] dirs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						&& !pathname.toString().endsWith(
								MutationProperties.OUTPUT_DIR);
			}
		});
		Set<File> result = new HashSet<File>();
		for (File f : dirs) {
			result.addAll(DirectoryFileSource.getFilesByExtension(f, "java"));
		}
		return result;
	}

	private static void updateExcludeFiel(Map<String, Integer> map) {
		Set<Entry<String, Integer>> entrySet = map.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			if (entry.getValue() > 0) {
				String testClass = entry.getKey();
				Excludes.getInstance().exclude(testClass);
			}
		}
		Excludes.getInstance().writeFile();
	}

	private static String getClassName(File file) {
		String fileName = file.getAbsolutePath();
		try {
			fileName = file.getCanonicalPath();
			logger.debug("Filename " + file.getCanonicalPath());
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
		logger.debug(name);
		return name;
	}

}
