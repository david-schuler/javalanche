/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
//package de.unisb.cs.st.javalanche.mutation.util;
//
//import java.io.File;
//
//import org.apache.log4j.Logger;
//import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
//
///**
// * Class that generates some directories that are needed by the mutation testing
// * framework. Should be executed once for installing the software.
// *
// * @author David Schuler
// *
// */
//public class Setup {
//	private static Logger logger = Logger.getLogger(Setup.class);
//
//	public static void main(String[] args) {
//		generateDirectories();
//	}
//
//	private static void generateDirectories() {
//		generateDirectory(MutationProperties.OUTPUT_DIR);
//		generateDirectory(MutationProperties.RESULT_OBJECTS_DIR);
//
//	}
//
//	private static void generateDirectory(String directoryName) {
//		File dir = new File(directoryName);
//		if (!dir.exists()) {
//			boolean createdDirectory = dir.mkdir();
//			if (createdDirectory) {
//				logger.info("Created directory: " + dir);
//			} else {
//				logger.info("Could not create directory: " + dir);
//			}
//		} else {
//			logger.info("Directory already exists: " + dir);
//		}
//	}
//}
