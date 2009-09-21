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
package de.unisb.cs.st.javalanche.mutation.analyze.splitExperiment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class ExperimentAnalyzer2 implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(ExperimentAnalyzer2.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		if (MutationProperties.EXPERIMENT_DATA_FILENAME == null
				|| MutationProperties.EXPERIMENT_DATA_FILENAME.length() == 0) {
			String message = "No filename given. It should be specified in property "
					+ MutationProperties.EXPERIMENT_DATA_FILENAME_KEY;
			logger.warn(message);
			return message;
		}
		Set<Long> caughtIds = new HashSet<Long>();
		Set<Long> survivedTotalIds = new HashSet<Long>();
		Set<Long> survivedViolatedIds = new HashSet<Long>();
		Map<Long, Mutation> survivedViolatedMap = new HashMap<Long, Mutation>();
		Set<Long> survivedNonViolatedCoveredIds = new HashSet<Long>();
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				caughtIds.add(m.getId());
			} else {
				survivedTotalIds.add(m.getId());
			}

			MutationTestResult mt = m.getMutationResult();
			if (mt != null && !m.isKilled()) {
				if (mt.getTotalViolations() > 0) {
					survivedViolatedIds.add(m.getId());
					survivedViolatedMap.put(m.getId(), m);
				} else {
					if (mt.isTouched()) {
						survivedNonViolatedCoveredIds.add(m.getId());
					}
				}
			}
		}
		ExperimentData2 experimentData = new ExperimentData2(caughtIds,
				survivedTotalIds, survivedViolatedIds,
				survivedNonViolatedCoveredIds, survivedViolatedMap);

		XmlIo
				.toXML(experimentData,
						MutationProperties.EXPERIMENT_DATA_FILENAME);
		SerializeIo.serializeToFile(experimentData,
				MutationProperties.EXPERIMENT_DATA_FILENAME.replace("xml",
						"ser"));

		File fullDataFile = new File("fullData.xml");
		if (fullDataFile.exists()) {
			Object fullDataObject = XmlIo.fromXml(fullDataFile);
			ExperimentData2 fullData = (ExperimentData2) fullDataObject;
			return ExperimentData2.compare(fullData, experimentData);
		}
		return experimentData.toString();
	}
}
