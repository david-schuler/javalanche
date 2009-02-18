package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Join;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.Io;

public class TestDetector {

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
		Collection<File> filesByExtension = DirectoryFileSource
				.getFilesByExtension(new File("."), "java");
		Heuristic[] heuristics = new Heuristic[] { new InTetsDir(),
				new ContainsJunitImport(), new ExtendsTest() };
		for (File file : filesByExtension) {
			List<String> linesFromFile = Io.getLinesFromFile(file);
			String join = Join.join(" ", linesFromFile);
			int matches = 0;
			for (Heuristic h : heuristics) {
				if (h.matches(file, join)) {
					matches++;
				}
			}
		}
	}
}
