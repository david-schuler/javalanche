package org.softevo.mutation.analyze;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.MutationTestResult;

public class ExperimentAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(ExperimentAnalyzer.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.softevo.mutation.analyze.MutationAnalyzer#analyze(java.lang.Iterable)
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
				} else {
					if (mt.isTouched()) {
						survivedNonViolatedCoveredIds.add(m.getId());
					}
				}
			}
		}
		ExperimentData experimentData = new ExperimentData(caughtIds,
				survivedTotalIds, survivedViolatedIds,
				survivedNonViolatedCoveredIds);

		XmlIo
				.toXML(experimentData,
						MutationProperties.EXPERIMENT_DATA_FILENAME);
		File fullDataFile = new File("fullData.xml");
		if(fullDataFile.exists()){
			ExperimentData fullData = (ExperimentData) XmlIo.fromXml(fullDataFile);
			return ExperimentData.compare(fullData, experimentData);
		}
		return experimentData.toString();
	}
}
