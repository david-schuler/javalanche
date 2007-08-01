package org.softevo.mutation.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.DirectoryWalker;

public class HtmlFileSource extends DirectoryWalker{

	public static Collection<File> getHtmlFiles(File startDirectory) throws IOException {
		Set<File> results = new HashSet<File>();
		HtmlFileSource source = new HtmlFileSource();
		source.walk(startDirectory, results);
		return results;
	}

	@Override
	protected boolean handleDirectory(File directory, int depth,
			Collection results) {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void handleFile(File file, int depth, Collection results) {
		if (file.getName().endsWith(".html")) {
			results.add(file);
		}
	}


}
