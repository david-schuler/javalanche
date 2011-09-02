/*
* Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.ds.util.MakefileGenerator;
import de.unisb.cs.st.ds.util.MakefileGenerator.Target;
import de.unisb.cs.st.ds.util.io.Io;

public class CoverageMakefileGenerator {


	public static void main(String[] args) {
		File f = new File(
				"/scratch/schuler/subjects/ibugs_rhino-0.1/passingTests.txt");
		List<String> linesFromFile = Io.getLinesFromFile(f);
		int count = 0;
		List<Target> targets = new ArrayList<Target>();
		for (String testName : linesFromFile) {
			String command = "./runCobertura.sh " + testName;
			count++;
			Target t = new Target("coverage-report/coverage-" + testName.replace('/', '_')
					+ ".xml", command);
			targets.add(t);
		}
		String makeFileContent = MakefileGenerator.generateMakefile(targets);
		// logger.info(makeFileContent);
		Io.writeFile(makeFileContent, new File("CoberturaMakefile"));
	}
}
