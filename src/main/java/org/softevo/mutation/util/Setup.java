package org.softevo.mutation.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.softevo.mutation.properties.MutationProperties;

public class Setup {
	private static Logger logger = Logger.getLogger(Setup.class);

	public static void main(String[] args) {
		generateDirectories();
	}

	private static void generateDirectories() {
		generateDirectory(MutationProperties.RESULT_DIR);
		generateDirectory(MutationProperties.RESULT_OBJECTS_DIR);

	}

	private static void generateDirectory(String directoryName) {
		File dir = new File(directoryName);
		if (!dir.exists()) {
			boolean createdDirectory = dir.mkdir();
			if (createdDirectory) {
				logger.info("Created directory: " + dir);
			} else {
				logger.info("Could not create directory: " + dir);
			}
		} else {
			logger.info("Directory already exists: " + dir);
		}
	}
}
