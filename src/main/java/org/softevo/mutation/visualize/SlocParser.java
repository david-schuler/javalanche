package org.softevo.mutation.visualize;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.Io;

public class SlocParser {

	public static class SlocEntry {
		private int sloc;

		private String className;

		public SlocEntry(int sloc, String className) {
			super();
			this.sloc = sloc;
			this.className = className;
		}

		/**
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @return the sloc
		 */
		public int getSloc() {
			return sloc;
		}

	}

	private static Logger logger = Logger.getLogger(SlocParser.class);

	public static void main(String[] args) {
		parseSlocFile();
	}

	public static Map<String, SlocEntry> parseSlocFile() {
		List<String> lines = Io.getLinesFromFile(new File(
				"src/main/resources/aspectj-sloc.txt"));
		logger.info(lines.size());
		Map<String, SlocEntry> slocData = new HashMap<String,SlocEntry>();
		for (String line : lines) {
			SlocEntry slocEntry = parseLine(line);
			if (slocEntry != null) {
				slocData.put(slocEntry.getClassName(), slocEntry);
			}
		}
		logger.info("Aspectj files: " + slocData.size());
		return slocData;
	}

	private static SlocEntry parseLine(String line) {
		String[] split = line.split("\\s+");
		SlocEntry returnEntry = null;
		if (split[1].equals("java")) {
			String className = BugsData.getClassNameFromFilename(split[3]);
			int lines = Integer.valueOf(split[0]);
			if(className.startsWith("org.aspect")){
				returnEntry = new SlocEntry(lines,className);
			}
		}
		return returnEntry;
	}
}
