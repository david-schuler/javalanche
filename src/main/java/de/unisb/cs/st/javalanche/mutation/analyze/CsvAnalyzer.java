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
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

// -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.mutation.analyze.CsvAnalyzer 
public class CsvAnalyzer implements MutationAnalyzer {

	@Override
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

		List<String> lines = new ArrayList<String>();
		lines.add(Mutation.getCsvHead() + ",Covered, Detected");
		for (Mutation mutation : mutations) {
			MutationTestResult result = mutation.getMutationResult();
			boolean covered = false;
			if (result != null) {
				covered = result.isTouched();
			}
			lines.add(mutation.getCsvString() + "," + covered + ","
					+ mutation.isDetected());

		}
		// File outputDir = ConfigurationLocator.getJavalancheConfiguration()
		// .getOutputDir();
		int entries = (lines.size() - 1);
		String fileName = "mutations-" + entries + ".csv";
		File outFile = new File(fileName);
		try {
			FileUtils.writeLines(outFile, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.format("File with %d entries written (%s) ", entries,
				outFile.getAbsolutePath());
	}

}
