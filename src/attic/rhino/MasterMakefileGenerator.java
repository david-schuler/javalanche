package de.unisb.cs.st.javalanche.rhino;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.ds.util.MakefileGenerator;
import de.unisb.cs.st.ds.util.MakefileGenerator.Target;
import de.unisb.cs.st.ds.util.io.Io;

public class MasterMakefileGenerator {

	public static void main(String[] args) {
		File dir = new File("/scratch/schuler/subjects/ibugs_rhino-0.1");
		File[] listFiles = dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.startsWith("Makefile-")) {
					return true;
				}
				return false;
			}

		});
		int count = 0;
		List<Target> targets = new ArrayList<Target>();
		for (File file : listFiles) {
			count++;
			Target t = new Target("target-" + count, "./runMakefile.sh \""  + file.getName() + "\"");
			targets.add(t);
		}
		String makefileContent = MakefileGenerator.generateMakefile(targets);
		Io.writeFile(makefileContent, new File(dir, "MasterMakefile"));
	}
}
