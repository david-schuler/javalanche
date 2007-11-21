package org.softevo.mutation.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

/**
*
* Provides a method that returns all class files, in all subdirectories, for a
* given directory.
*
* @author David Schuler
*
*/
public class ClassFileSource extends DirectoryWalker {

	public ClassFileSource() {
	}

	public List<File> getClasses(File startDirectory) throws IOException {
		List<File> results = new ArrayList<File>();
		walk(startDirectory, results);
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
		if (file.getName().endsWith(".class")) {
			if(results.contains(file)){
				throw new RuntimeException("class Already contained");
			}
			results.add(file);
		}
	}

}
