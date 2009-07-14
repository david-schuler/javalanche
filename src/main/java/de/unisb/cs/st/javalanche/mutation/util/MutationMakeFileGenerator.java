package de.unisb.cs.st.javalanche.mutation.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.run.threaded.task.MutationTaskCreator;

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
	 *
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

	private static String generateMakeFile(String scriptCommand, String add) {
		File[] files = getTaskFiles();
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
		for (File taskFile : files) {
			filecount++;
			int number = getTaskNumber(taskFile);

			MakefileTask mt = createTask(scriptCommand, taskFile, number, add);
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

	private static MakefileTask createTask(String scriptCommand, File taskFile,
			int number, String add) {
		String targetName = "result-" + number + ".xml";
		String mutationCommand = String.format("%s %s %d \"%s\"",
				scriptCommand, taskFile.toString(), number, add);
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

	public static File[] getTaskFiles() {
		File dir = new File(MutationProperties.RESULT_DIR);
		logger.info("Searching for files in directory" + dir);
		logger.info("Seraching for files starting with: "
				+ (MutationTaskCreator.MUTATION_TASK_PROJECT_FILE_PREFIX));
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
		String scriptName = "mutation.command";

		String scriptCommand = getScriptCommand(System.getProperty(scriptName));

		if (scriptCommand == null) {
			throw new RuntimeException("No command given. Expecting property "
					+ scriptName + "to be set");
		}
		logger.info(scriptCommand);
		logger.info(new File(".").getAbsoluteFile());
		String generateMakeFile = generateMakeFile(scriptCommand, add);
		Io.writeFile(generateMakeFile, new File("Makefile"));
		// try {
		// File file = new File("Makefile");
		// BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		// bw.write(generateMakeFile);
		// bw.close();
		// logger.info(file.getAbsoluteFile() + " written");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private static String getScriptCommand(String property) {
		String result = property;
		File dir = new File(".").getAbsoluteFile();
		logger.info("DIR| " + dir);
		File scriptFile = new File(dir, property);
		try {
			logger.info("Script | " + scriptFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!scriptFile.exists() && dir != null) {
			dir = dir.getParentFile();
			scriptFile = new File(dir, property);
		}
		if (scriptFile.exists()) {
			try {
				result = scriptFile.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
