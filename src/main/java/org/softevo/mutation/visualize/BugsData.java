package org.softevo.mutation.visualize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BugsData {

	private static final String IBUGS_XML_FILE = "/Users/schuler/Desktop/firefox/ibugs_aspectj-1.1/repository.xml";

	private static final String ASPECTJ_PREFIX = "org.aspectj";

	private static String ASPECTJ_DIRECTORY_STRING = "org/aspectj/";

	private static class IBugsSaxHandler extends DefaultHandler {

		private List<String> filenames = new ArrayList<String>();

		private boolean waitForFixedFiles;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("fixedFiles")) {
				waitForFixedFiles = true;
			} else if (waitForFixedFiles && qName.equals("file")) {
				String filename = attributes.getValue("name");
				filenames.add(filename);
				// System.out.println(filename);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equals("fixedFiles")) {
				waitForFixedFiles = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

		}

		public List<String> getFileNames() {
			return filenames;
		}

	}

	public static void main(String[] args) {
		Map<String, Integer> map = getIBugsDataForClasses();
		printBugsForClasses(map);
	}

	public static Map<String, Integer> getIBugsDataForClasses() {
		File file = new File(IBUGS_XML_FILE);
		IBugsSaxHandler ibugsHandler = new IBugsSaxHandler();
		parseXmlFile(file, ibugsHandler);
		List<String> files = ibugsHandler.getFileNames();

		List<String> testClasses = new ArrayList<String>();
		List<String> aspectjClasses = new ArrayList<String>();
		List<String> rest = new ArrayList<String>();
		for (String filename : files) {
			if (filename.startsWith(ASPECTJ_PREFIX)
					&& filename.endsWith("java")) {

				String fnew = getClassNameFromFilename(filename);
				if (fnew.toLowerCase().contains("test")) {
					testClasses.add(fnew);
				} else {
					aspectjClasses.add(fnew);
				}
			} else {
				rest.add(filename);
			}
		}
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (String name : aspectjClasses) {
			if (map.containsKey(name)) {
				map.put(name, map.get(name) + 1);
			} else {
				map.put(name, 1);
			}
		}
		return map;
	}

	/**
	 * Returns the the classname (seperated with dots) from the filename, if it
	 * liese in the directrorystructure (../org/aspectj/...)
	 *
	 * @param filename
	 *            The full name of the file.
	 * @return dot seperated classname
	 */
	public static String getClassNameFromFilename(String filename) {
		String newFileName = filename;
		if (filename.contains(ASPECTJ_DIRECTORY_STRING)) {
			int startPosition = filename.indexOf(ASPECTJ_DIRECTORY_STRING);
			newFileName = filename.substring(startPosition, filename.length()
					- ".java".length());
			newFileName = newFileName.replace('/', '.');

		}
		return newFileName;
	}

	private static void printBugsForClasses(Map<String, Integer> map) {
		Set<Entry<String, Integer>> entrySet = map.entrySet();
		SortedSet<Entry<String, Integer>> sortedEntrySet = new TreeSet<Entry<String, Integer>>(
				new Comparator<Entry<String, Integer>>() {
					public int compare(Entry<String, Integer> o1,
							Entry<String, Integer> o2) {
						return o1.getValue() - o2.getValue();
					}
				});
		sortedEntrySet.addAll(entrySet);
		for (Entry<String, Integer> entry : sortedEntrySet) {
			System.out.println(entry.getKey() + "   " + entry.getValue());
		}
	}

	private static void parseXmlFile(File file, DefaultHandler handler) {
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

}
