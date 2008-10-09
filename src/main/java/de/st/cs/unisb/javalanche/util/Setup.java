package de.st.cs.unisb.javalanche.util;

import java.io.File;

import org.apache.log4j.Logger;
import de.st.cs.unisb.javalanche.properties.MutationProperties;

/**
 * Class that generates some directories that are needed by the mutation testing
 * framework. Should be executed once for installing the software.
 *
 * @author David Schuler
 *
 */
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
