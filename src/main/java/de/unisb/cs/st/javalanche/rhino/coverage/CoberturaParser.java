package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CoberturaParser {

	static class CoberturaHandler extends DefaultHandler {
		private String className;

		private boolean linesStart;

		private List<Integer> lines;

		private CoverageData coverageData = new CoverageData();

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (name.equals("class")) {
				className = attributes.getValue("name");
			}
			if (name.equals("lines")) {
				linesStart = true;
				lines = new ArrayList<Integer>();
			}
			if (linesStart && name.equals("line")) {
				int hits = Integer.parseInt(attributes.getValue("hits"));
				if (hits > 0) {
					String value = attributes.getValue("number");
					lines.add(Integer.valueOf(value));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {

			super.endElement(uri, localName, name);
			if (name.equals("class")) {
				ClassData classData = new ClassData(className, lines);
				className = null;
				lines = new ArrayList<Integer>();
				coverageData.add(classData);
			}
			if (name.equals("lines")) {
				linesStart = false;
			}
		}

		/**
		 * @return the coverageData
		 */
		public CoverageData getCoverageData() {
			return coverageData;
		}
	}

	private static Logger logger = Logger.getLogger(CoberturaParser.class);

	public static void main(String[] args) {
		String dirName = "/scratch/schuler/subjects/ibugs_rhino-0.1/coverage-report/";
		File dir = new File(dirName);
		File[] files = dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name.endsWith("xml")) {
					return true;
				}
				return false;
			}

		});
		Map<CoverageData, String> map = new HashMap<CoverageData, String>();
		for (File file : files) {
			CoverageData coverageData = parseFile(file);
			String name = parseTestName(file);
			map.put(coverageData, name);
		}
		List<CoverageData> sortedList = new ArrayList<CoverageData>(map
				.keySet());
		CoverageData.prioritize(sortedList);
		int count = 0;
		for (CoverageData coverageData : sortedList) {
			count++;
			String testname = map.get(coverageData);
			System.out.println(count + "  " + testname + " "
					+ coverageData.getNumberOfCoveredLines());
		}
	}

	private static String parseTestName(File file) {
		String fileName = file.getName();
		String substring = "NO NAME " + fileName;
		try {
			substring = fileName.substring("coverage-".length(), fileName
					.lastIndexOf('.'));
		} catch (IndexOutOfBoundsException e) {
			logger.warn("Could not find testname for " + fileName, e);
		}
		if(substring == null || substring.length() == 0){
			substring = "NO NAME " + fileName;
		}
		return substring.replace('_', '/');

	}

	private static CoverageData parseFile(File file) {
		StopWatch sw = new StopWatch();
		sw.start();
		CoberturaHandler handler = new CoberturaHandler();

		// Parse the file using the handler
		parseXmlFile(file, handler, false);
		CoverageData coverageData = handler.getCoverageData();
		int numberOfCoveredLines = coverageData.getNumberOfCoveredLines();
		sw.stop();
		logger.info("Parser took: "
				+ DurationFormatUtils.formatDurationHMS(sw.getTime()));
		logger.info(numberOfCoveredLines);
		return coverageData;
	}

	public static void parseXmlFile(File file, DefaultHandler handler,
			boolean validating) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(validating);

		try {
			factory.newSAXParser().parse(file, handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
