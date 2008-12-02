package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.MakefileGenerator;
import de.unisb.cs.st.ds.util.MakefileGenerator.Target;
import de.unisb.cs.st.ds.util.io.Io;

public class CoverageMakefileGenerator {

	private static Logger logger = Logger
			.getLogger(CoverageMakefileGenerator.class);

	public static void main(String[] args) {
		File f = new File(
				"/scratch/schuler/subjects/ibugs_rhino-0.1/passingTests.txt");
		List<String> linesFromFile = Io.getLinesFromFile(f);
		int count = 0;
		List<Target> targets = new ArrayList<Target>();
		for (String testName : linesFromFile) {
			String command = "./runCobertura.sh " + testName;
			count++;
			Target t = new Target("task-" + count, command);
			targets.add(t);
		}
		String makeFileContent = MakefileGenerator.generateMakefile(targets);
//		logger.info(makeFileContent);
		Io.writeFile(makeFileContent, new File("CoberturaMakefile"));
	}
}
