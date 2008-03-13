package org.softevo.mutation.visualize;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.run.analyze.MutationsClassData;

public class MutationsClassBugData {

	private static Logger logger = Logger
			.getLogger(MutationsClassBugData.class);

	private String className;

	private int mutationsTotal;

	private int mutationsKilled;

	private int mutationsSurvived;

	private int numberOfBugs;

	private int sloc;

	public MutationsClassBugData(String className, int mutationsTotal,
			int mutationsKilled, int mutationsSurvived, int numberOfBugs,
			int sloc) {
		super();
		this.className = className;
		this.mutationsTotal = mutationsTotal;
		this.mutationsKilled = mutationsKilled;
		this.mutationsSurvived = mutationsSurvived;
		this.numberOfBugs = numberOfBugs;
		this.sloc = sloc;
	}

	public MutationsClassBugData(MutationsClassData classData,
			int bugsForClass, int sloc) {
		className = classData.getClassName();
		mutationsTotal = classData.getMutationsTotal();
		mutationsSurvived = classData.getMutationsSurvived();
		mutationsKilled = classData.getMutationsKilled();
		this.numberOfBugs = bugsForClass;
		this.sloc = sloc;
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
		sb.append(',');
		sb.append(sloc);
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
				.append("className,bug,mutationsTotal,mutationsKilled,mutationsSurvived,sloc\n");
		for (MutationsClassBugData mcbd : map.values()) {
			sb.append(mcbd.toCSV());
			sb.append('\n');
		}
		Io.writeFile(sb.toString(), file);
	}

	public static Map<String, MutationsClassBugData> fromCsvFile(
			InputStream inputStream) {
		List<String> lines = Io.getLinesFromFile(inputStream);
		return readFromCSVLines(lines);
	}

	public static Map<String, MutationsClassBugData> fromCsvFile(String filename) {
		return fromCsvFile(new File(filename));
	}

	public static Map<String, MutationsClassBugData> fromCsvFile(File file) {
		List<String> lines = Io.getLinesFromFile(file);
		return readFromCSVLines(lines);
	}

	private static Map<String, MutationsClassBugData> readFromCSVLines(
			List<String> lines) {
		logger.info(lines.size());
		Map<String, MutationsClassBugData> data = new HashMap<String, MutationsClassBugData>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			MutationsClassBugData mutationsClassBugData = parseLine(line);
			if (mutationsClassBugData != null) {
				data.put(mutationsClassBugData.getClassName(),
						mutationsClassBugData);
			}
		}
		logger.info("Total Files: " + data.size());
		return data;
	}

	private static MutationsClassBugData parseLine(String line) {
		String[] split = line.split(",");
		String className = split[0];
		int numberOfBugs = Integer.valueOf(split[1]);
		int mutationsTotal = Integer.valueOf(split[2]);
		int mutationsKilled = Integer.valueOf(split[3]);
		int mutationsSurvived = Integer.valueOf(split[4]);
		int sloc = -1;
		if (split.length > 5) {
			sloc = Integer.valueOf(split[5]);
		}
		return new MutationsClassBugData(className, mutationsTotal,
				mutationsKilled, mutationsSurvived, numberOfBugs, sloc);
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the sloc
	 */
	public int getSloc() {
		return sloc;
	}

	/**
	 * @param sloc
	 *            the sloc to set
	 */
	public void setSloc(int sloc) {
		this.sloc = sloc;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @return the mutationsKilled
	 */
	public int getMutationsKilled() {
		return mutationsKilled;
	}

	/**
	 * @return the mutationsSurvived
	 */
	public int getMutationsSurvived() {
		return mutationsSurvived;
	}

	/**
	 * @return the mutationsTotal
	 */
	public int getMutationsTotal() {
		return mutationsTotal;
	}

	/**
	 * @return the numberOfBugs
	 */
	public int getNumberOfBugs() {
		return numberOfBugs;
	}

}
