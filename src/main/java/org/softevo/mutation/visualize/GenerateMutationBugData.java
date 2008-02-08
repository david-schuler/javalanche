package org.softevo.mutation.visualize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.run.analyze.MutationsClassData;
import org.softevo.mutation.tom.data.GetBugData;
import org.softevo.mutation.visualize.SlocParser.SlocEntry;

public class GenerateMutationBugData {

	private static Logger logger = Logger
			.getLogger(GenerateMutationBugData.class);

	public static void main(String[] args) {
//		generateCsvFile();
		addSlocInfo();
	}

	private static void generateCsvFile() {
		Map<String, Integer> bugMap = GetBugData.getBugDataFromDB();
		// BugsData.getBugsForClasses();
		Map<String, MutationsClassData> mutationMap = MutationsClassData
				.getMapFromFile(new File(
						MutationProperties.MUTATIONS_CLASS_RESULT_XML));

		Map<String, MutationsClassBugData> classBugData = new HashMap<String, MutationsClassBugData>();
		for (Map.Entry<String, MutationsClassData> entry : mutationMap
				.entrySet()) {
			int bugs = -1;

			if (bugMap.containsKey(entry.getKey())) {
				logger.info("Found bug data for class" + entry.getKey());
				bugs = bugMap.get(entry.getKey());
			}

			MutationsClassBugData mutationsClassBugData = new MutationsClassBugData(
					entry.getValue(), bugs, -1);
			classBugData.put(entry.getKey(), mutationsClassBugData);
		}
		MutationsClassBugData.toCsvFile(classBugData,
				new File("bugdata0.csv"));
	}

	private static void addSlocInfo() {
		Map<String, MutationsClassBugData> classBugData = MutationsClassBugData
				.fromCsvFile("bugdata.csv");

		Map<String, SlocEntry> slocMap = SlocParser.parseSlocFile();
		for (Map.Entry<String, MutationsClassBugData> entry : classBugData
				.entrySet()) {
			int sloc = -1;
			if (slocMap.containsKey(entry.getKey())) {

				sloc = slocMap.get(entry.getKey()).getSloc();
				entry.getValue().setSloc(sloc);
			}
			else{
				logger.info("Found no sloc data for class" + entry.getKey());
			}
		}
		MutationsClassBugData.toCsvFile(classBugData,
				new File("bugdata_v2.csv"));

	}
}