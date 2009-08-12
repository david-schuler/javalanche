package de.unisb.cs.st.javalanche.tracer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/* TODO rewrite of processTest* and processMutation*
 * Maybe it is possible to merge both methods somehow.
 * Another trick would be to read the whole data in one step and analyze in
 * a second step.
 */

/**
 * @author Bernhard Gruen
 * 
 */
public class NewTracerAnalyzer implements MutationAnalyzer {

	private static class MutationCache {
		public long id;
		public boolean killed;
		public boolean classInit;
		public String shortString;
		public String className;
		public String methodName;
		public int lineNumber;
		public int mutationForLine;
		public String mutationType;
		public String mutationResult;

		public static MutationCache create(Mutation m) {
			MutationCache mc = new MutationCache();
			mc.id = m.getId();
			// mc.shortString = m.toShortString();
			mc.killed = m.isKilled();
			mc.classInit = m.isClassInit();
			mc.className = m.getClassName();
			mc.lineNumber = m.getLineNumber();
			mc.mutationForLine = m.getMutationForLine();
			mc.mutationType = m.getMutationType().toString();
			mc.mutationResult = m.getMutationResult() != null ? m
					.getMutationResult().toShortString()
					: TracerProperties.NO_RESULT;
			return mc;
		}
	}

	private static class TracerResult {
		// The summarized number of classes called by the run/mutation
		// only line coverage for the modified value!
		public int classesTotal = 0;
		public int classesModified = 0;

		// The summarized number of classes called by the run/mutation;
		public int methodsTotal = 0;
		public int methodsModifiedLine = 0;
		public int methodsModifiedData = 0;
		public int methodsModifiedAll = 0;

		// The same as above but without self (W/O Self)
		public int methodsModifiedLineWOS = 0;
		public int methodsModifiedDataWOS = 0;
		public int methodsModifiedAllWOS = 0;

		// The summarized number of lines called by the run/mutation
		// only line coverage for the modified value!
		public int linesTotal = 0;
		public int linesModified = 0;

		// The number of tests
		public int testsTotal = 0;
		public int testsExecuted = 0;

		// The number of data
		public int dataTotal = 0;
		public int dataModified = 0;
	}

	private static final Logger logger = Logger
			.getLogger(NewTracerAnalyzer.class);

	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalLineCoverageMaps = null;
	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalDataCoverageMaps = null;

	boolean firstCallToWriteOut = true;
	PrintStream out = null;

	// (not-)killed by all criterions (line + data)
	ArrayList<Number> killed = new ArrayList<Number>();
	ArrayList<Number> notKilled = new ArrayList<Number>();

	// (not-)killed by line criterion
	ArrayList<Number> killedLine = new ArrayList<Number>();
	ArrayList<Number> notKilledLine = new ArrayList<Number>();

	// (not-)killed by data criterion
	ArrayList<Number> killedData = new ArrayList<Number>();
	ArrayList<Number> notKilledData = new ArrayList<Number>();

	DecimalFormat dec = new DecimalFormat("###.##");

	LinkedBlockingQueue<MutationCache> lbq = new LinkedBlockingQueue<MutationCache>();

	/*
	 * Theses two Sets contain excluded method names. So those methods don't
	 * change the impact even if their traces are different.
	 */

	HashSet<String> dontInstrumentSet = loadDontInstrument();
	HashSet<String> differencesSet = loadDifferences();

	private int outcount;

	private static HashSet<String> loadDontInstrument() {
		if (new File(TracerProperties.TRACE_DONT_INSTRUMENT_FILE).exists()) {
			return XmlIo.get(TracerProperties.TRACE_DONT_INSTRUMENT_FILE);
		}
		return new HashSet<String>();
	}

	private static HashSet<String> loadDifferences() {
		if (new File(TracerProperties.TRACE_DIFFERENCES_FILE).exists()) {
			return XmlIo.get(TracerProperties.TRACE_DIFFERENCES_FILE);
		}
		return new HashSet<String>();
	}

	/*
	 * *************************************************************************
	 * Helper method to load the original (id=0) line and data coverage traces.
	 */
	private void loadOriginalTraces() {
		originalLineCoverageMaps = loadLineCoverageTrace("0");
		originalDataCoverageMaps = loadDataCoverageTrace("0");
	}

