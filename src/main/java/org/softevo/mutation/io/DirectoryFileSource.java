package org.softevo.mutation.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.DirectoryWalker;

/**
 *
 * Provides a method that returns all files that end with a given extension, in
 * all subdirectories, for a given start directory.
 *
 * @author David Schuler
 *
 */
public class DirectoryFileSource extends DirectoryWalker {

	private static String fileExtension;

	/**
	 * Searches recursively in all subdirectories for files with given
	 * extension.
	 *
	 * @param startDirectory
	 *            Location of the directory to start from.
	 * @return A list of all files, from all subdirectories, ending with given
	 *         extension.
	 * @throws IOException
	 *             if IOException thrown when processing directories.
	 */
	public static Collection<File> getFilesByExtension(File startDirectory,
			String fileExtension) throws IOException {
		DirectoryFileSource.fileExtension = fileExtension;
		Set<File> results = new HashSet<File>();
		DirectoryFileSource source = new DirectoryFileSource();
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
		if (file.getName().endsWith(fileExtension)) {
			results.add(file);
		}
	}

}
