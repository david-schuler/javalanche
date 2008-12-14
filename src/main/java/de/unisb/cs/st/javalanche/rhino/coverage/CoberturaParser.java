package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.google.common.base.Join;

import de.unisb.cs.st.ds.util.io.Io;

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

		File[] files = getXmlFiles();
		Map<CoverageData, String> map = parseFiles(files);
		FailureMatrix fm = FailureMatrix.parseFile(new File(
				"/scratch/schuler/subjects/ibugs_rhino-0.1/failureMatrix.csv"));
		ArrayList<CoverageData> coverageDataList = new ArrayList<CoverageData>(
				map.keySet());
		List<List<PriorizationResult>> averageList = new ArrayList<List<PriorizationResult>>();
		final int SHUFFLES = 1000;
//		for (int i = 1; i < SHUFFLES; i++) {
//			if(i %100 == 0){
//				System.out.println("Computing result " + i);
//			}
//			Collections.shuffle(coverageDataList);
//			List<PriorizationResult> prioritizedTotal = CoverageData
//					.prioritizeTotal(coverageDataList);
//			averageList
//					.add(new ArrayList<PriorizationResult>(prioritizedTotal));
//
//			// summarizePriorization(fm, prioritizedTotal, "total-coverage");
//		}
//		double[] avarageData = generateAverageData(fm, averageList);
//		writeRData(avarageData, "total-coverage");
		averageList = new ArrayList<List<PriorizationResult>>();
		for (int i = 1; i < SHUFFLES; i++) {
			if(i %100 == 0){
				System.out.println("Computing result " + i);
			}
			Collections.shuffle(coverageDataList);
			List<PriorizationResult> prioritizedAdditional = CoverageData
					.prioritizeAdditional(coverageDataList);
			averageList.add(new ArrayList<PriorizationResult>(
					prioritizedAdditional));

			// summarizePriorization(fm, prioritizedTotal, "total-coverage");
		}
		double[] avarageDataAdd = generateAverageData(fm, averageList);
		writeRData(avarageDataAdd, "additional-coverage");
		//
		//
		// summarizePriorization(fm, prioritizedAdditional,
		// "additional-coverage");

	}

	private static void writeRData(double[] avarageData,
			String prioritizationType) {
		Integer[] xarray = new Integer[avarageData.length];
		Double[] yarray = new Double[avarageData.length];
		for (int i = 0; i < avarageData.length; i++) {
			xarray[i] = i + 1;
			yarray[i] = avarageData[i];
		}
		String xJoin = Join.join(",", xarray);
		String xString = "x <- c(" + xJoin + " )";
		String yJoin = Join.join(",", yarray);
		String yString = "x <- c(" + yJoin + " )";
		System.out.println(xString);
		System.out.println(yString);
		Io.writeFile(xString + '\n' + yString, new File(prioritizationType
				+ "-average.csv"));
	}

	private static double[] generateAverageData(FailureMatrix fm,
			List<List<PriorizationResult>> averageList) {
		double[] values = new double[averageList.get(0).size()];
		for (List<PriorizationResult> singleResult : averageList) {
			Collections.reverse(singleResult);
			List<String> testList = new ArrayList<String>();
			for (int i = 0; i < singleResult.size(); i++) {
				PriorizationResult prioritizationResult = singleResult.get(i);
				testList.add(prioritizationResult.getTestName());
				int detectedFailures = fm.getDetectedFailures(testList);
				// System.out.println("Detected Failures: " + detectedFailures);
				values[i] += 1. * detectedFailures;
			}
		}
		double size = 1.* averageList.size();
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i] / size;
		}
		return values;
	}

	private static void summarizePriorization(FailureMatrix fm,
			List<PriorizationResult> prioritizedAdditional,
			String prioritizationType) {
		Collections.reverse(prioritizedAdditional);
		List<String> testList = new ArrayList<String>();
		int totalFailures = fm.getNumberOfFailures();
		int count = 0;
		logger.info("Result for: " + prioritizationType);
		StringBuilder sb = new StringBuilder();
		for (PriorizationResult prioritizationResult : prioritizedAdditional) {
			count++;
			testList.add(prioritizationResult.getTestName());
			int detectedFailures = fm.getDetectedFailures(testList);
			System.out.println(count + "  "
					+ prioritizationResult.getTestName() + " ("
					+ prioritizationResult.getInfo() + ")  -  "
					+ detectedFailures + " out of " + totalFailures
					+ " failures");
			String join = Join.join(",",
					new Object[] { count, prioritizationResult.getTestName(),
							detectedFailures, totalFailures,
							"\"" + prioritizationResult.getInfo() + "\"" });
			sb.append(join).append('\n');
		}
		Io.writeFile(sb.toString(), new File(prioritizationType + ".csv"));
	}

	private static Map<CoverageData, String> parseFiles(File[] files) {
		Map<CoverageData, String> map = new HashMap<CoverageData, String>();
		int count = 0;
		for (File file : files) {
			count++;
			CoverageData coverageData = parseFile(file);
			String name = parseTestName(file);
			coverageData.setTestName(parseTestName(file));
			logger.info("Test " + name + "(" + count + ") covered "
					+ coverageData.getNumberOfCoveredLines() + "  lines ");
			map.put(coverageData, name);

		}
		return map;
	}

	private static File[] getXmlFiles() {
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
		return files;
	}

	private static String parseTestName(File file) {
		String fileName = file.getName();
		String parseErr = "PARSE ERROR ";
		String substring = parseErr + fileName;
		try {
			substring = fileName.substring("coverage-".length(), fileName
					.lastIndexOf('.'));
		} catch (IndexOutOfBoundsException e) {
			logger.warn("Could not find testname for " + file, e);
		}
		if (substring == null || substring.length() == 0) {
			substring = parseErr + fileName;
		}
		String result = substring.replace('_', '/');
		result = result.replace("js1/5", "js1_5");
		result = result.replace("js1/2", "js1_2");
		result = result.replace("ecma/3", "ecma_3");
		result = result.replace("ecma/2", "ecma_2");
		return result;
	}

	private static CoverageData parseFile(File file) {
		StopWatch sw = new StopWatch();
		sw.start();
		CoberturaHandler handler = new CoberturaHandler();

		// Parse the file using the handler
		parseXmlFile(file, handler, false);
		CoverageData coverageData = handler.getCoverageData();

		sw.stop();
		logger.info("Parser took: "
				+ DurationFormatUtils.formatDurationHMS(sw.getTime()));

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