	/*
	 * *************************************************************************
	 * Helper method to load an arbitrary trace (line or data coverage)
	 */
	private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadTrace(
			String path, String mutation_dir) {
		ObjectInputStream ois = null;
		path += mutation_dir + "/";

		File dir = new File(path);
		String[] originalTests = dir.list();

		int numClasses, numLines;
		String className;

		HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
		HashMap<String, HashMap<Integer, Integer>> classMap;
		HashMap<Integer, Integer> lineMap;

		for (String test : originalTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(
						new GZIPInputStream(new FileInputStream(path + test))));
				numClasses = ois.readInt();
				classMap = new HashMap<String, HashMap<Integer, Integer>>();
				for (int i = 0; i < numClasses; i++) {
					className = ois.readUTF();
					numLines = ois.readInt();
					lineMap = new HashMap<Integer, Integer>();
					for (int j = 0; j < numLines; j++) {
						lineMap.put(ois.readInt(), ois.readInt());
					}
					classMap.put(className, lineMap);

				}
				map.put(test, classMap);
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/*
	 * *************************************************************************
	 * Helper method to load an arbitrary line coverage trace.
	 */
	protected HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadLineCoverageTrace(
			String mutation_dir) {
		return loadTrace(TracerProperties.TRACE_RESULT_LINE_DIR, mutation_dir);
	}

	/*
	 * *************************************************************************
	 * Helper method to load an arbitrary data coverage trace.
	 */
	protected HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadDataCoverageTrace(
			String mutation_dir) {
		return loadTrace(TracerProperties.TRACE_RESULT_DATA_DIR, mutation_dir);
	}

	/*
	 * *************************************************************************
	 * Method to write out results as csv data for a given mutation and result.
	 * This method is synchronized as we have to ensure that each thread can
	 * write its own data (a whole line of csv data) to the file.
	 */
	private synchronized void writeOut(MutationCache mutation,
			TracerResult results) {
		if (mutation.killed) {
			killed.add(results.methodsModifiedAll);
			killedLine.add(results.methodsModifiedLine);
			killedData.add(results.methodsModifiedData);
		} else {
			notKilled.add(results.methodsModifiedAll);
			notKilledLine.add(results.methodsModifiedLine);
			notKilledData.add(results.methodsModifiedData);
		}

		if (firstCallToWriteOut) {
			out
					.println("ID;KILLED;"
							+ "CLASSES_TOTAL;CLASSES_MODIFIED;"
							+ "METHODS_TOTAL;"
							+ "METHODS_MODIFIED_LINE;METHODS_MODIFIED_DATA;METHODS_MODIFIED_ALL;"
							+ "METHODS_MODIFIED_LINE_WOS;METHODS_MODIFIED_DATA_WOS;METHODS_MODIFIED_ALL_WOS;"
							+ "LINES_TOTAL;LINES_MODIFIED;"
							+ "DATA_TOTAL;DATA_MODIFIED;"
							+ "TESTS_TOTAL;TESTS_EXECUTED;"
							+ "MUTATION_TYPE;CLASS_NAME;METHOD_NAME;LINE_NUMBER;MUTATION_FOR_LINE;"
							+ "CLASS_INIT;MUTATION_RESULT");
			firstCallToWriteOut = false;
		}
		logger.debug("ID: " + mutation.id + "\tKilled: " + mutation.killed
				+ "\tValue: " + (results.methodsModifiedAll) + " ("
				+ mutation.mutationType + ")");
		outcount++;
		if (outcount % 500 == 0) {
			logger.info("Written results for " + outcount + " mutations");
		}
		out.println(mutation.id + ";" + mutation.killed + ";"
				+ results.classesTotal + ";" + results.classesModified + ";"
				+ results.methodsTotal + ";" + results.methodsModifiedLine
				+ ";" + results.methodsModifiedData + ";"
				+ results.methodsModifiedAll + ";"
				+ results.methodsModifiedLineWOS + ";"
				+ results.methodsModifiedDataWOS + ";"
				+ results.methodsModifiedAllWOS + ";" + results.linesTotal
				+ ";" + results.linesModified + ";" + results.dataTotal + ";"
				+ results.dataModified + ";" + results.testsTotal + ";"
				+ results.testsExecuted + ";" + mutation.mutationType + ";"
				+ mutation.className + ";" + mutation.methodName + ";"
				+ mutation.lineNumber + ";" + mutation.mutationForLine + ";"
				+ mutation.classInit + ";" + mutation.mutationResult);

	}

	/*
	 * *************************************************************************
	 * Method that processes a single line coverage test and returns its results
	 * to the calling mutation test method.
	 */
	private void processTestLineCoverage(
			HashMap<String, HashMap<Integer, Integer>> classMap,
			ObjectInputStream ois,
			HashMap<String, HashMap<Integer, Boolean>> modified)
			throws IOException {
		int numClasses, numLines;
		String className;

		HashMap<Integer, Integer> lineMap;

		int lineKey, lineValue;

		double value = 0;

		Integer tmp;
		int originalValue;

		HashSet<Integer> doneLines;
		HashSet<String> doneClasses;

		numClasses = ois.readInt();

		HashMap<Integer, Boolean> lineSet;

		// process Classes
		doneClasses = new HashSet<String>();
		for (int i = 0; i < numClasses; i++) {
			className = ois.readUTF();
			doneClasses.add(className);

			lineMap = classMap.get(className);
			numLines = ois.readInt();

			// process Lines
			doneLines = new HashSet<Integer>();

			lineSet = modified.get(className);
			if (lineSet == null) {
				lineSet = new HashMap<Integer, Boolean>();
			}
			for (int j = 0; j < numLines; j++) {
				lineKey = ois.readInt();
				doneLines.add(lineKey);

				lineValue = ois.readInt();
				originalValue = 0;
				if (lineMap != null) {
					tmp = lineMap.get(lineKey);
					if (tmp == null) {
						originalValue = 0;
					} else {
						originalValue = tmp;
					}
				}
				value = Math.abs(originalValue - lineValue);

				if (value != 0) {
					lineSet.put(lineKey, true);
				} else if (lineSet.get(lineKey) == null
						|| !lineSet.get(lineKey)) {
					lineSet.put(lineKey, false);
				} else {
					lineSet.put(lineKey, true);
				}

			}
			if (lineMap != null) {
				// process Lines only found in the original map
				Iterator<Integer> it = lineMap.keySet().iterator();
				while (it.hasNext()) {
					tmp = it.next();
					if (doneLines.contains(tmp)) {
						continue;
					}
					lineSet.put(tmp, true);
				}
			}
			modified.put(className, lineSet);
		}

		// process Classes only found in the original map
		Iterator<String> itS = classMap.keySet().iterator();
		String tmpS;
		while (itS.hasNext()) {
			tmpS = itS.next();
			if (doneClasses.contains(tmpS)) {
				continue;
			}

			lineMap = classMap.get(tmpS);

			Iterator<Integer> itI = lineMap.keySet().iterator();

			lineSet = modified.get(tmpS);
			if (lineSet == null) {
				lineSet = new HashMap<Integer, Boolean>();
			}
			while (itI.hasNext()) {
				tmp = itI.next();
				lineSet.put(tmp, true);
			}
			modified.put(tmpS, lineSet);
		}
	}

	/*
	 * *************************************************************************
	 * Method that processes a single data coverage test and returns its results
	 * to the calling mutation test method.
	 */
	private void processTestDataCoverage(
			HashMap<String, HashMap<Integer, Integer>> classMap,
			ObjectInputStream ois,
			HashMap<String, HashMap<Integer, Boolean>> modified)
			throws IOException {

		int numClasses, numLines;
		String className;

		HashMap<Integer, Integer> lineMap;

		int lineKey, lineValue;

		double value = 0;

		Integer tmp;
		int originalValue;

		HashSet<Integer> doneLines;
		HashSet<String> doneClasses;

		numClasses = ois.readInt();

		HashMap<Integer, Boolean> lineSet;

		// process Classes
		doneClasses = new HashSet<String>();
		for (int i = 0; i < numClasses; i++) {
			className = ois.readUTF();
			doneClasses.add(className);

			lineMap = classMap.get(className);
			numLines = ois.readInt();

			// process Lines
			doneLines = new HashSet<Integer>();

			lineSet = modified.get(className);
			if (lineSet == null) {
				lineSet = new HashMap<Integer, Boolean>();
			}
			for (int j = 0; j < numLines; j++) {
				lineKey = ois.readInt();
				doneLines.add(lineKey);

				lineValue = ois.readInt();
				originalValue = 0;
				if (lineMap != null) {
					tmp = lineMap.get(lineKey);
					if (tmp == null) {
						originalValue = 0;
					} else {
						originalValue = tmp;
					}
				}
				value = Math.abs(originalValue - lineValue);

				if (value != 0) {
					lineSet.put(lineKey, true);
				} else if (lineSet.get(lineKey) == null
						|| !lineSet.get(lineKey)) {
					lineSet.put(lineKey, false);
				} else {
					lineSet.put(lineKey, true);
				}

			}
			if (lineMap != null) {
				// process Lines only found in the original map
				Iterator<Integer> it = lineMap.keySet().iterator();
				while (it.hasNext()) {
					tmp = it.next();
					if (doneLines.contains(tmp)) {
						continue;
					}
					lineSet.put(tmp, true);

				}
			}
			modified.put(className, lineSet);
		}

		// process Classes only found in the original map
		Iterator<String> itS = classMap.keySet().iterator();
		String tmpS;
		while (itS.hasNext()) {
			tmpS = itS.next();
			if (doneClasses.contains(tmpS)) {
				continue;
			}

			lineMap = classMap.get(tmpS);

			Iterator<Integer> itI = lineMap.keySet().iterator();

			lineSet = modified.get(tmpS);
			if (lineSet == null) {
				lineSet = new HashMap<Integer, Boolean>();
			}
			while (itI.hasNext()) {
				tmp = itI.next();
				lineSet.put(tmp, true);
			}
			modified.put(tmpS, lineSet);
		}
	}

	/*
	 * *************************************************************************
	 * Helper method to find the mutated method given a line number in some
	 * class.
	 */
	private String findMutatedMethod(MutationCache mutation) {
		Iterator<String> it = originalLineCoverageMaps.keySet().iterator();
		while (it.hasNext()) {
			String testName = it.next();
			HashMap<String, HashMap<Integer, Integer>> classes = originalLineCoverageMaps
					.get(testName);
			Iterator<String> it2 = classes.keySet().iterator();
			while (it2.hasNext()) {
				String name = it2.next();
				if (name.startsWith(mutation.className)) {
					HashMap<Integer, Integer> lines = classes.get(name);
					if (lines.containsKey(mutation.lineNumber)) {
						int pos = name.indexOf('@') + 1;
						mutation.methodName = name.substring(pos);
						return name;
					}
				}
			}
		}
		return "";
	}

	/*
	 * *************************************************************************
	 * General that gets a mutation out of the queue and runs two helper methods
	 * to process line coverage and data coverage sequentially.
	 */
	private void processMutation() {
		MutationCache mutation;
		while ((mutation = lbq.poll()) != null) {
			String ignoredMethod = findMutatedMethod(mutation);
			TracerResult results = new TracerResult();
			HashSet<String> modifiedMethods = new HashSet<String>();
			processMutationLineCoverage(mutation, results, modifiedMethods,
					ignoredMethod);
			processMutationDataCoverage(mutation, results, modifiedMethods,
					ignoredMethod);

			results.methodsModifiedAll = modifiedMethods.size();
			results.methodsModifiedAllWOS = modifiedMethods.size();

			if (modifiedMethods.contains(ignoredMethod)) {
				results.methodsModifiedAllWOS = modifiedMethods.size() - 1;
			}

			writeOut(mutation, results);
		}
	}

	/*
	 * *************************************************************************
	 * Helper method that processes a single mutation for line coverage data.
	 */
	private void processMutationLineCoverage(MutationCache mutation,
			TracerResult results, HashSet<String> modifiedMethodsSet,
			String ignoredMethod) {
		ObjectInputStream ois = null;

		String path = TracerProperties.TRACE_RESULT_LINE_DIR + mutation.id + "/";
		File dir = new File(path);
		if (!dir.exists()) {
			logger.warn("No line coverage data found for mutation: "
					+ mutation.shortString);
			return;
		}
		String[] mutatedTests = dir.list();

		HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();

		HashMap<String, HashMap<Integer, Boolean>> modifiedTmp = null;
		boolean commit = true;
		for (String test : mutatedTests) {

			// TRANSACTION START: copy modified to modifiedTmp
			modifiedTmp = modified;
			commit = true;
			try {
				ois = new ObjectInputStream(new BufferedInputStream(
						new GZIPInputStream(new FileInputStream(path + test))));
				if (originalLineCoverageMaps.containsKey(test)) {
					processTestLineCoverage(originalLineCoverageMaps.get(test),
							ois, modifiedTmp);
				} else {
					logger
							.warn("Got no coverage data of unmutated run for test: "
									+ test);
				}
			} catch (FileNotFoundException e) {
				// TRANSACTION FAIL
				commit = false;
				logger.info("File not found: " + (path + test));
				e.printStackTrace();
			} catch (IOException e) {
				// TRANSACTION FAIL
				commit = false;
				logger.info("IO Exception: " + (path + test));
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// TRANSACTION COMMIT
			if (commit) {
				modified = modifiedTmp;
			} else {
				logger.info("Line Coverage Test '" + test + "' for Mutation "
						+ mutation.id + " excluded.");
			}
		}

		// Count the number of modified lines/methods/classes;
		int linesTotal = 0, linesModified = 0;
		int methodsTotal = 0, methodsModified = 0;

		HashSet<String> classesTotalHash = new HashSet<String>();
		HashSet<String> classesModifiedHash = new HashSet<String>();

		Iterator<String> itModified = modified.keySet().iterator();
		boolean foundSelf = false;

		while (itModified.hasNext()) {
			String name = itModified.next();

			// don't analyze methods contained in the differencesSet
			if (differencesSet.contains(name)) {
				continue;
			}

			if (ignoredMethod.equals(name)) {
				foundSelf = true;
			}
			int index = name.indexOf('@');
			String className = null;
			if (index > 0) {
				className = name.substring(0, name.indexOf('@'));
			} else {
				throw new RuntimeException(
						"Expected that name contains an @ character. Name: "
								+ name);
			}
			HashMap<Integer, Boolean> lineSet = modified.get(name);

			Iterator<Integer> itLineSet = lineSet.keySet().iterator();

			boolean first = true;

			while (itLineSet.hasNext()) {
				Integer line = itLineSet.next();
				Boolean b = lineSet.get(line);
				// System.out.println(className + ": " + line + " modified:
				// " +b);
				if (b) {
					if (first) {
						first = false;
						methodsModified++;
						modifiedMethodsSet.add(name);
						classesModifiedHash.add(className);
						// System.out.println(className);
					}
					linesModified++;
					// System.out.println(line);
				}
				linesTotal++;
			}
			methodsTotal++;
			classesTotalHash.add(className);
		}

		results.testsExecuted = mutatedTests.length;
		results.testsTotal = originalLineCoverageMaps.size();
		results.linesTotal += linesTotal;
		results.linesModified += linesModified;
		results.methodsTotal += methodsTotal;

		results.methodsModifiedLine += methodsModified;

		if (foundSelf && methodsModified > 0) {
			results.methodsModifiedLineWOS += (methodsModified - 1);
		} else {
			results.methodsModifiedLineWOS += methodsModified;
		}

		results.classesTotal += classesTotalHash.size();
		results.classesModified += classesModifiedHash.size();
	}

	/*
	 * *************************************************************************
	 * Helper method that processes a single mutation for data coverage data.
	 */
	private void processMutationDataCoverage(MutationCache mutation,
			TracerResult results, HashSet<String> modifiedMethodsSet,
			String ignoredMethod) {
		ObjectInputStream ois = null;

		String path = TracerProperties.TRACE_RESULT_DATA_DIR + mutation.id + "/";
		File dir = new File(path);
		if (!dir.exists()) {
			System.out.println("NOT FOUND: " + mutation.shortString);
			return;
		}
		String[] mutatedTests = dir.list();

		HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();

		HashMap<String, HashMap<Integer, Boolean>> modifiedTmp = null;
		boolean commit = true;
		for (String test : mutatedTests) {

			// TRANSACTION START: copy modified to modifiedTmp
			modifiedTmp = modified;
			commit = true;
			try {
				ois = new ObjectInputStream(new BufferedInputStream(
						new GZIPInputStream(new FileInputStream(path + test))));
				if (originalDataCoverageMaps.containsKey(test)) {
					processTestDataCoverage(originalDataCoverageMaps.get(test),
							ois, modified);
				} else {
					logger
							.warn("Got no coverage data of unmutated run for test: "
									+ test);
				}
			} catch (FileNotFoundException e) {
				// TRANSACTION FAIL
				commit = false;
				logger.info("File not found: " + path + test);
				e.printStackTrace();
			} catch (IOException e) {
				// TRANSACTION FAIL
				commit = false;
				logger.info("IO Exception: " + path + test);
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// TRANSACTION COMMIT
			if (commit) {
				modified = modifiedTmp;
			} else {
				logger.info("Data Coverage Test '" + test + "' for Mutation "
						+ mutation.id + " excluded.");
			}
		}

		// Count the number of modified data
		int dataTotal = 0, dataModified = 0;

		HashSet<String> countModified = new HashSet<String>();

		Iterator<String> itModified = modified.keySet().iterator();

		boolean foundSelf = false;
		while (itModified.hasNext()) {
			String name = itModified.next();

			// don't analyze methods contained in the differencesSet
			if (differencesSet.contains(name)) {
				continue;
			}
			// don't analyze methods contained in the dontInstrumentSet
			if (dontInstrumentSet.contains(name)) {
				continue;
			}

			if (ignoredMethod.equals(name)) {
				foundSelf = true;
			}

			HashMap<Integer, Boolean> lineSet = modified.get(name);

			Iterator<Integer> itLineSet = lineSet.keySet().iterator();

			while (itLineSet.hasNext()) {
				Integer line = itLineSet.next();
				Boolean b = lineSet.get(line);
				if (b) {
					dataModified++;
					countModified.add(name);
					modifiedMethodsSet.add(name);
				}
				dataTotal++;
			}
		}

		results.methodsModifiedData += countModified.size();
		if (foundSelf && countModified.size() > 0) {
			results.methodsModifiedDataWOS += (countModified.size() - 1);
		} else {
			results.methodsModifiedDataWOS += countModified.size();
		}
		results.dataTotal += dataTotal;
		results.dataModified += dataModified;
	}

	/*
	 * *************************************************************************
	 * Helper method is called at the end of the whole process to give the user
	 * a short summary (written out to the console) of the processed data.
	 */
	private String getShortResults(int counter, double epsilon) {
		StringBuilder sb = new StringBuilder();
		sb.append("Mutations processed: " + counter + "\n");
		sb.append("\tEpsilon:        " + epsilon + "\n");
		sb.append("Results for line coverage traces:\n");
		writeShortResultPercentHelper(sb, epsilon, killedLine, notKilledLine);
		sb.append("Results for data coverage traces:\n");
		writeShortResultPercentHelper(sb, epsilon, killedData, notKilledData);
		sb.append("Results for combined coverage traces:\n");
		writeShortResultPercentHelper(sb, epsilon, killed, notKilled);
		return sb.toString();
	}

	private void writeShortResultPercentHelper(StringBuilder sb,
			double epsilon,
			List<Number> killed, List<Number> notKilled) {
		// killed
		int over = 0, under = 0;
		for (Number n : killed) {
			if (n.doubleValue() > epsilon) {
				over++;
			} else {
				under++;
			}
		}
		if (killed.size() > 0) {
			sb.append("\tKilled:         " + killed.size() + "\tover epsilon: "
					+ dec.format(over / (double) killed.size() * 100)
					+ "%\t under epsilon: "
					+ dec.format(under / (double) killed.size() * 100) + "%\n");
		}

		// not Killed
		over = 0;
		under = 0;
		for (Number n : notKilled) {
			if (n.doubleValue() > epsilon) {
				over++;
			} else {
				under++;
			}
		}
		if (notKilled.size() > 0) {
			sb.append("\tNOT Killed:     " + notKilled.size()
					+ "\tover epsilon: "
					+ dec.format(over / (double) notKilled.size() * 100)
					+ "%\t under epsilon: "
					+ dec.format(under / (double) notKilled.size() * 100)
					+ "%\n");
		}
	}

	/*
	 * *************************************************************************
	 * Main method that first reads in all mutations and writes it into a queue.
	 * After that the method starts several threads. Each thread then works on
	 * the same queue and so processes mutations.
	 */
	public String analyze(Iterable<Mutation> mutations) {
		loadOriginalTraces();

		int counter = 0;
		double epsilon = 0;

		try {
			out = new PrintStream("analyze.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Iterator<Mutation> mi = mutations.iterator();

		while (mi.hasNext()) {
			Mutation m = mi.next();
			MutationCache mc = MutationCache.create(m);
			if (!mc.mutationResult.equals(TracerProperties.NO_RESULT)) {
				counter++;
				if (counter % 500 == 0) {
					logger.info("Added " + counter + " mutations to queue");
				}
				lbq.offer(mc);
			}
		}

		Runnable r = new Runnable() {
			public void run() {
				processMutation();
			}
		};

		Thread[] innerThread = new Thread[8];
		for (int i = 0; i < innerThread.length; i++) {
			innerThread[i] = new Thread(r);
			innerThread[i].start();
		}
		int finish = 0;
		while (finish != innerThread.length) {
			if (lbq.size() > 1000) {
				// sleep for 5 seconds
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			finish = 0;
			for (int i = 0; i < innerThread.length; i++) {
				if (!innerThread[i].isAlive()) {
					finish++;
				}
			}
		}
		String shortResults = getShortResults(counter, epsilon);
		return shortResults;
	}
}
