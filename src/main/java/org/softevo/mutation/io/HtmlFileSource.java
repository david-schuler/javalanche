package org.softevo.mutation.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.DirectoryWalker;

/**
 *
 * Provides a method that returns all html files, in all subdirectories, for a
 * given directory.
 *
 * @author David Schuler
 *
 */
public class HtmlFileSource extends DirectoryWalker {

	/**
	 * Searches recursively in all subdirectories for html files.
	 *
	 * @param startDirectory
	 *            Location of the directory.
	 * @return A list of all files, from all subdirectories, ending with html.
	 * @throws IOException
	 *             if IOException thrown when processing directories.
	 */
	public static Collection<File> getHtmlFiles(File startDirectory)
			throws IOException {
		Set<File> results = new HashSet<File>();
		HtmlFileSource source = new HtmlFileSource();
		source.walk(startDirectory, results);
		return results;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.io.DirectoryWalker#handleDirectory(java.io.File,
	 *      int, java.util.Collection)
	 */
	@Override
	protected boolean handleDirectory(File directory, int depth,
			Collection results) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.commons.io.DirectoryWalker#handleFile(java.io.File, int,
	 *      java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void handleFile(File file, int depth, Collection results) {
		if (file.getName().endsWith(".html")) {
			results.add(file);
		}
	}

}
