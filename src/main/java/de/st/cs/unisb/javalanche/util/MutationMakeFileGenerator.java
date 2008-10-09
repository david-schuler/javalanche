package de.st.cs.unisb.javalanche.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import de.st.cs.unisb.javalanche.properties.MutationProperties;
import de.st.cs.unisb.javalanche.run.threaded.task.MutationTaskCreator;

/**
 * Class that generates Makefiles to execute the mutation testing framework. A
 * makefile consists of several target each executing a mutation task. If
 * multifile mode is activated 6 different makefiles are produced where the
 * mutation tasks are distributed over all makefiles.
 *
 * @author David Schuler
 *
 */
public class MutationMakeFileGenerator {

	/**
	 * Represents one task in the makefile.
	 * @author David Schuler
	 *
	 */
	private static class MakefileTask {
		String targetName;

		String command;

		public MakefileTask(String targetName, String command) {
			super();
			this.targetName = targetName;
			this.command = command;
		}

	}

	private static Logger logger = Logger
			.getLogger(MutationMakeFileGenerator.class);

	private static boolean multiFileMode = MutationProperties.MULTIPLE_MAKEFILES;

	// ant runMutationsDaikon -Dmutation.file=${1}
	// -Dmutation.result.file=result-${2}.xml | tee
	// output-runMutationDaikon-${2}.txt &

	private static final String COMMAND = "-/scratch5/schuler/subjects/runMutationFile.sh  %s %d \"%s\"";

	private static String generateMakeFile(String add) {
		File[] files = getTaskFiles(new File(MutationProperties.RESULT_DIR));
		logger.info("Creating targets for " + files.length + " tasks");

		StringBuilder sb = new StringBuilder();
		StringBuilder allTarget = new StringBuilder();
		allTarget.append("all:");
		int makefileCount = 1;
		int taskSize = (files.length / 6) + 1;
		if (multiFileMode) {
			// TODO (files.length % 6 == 0 ? 0 : 1);
			logger.info("Targets for one Makefile" + taskSize);
		}
		int filecount = 0;
		for (File f : files) {
			filecount++;
			int number = getTaskNumber(f);
			MakefileTask mt = createTask(number, f, add);
			allTarget.append(" " + mt.targetName);
			sb.append(mt.targetName + ":\n");
			sb.append("\t" + mt.command + "\n");
			if (multiFileMode && (filecount % taskSize == 0)) {
				try {
					String tmpString = allTarget.toString() + "\n"
							+ sb.toString();
					File file = new File("Makefile" + makefileCount++);
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					bw.write(tmpString);
					bw.close();
					logger.info(file.getAbsoluteFile() + " written");
					allTarget = new StringBuilder("all:");
					sb = new StringBuilder();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return allTarget.toString() + "\n" + sb.toString();
	}

	private static MakefileTask createTask(int number, File f, String add) {
		String targetName = "result-" + number + ".xml";
		String mutationCommand = String.format(COMMAND, f.toString(), number,
				add);
		return new MakefileTask(targetName, mutationCommand);
	}

	private static int getTaskNumber(File f) {
		// e.g. mutation-task-org_jaxen-01.txt
		String name = f.getName();
		int start = name.lastIndexOf('-') + 1;
		int end = name.lastIndexOf('.');
		String number = name.substring(start, end);
		return Integer.parseInt(number);
	}

	private static File[] getTaskFiles(File dir) {
		File[] listFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name
						.startsWith(MutationTaskCreator.MUTATION_TASK_PROJECT_FILE_PREFIX)) {
					return true;
				}
				return false;
			}
		});
		return listFiles;
	}

	public static void main(String[] args) {
		String add = "";
		if (args.length > 0) {
			add = args[0];
			System.out.println("Additional args: " + add);
		}
		writeMakefile(add);
	}

	private static void writeMakefile(String add) {
		String generateMakeFile = generateMakeFile(add);
		try {
			File file = new File("Makefile");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(generateMakeFile);
			bw.close();
			logger.info(file.getAbsoluteFile() + " written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
