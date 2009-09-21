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
package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;

public class InvariantFilesUtil {

	private static final File DIR = new File(InvariantProperties.INVARIANT_DIR);

	public static Map<String, Set<Integer>> readInvariantPerTestFiles() {
		Map<String, Set<Integer>>  result = new HashMap<String, Set<Integer>>();
		File[] listFiles = DIR.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.endsWith("mapping.txt")) {
					return true;
				}
				return false;
			}

		});
		assert (listFiles.length == 1);
		List<String> linesFromFile = Io.getLinesFromFile(listFiles[0]);
		for (String line : linesFromFile) {
			int number = getNumber(line);
			String testName = getTestName(line);
			File file = new File(DIR, InvariantProperties.CHECKED_PREFIX + "invariant-ids-" + number + ".ser");
			Set<Integer> ids = SerializeIo.get(file);
			result.put(testName, ids);
//			idsPerTest.put(testName, ids);
//			testNumbers.put(testName, number);
//			files.put(ids, file);
		}
		return result;
	}

	private static int getNumber(String line) {
		int end = line.indexOf(',');
		String number = line.substring(end - 4, end);
		return Integer.parseInt(number);
	}

	private static String getTestName(String line) {
		int index = line.indexOf(',');
		int index2 = line.indexOf('(');
		String test = line.substring(index + 1, index2);
		String className = line.substring(index2 + 1, line.length() - 1);
		return className + "." + test;
	}

}
