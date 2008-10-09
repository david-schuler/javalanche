package de.st.cs.unisb.javalanche.analyze;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.st.cs.unisb.ds.util.io.SerializeIo;
import de.st.cs.unisb.ds.util.io.XmlIo;
import de.st.cs.unisb.javalanche.properties.MutationProperties;
import de.st.cs.unisb.javalanche.results.Mutation;
import de.st.cs.unisb.javalanche.results.MutationTestResult;

public class ExperimentAnalyzer2 implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(ExperimentAnalyzer2.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.st.cs.unisb.javalanche.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
	 */
	public String analyze(Iterable<Mutation> mutations) {
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
			if (fullDataObject instanceof ExperimentData) {
				ExperimentData fullData = (ExperimentData) fullDataObject;
				return ExperimentData2.compare(fullData, experimentData);
			} else {
				ExperimentData2 fullData = (ExperimentData2) fullDataObject;
				return ExperimentData2.compare(fullData, experimentData);
			}
		}
		return experimentData.toString();
	}
}
