package org.softevo.mutation.visualize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.run.analyze.MutationsClassData;
import org.softevo.mutation.tom.data.GetBugData;

public class GenerateMutationBugData {

	private static Logger logger = Logger
			.getLogger(GenerateMutationBugData.class);

	public static void main(String[] args) {
		Map<String, Integer> bugMap = GetBugData.getBugDataFromDB();
			//BugsData.getBugsForClasses();
		Map<String, MutationsClassData> mutationMap = MutationsClassData.getMapFromFile(new File(MutationProperties.MUTATIONS_CLASS_RESULT_XML));
		Map<String, MutationsClassBugData> classBugData = new HashMap<String, MutationsClassBugData>();
		for (Map.Entry<String, MutationsClassData> entry : mutationMap
				.entrySet()) {
			int bugs = 0;
			if (bugMap.containsKey(entry.getKey())) {
				logger.info("Found bug data for class" + entry.getKey());
				bugs = bugMap.get(entry.getKey());
			}
			MutationsClassBugData mutationsClassBugData = new MutationsClassBugData(
					entry.getValue(), bugs);
			classBugData.put(entry.getKey(), mutationsClassBugData);
		}
		MutationsClassBugData.toCsvFile(classBugData, new File("bugdata.csv"));
	}
}
