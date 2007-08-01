package org.softevo.mutation.coverageResults.clover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.softevo.mutation.coverageResults.CoverageResult;
import org.softevo.mutation.io.HtmlFileSource;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseCloverResults {

	private static Logger logger = Logger.getLogger(ParseCloverResults.class
			.toString());

	private static final String RELATIVE_CLOVER_DIR = "run-all-junit-tests/clover_html/";

	private static Pattern coveragePattern = Pattern
			.compile("\"sl\":\\s(\\d{1,5})");

	// private static Pattern classNamePattern = Pattern
	// .compile("\"id\" .*\"el\" .*\"name\"\\s+:\\s+\"(.*)\"");

	private static Pattern testNamePattern = Pattern
			.compile("\"name\"\\s:\\s\"(.+)\",");

	private static Pattern testIDPattern = Pattern.compile("\"test_(\\d+)\"");

	private static class TestSaxHandler extends DefaultHandler {

		private Map<String, String> tests = new HashMap<String, String>();

		private boolean waitForTestClassName = false;

		private boolean waitForText = false;

		private String tempID;

		private String tempfullTestCaseName;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("td")) {
				int classIndex = attributes.getIndex("class");
				if (classIndex >= 0) {
					if (attributes.getValue(classIndex).equals("testCase")) {
						String id = attributes.getValue("id");
						if (id.startsWith("tc-")) {
							tempID = id.substring(3);
						}
						waitForTestClassName = true;
					}
				}
			}
			if (waitForTestClassName) {
				if (qName.equals("span")
						&& attributes.getValue("class").equals("sortValue")) {
					waitForText = true;
					waitForTestClassName = false;
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (waitForText && qName.equals("span")) {
				waitForText = false;
				tests.put(tempID, tempfullTestCaseName);
				tempfullTestCaseName = null;
				tempID = null;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (waitForText) {
				String fullTestCaseName = new String(ch, start, length);
				if (tempfullTestCaseName != null) {
					tempfullTestCaseName = tempfullTestCaseName
							+ fullTestCaseName;
				} else {
					tempfullTestCaseName = fullTestCaseName;
				}
			}
		}

		@SuppressWarnings("unchecked")
		public Map<String, String> getTestCases() {
			return (Map<String, String>) ((HashMap<String, String>) tests)
					.clone();
		}
	}

	public static Map<String, CoverageResult> parseResults() {
		Map<String, CoverageResult> results = new HashMap<String, CoverageResult>();
		Collection<File> files = null;
		try {
			files = HtmlFileSource.getHtmlFiles(new File(
					MutationProperties.CLOVER_REPORT_DIR));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (File file : files) {
			try {
				String classname = getClassname(file.getAbsolutePath());
				logger.log(Level.INFO, "ClassName: " + classname);
				Map<String, String> testIDs = null;
				BufferedReader in = new BufferedReader(new FileReader(file));
				boolean collectLines = false;
				CoverageResult coverageResult = new CoverageResult(classname);
				String testName = null;
				String id = null;
				while (in.ready()) {
					String line = in.readLine();
					if (line.contains("var testTargets")) {
						collectLines = true;
					}
					if (collectLines) {
						if (line.contains("\"test_")) {
							id = getTestID(line);
							if (testIDs == null) {
								testIDs = getTestIds(file);
							}
							assert testIDs.containsKey(id);

						}
						if (line.contains("\"name\"")) {
							testName = getTestName(line);
							if (testIDs == null) {
								testIDs = getTestIds(file);
							}
							logger.log(Level.INFO, id + " --- " + testName
									+ " --- " + testIDs.get(id));
							assert testIDs.get(id).contains(testName);
							testName = testIDs.get(id);
						}
						if (line.contains("\"statements\"")) {
							List<Integer> lineList = collectLines(line);
							coverageResult.addTestCase(testName, lineList);
						}

					}
					if (line.contains("var srcFileLines")) {
						collectLines = false;
					}
				}
				in.close();
				results.put(classname, coverageResult);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	private static Map<String, String> getTestIds(File f) {
		TestSaxHandler testSaxHandler = new TestSaxHandler();
		parseXmlFile(f, testSaxHandler);
		Map<String, String> testIDs = testSaxHandler.getTestCases();
		return testIDs;
	}

	private static String getTestID(String line) {
		Matcher m = testIDPattern.matcher(line);
		if (m.find()) {
			return m.group(1);
		} else {
			throw new RuntimeException("Regular Expression does not match: "
					+ line);
		}
	}

	public static Document parseXmlFile(File file, boolean validating) {
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(validating);
			// Create the builder and parse the file
			Document doc = factory.newDocumentBuilder().parse(file);
			return doc;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getTestName(String line) {
		Matcher m = testNamePattern.matcher(line);
		String result = null;
		if (m.find()) {
			result = m.group(1);
		} else {
			throw new RuntimeException("Regular Expression does not match: "
					+ line);
		}
		return result;
	}

	private static String getClassname(String absolutePath) {
		int index = absolutePath.indexOf(RELATIVE_CLOVER_DIR);
		String filename = absolutePath.substring(index
				+ RELATIVE_CLOVER_DIR.length(), absolutePath.length() - 5);
		return filename.replace('/', '.');
	}

	public static void parseXmlFile(File file, DefaultHandler handler) {
		try {
			// Create a builder factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);

			// Create the builder and parse the file
			factory.newSAXParser().parse(file, handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Integer> collectLines(String line) {
		Matcher m = coveragePattern.matcher(line);
		List<Integer> lineNumbers = new ArrayList<Integer>();
		while (m.find()) {
			Integer lineNumber = new Integer(m.group(1));
			lineNumbers.add(lineNumber);
		}
		return lineNumbers;
	}

	public static void main(String[] args) {
		Object o = parseResults();
		XmlIo.toXML(o, new File(MutationProperties.CLOVER_RESULTS_FILE));
		logger.info("Parsing Finished");
	}
}
