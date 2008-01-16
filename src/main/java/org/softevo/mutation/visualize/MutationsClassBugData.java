package org.softevo.mutation.visualize;

import java.io.File;
import java.util.Map;

import org.softevo.mutation.io.Io;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.run.analyze.MutationsClassData;

public class MutationsClassBugData {

	private String className;

	private int numberOfBugs;

	private int mutationsTotal;

	private int mutationsKilled;

	private int mutationsSurvived;

	public MutationsClassBugData(MutationsClassData classData, int bugsForClass) {
		className = classData.getClassName();
		mutationsTotal = classData.getMutationsTotal();
		mutationsSurvived = classData.getMutationsSurvived();
		mutationsKilled = classData.getMutationsKilled();
		this.numberOfBugs = bugsForClass;
	}

	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(className);
		sb.append(',');
		sb.append(numberOfBugs);
		sb.append(',');
		sb.append(mutationsTotal);
		sb.append(',');
		sb.append(mutationsKilled);
		sb.append(',');
		sb.append(mutationsSurvived);
		return sb.toString();
	}

	public static void saveToXMLFile(Map<String, MutationsClassBugData> map,
			File file) {
		XmlIo.toXML(map, file);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, MutationsClassBugData> fromXML(File file) {
		return (Map<String, MutationsClassBugData>) XmlIo.fromXml(file);
	}

	public static void toCsvFile(Map<String, MutationsClassBugData> map,
			File file) {
		StringBuilder sb = new StringBuilder();
		sb
				.append("className,bug,mutationsTotal,mutationsKilled,mutationsSurvived");
		for (MutationsClassBugData mcbd : map.values()) {
			sb.append(mcbd.toCSV());
			sb.append('\n');
		}
		Io.writeFile(sb.toString(), file);
	}

}
