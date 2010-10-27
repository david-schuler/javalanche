/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.testDetector;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class TestInfo {

	private static Logger logger = Logger.getLogger(TestInfo.class);

	private static Map<String, Integer> testNames;

	public static boolean isTest(String classNameWithDots) {
		if (testNames == null) {
			initTestNames();
		}
		String checkString = classNameWithDots;
		if (classNameWithDots.contains("$")) {
			checkString = classNameWithDots.substring(0, classNameWithDots
					.indexOf("$"));
		}
		logger.debug("Checking test: " + classNameWithDots + " Contained "
				+ testNames.containsKey(checkString));
		return testNames.containsKey(checkString);
	}

	private static void initTestNames() {
		File file = MutationProperties.TEST_MAP_FILE;
		if (file.exists()) {
			testNames = XmlIo.get(file);
		} else {
			throw new RuntimeException("Test name file does not exists " + file
					+ "\nConsider creating it with $> ant scanForTests");
		}
		if (testNames == null || testNames.isEmpty()) {
			throw new RuntimeException("Could not read map correctly. Map: "
					+ testNames);
		}
	}
}
