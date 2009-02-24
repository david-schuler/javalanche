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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/* TODO rewrite of processTest* and processMutation*
 * Maybe it is possible to merge both methods somehow.
 * Another trick would be to read the whole data in one step and analyze in
 * a second step.
 */

public class NewTracerAnalyzer implements MutationAnalyzer {

	private static class MutationCache {
		public long id;
		public boolean killed;
		public boolean classInit;
		public String shortString;
		public String className;
		public int lineNumber;
		public int mutationForLine;
		public String mutationType;
		public String mutationResult;

		public static MutationCache create(Mutation m) {
			MutationCache mc = new MutationCache();
			mc.id = m.getId();
//			mc.shortString = m.toShortString();
			mc.killed = m.isKilled();
			mc.classInit = m.isClassInit();
			mc.className = m.getClassName();
			mc.lineNumber = m.getLineNumber();
			mc.mutationForLine = m.getMutationForLine();
			mc.mutationType = m.getMutationType().toString();
			mc.mutationResult = m.getMutationResult() != null ? m
					.getMutationResult().toShortString()
					: TracerConstants.NO_RESULT;
			return mc;
		}
	}

	private static class TracerResult {
		// an artificial number
		public double result = 0;

		// The summarized number of classes called by the run/mutation
		public double classesTotal = 0;
		public double classesModified = 0;

		// The summarized number of classes called by the run/mutation;
		public double methodsTotal = 0;
		public double methodsModifiedLine = 0;
		public double methodsModifiedAll = 0;

		// The summarized number of lines called by the run/mutation
		public double linesTotal = 0;
		public double linesModified = 0;

		// The number of tests
		public double testsTotal = 0;
		public double testsExecuted = 0;

		// THe number of data
		public double dataTotal = 0;
		public double dataModified = 0;
	}

	private static final Logger logger = Logger
			.getLogger(NewTracerAnalyzer.class);

	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalLineCoverageMaps = null;
	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalDataCoverageMaps = null;

	boolean firstCallToWriteOut = true;
	PrintStream out = null;
	StringBuffer sb = new StringBuffer();

	ArrayList<Number> killed = new ArrayList<Number>();
	ArrayList<Number> notKilled = new ArrayList<Number>();

	DecimalFormat dec = new DecimalFormat("###.##");
	DecimalFormat dec2 = new DecimalFormat("#.########");

	LinkedBlockingQueue<MutationCache> lbq = new LinkedBlockingQueue<MutationCache>();

	/*
	private HashMap<Integer, String> loadIdMap(long mutation_id) {
		HashMap<Integer, String> idMap = new HashMap<Integer, String>();
		if (mutation_id != 0) {
			File tmp = new File(TracerConstants.TRACE_RESULT_DIR + mutation_id
					+ "-" + TracerConstants.TRACE_CLASS_IDFILE);
			if (!tmp.exists()) {
				mutation_id = 0;
			}
		}
		if (mutation_id == 0) {
			File tmp = new File(TracerConstants.TRACE_CLASS_MASTERIDS);
			if (!tmp.exists()) {
				return null;
			}
		}

		ObjectInputStream ois = null;
		try {
			if (mutation_id == 0) {
				ois = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(
								TracerConstants.TRACE_CLASS_MASTERIDS)));
			} else {
				ois = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(TracerConstants.TRACE_RESULT_DIR
								+ mutation_id + "-"
								+ TracerConstants.TRACE_CLASS_IDFILE)));
			}
			int numIds = ois.readInt();
			idMap = new HashMap<Integer, String>();
			for (int i = 0; i < numIds; i++) {
				String className = ois.readUTF();
				int id = ois.readInt();
				idMap.put(id, className);
			}
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idMap;
	}
	*/

	
	/* *************************************************************************
	 * Helper method to load the original (id=0) line and data coverage traces.
	 */
	private void loadOriginalTraces() {
		originalLineCoverageMaps = loadLineCoverageTrace(0);
		originalDataCoverageMaps = loadDataCoverageTrace(0);
	}

	/* *************************************************************************
	 * Helper method to load an arbitrary trace (line or data coverage)
	 */
	private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadTrace(
			String path, long mutation_id) {
		ObjectInputStream ois = null;
		path += mutation_id + "/";

		File dir = new File(path);
		String[] originalTests = dir.list();

		int numClasses, numLines;
		String className;

		HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
		HashMap<String, HashMap<Integer, Integer>> classMap;
		// HashMap<Integer, String> idMap = loadIdMap(0);
		HashMap<Integer, Integer> lineMap;

		for (String test : originalTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(
						new FileInputStream(path + test))));
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

	/* *************************************************************************
	 * Helper method to load an arbitrary line coverage trace. 
	 */
	private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadLineCoverageTrace(
			long mutation_id) {
		return loadTrace(TracerConstants.TRACE_RESULT_LINE_DIR, mutation_id);
	}

	/* *************************************************************************
	 * Helper method to load an arbitrary data coverage trace. 
	 */
	private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> loadDataCoverageTrace(
			long mutation_id) {
		return loadTrace(TracerConstants.TRACE_RESULT_DATA_DIR, mutation_id);
	}

	/* *************************************************************************
	 * Method to write out results as csv data for a given mutation and result.
	 * This method is synchronized as we have to ensure that each thread can 
	 * write its own data (a whole line of csv data) to the file. 
	 */
	private synchronized void writeOut(MutationCache mutation,
			TracerResult results) {
		if (mutation.killed) {
			killed.add(results.methodsModifiedAll);
		} else {
			notKilled.add(results.methodsModifiedAll);
		}

		if (firstCallToWriteOut) {
			out.println("ID;KILLED;RESULT;" + "CLASSES_TOTAL;CLASSES_MODIFIED;"
					+ "METHODS_TOTAL;METHODS_MODIFIED_LINE;METHODS_MODIFIED_ALL;"
					+ "LINES_TOTAL;LINES_MODIFIED;"
					+ "DATA_TOTAL;DATA_MODIFIED;"
					+ "TESTS_TOTAL;TESTS_EXECUTED;"
					+ "MUTATION_TYPE;CLASS_NAME;LINE_NUMBER;MUTATION_FOR_LINE;"
					+ "CLASS_INIT;MUTATION_RESULT");
			firstCallToWriteOut = false;
		}

		logger.info("ID: " + mutation.id + "\tKilled: " + mutation.killed
				+ "\tValue: "
				+ (results.methodsModifiedAll) + " ("
				+ mutation.mutationType + ")");
		out.println(mutation.id + ";" + mutation.killed + ";"
				+ dec2.format(results.result) + ";"
				+ dec2.format(results.classesTotal) + ";"
				+ dec2.format(results.classesModified) + ";"
				+ dec2.format(results.methodsTotal) + ";"
				+ dec2.format(results.methodsModifiedLine) + ";"
				+ dec2.format(results.methodsModifiedAll) + ";"
				+ dec2.format(results.linesTotal) + ";"
				+ dec2.format(results.linesModified) + ";"
				+ dec2.format(results.dataTotal) + ";"
				+ dec2.format(results.dataModified) + ";"
				+ dec2.format(results.testsTotal) + ";"
				+ dec2.format(results.testsExecuted) + ";"
				+ mutation.mutationType + ";" + mutation.className + ";"
				+ mutation.lineNumber + ";" + mutation.mutationForLine + ";"
				+ mutation.classInit + ";" + mutation.mutationResult);

	}

	/* *************************************************************************
	 * Method that processes a single line coverage test and returns its results
	 * to the calling mutation test method.  
	 */
	private void processTestLineCoverage(
			HashMap<String, HashMap<Integer, Integer>> classMap,
			HashMap<Integer, String> idMap, ObjectInputStream ois,
			TracerResult results,
			HashMap<String, HashMap<Integer, Boolean>> modified)
			throws IOException {
		int numClasses, numLines;
		String className;

		HashMap<Integer, Integer> lineMap;

		int lineKey, lineValue;

		double result = 0;
		double maxresult = 0;

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
			// className = idMap.get(ois.readInt());
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

				result += value;
				maxresult += Math.max(originalValue, lineValue);
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

					result += lineMap.get(tmp);
					maxresult += lineMap.get(tmp);
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
				result += lineMap.get(tmp);
				maxresult += lineMap.get(tmp);
			}
			modified.put(tmpS, lineSet);
		}

		results.result += (result / maxresult);
	}
	
	
	/* *************************************************************************
	 * Method that processes a single data coverage test and returns its results
	 * to the calling mutation test method.  
	 */
	private void processTestDataCoverage(
			HashMap<String, HashMap<Integer, Integer>> classMap,
			HashMap<Integer, String> idMap, ObjectInputStream ois,
			TracerResult results,
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

	
	/* *************************************************************************
	 * General that gets a mutation out of the queue and runs two helper methods
	 * to process line coverage and data coverage sequentially. 
	 */
	private void processMutation() {
		MutationCache mutation;
		while ((mutation = lbq.poll()) != null) {
			TracerResult results = new TracerResult();
			HashSet<String> modifiedMethods = new HashSet<String>();
			processMutationLineCoverage(mutation, results, modifiedMethods);
			processMutationDataCoverage(mutation, results, modifiedMethods);
			
			results.methodsModifiedAll = modifiedMethods.size();
			writeOut(mutation, results);
		}
	}

	/* *************************************************************************
	 * Helper method that processes a single mutation for line coverage data.   
	 */
	private void processMutationLineCoverage(MutationCache mutation,
			TracerResult results, HashSet<String> modifiedMethodsSet) {
		ObjectInputStream ois = null;

		String path = TracerConstants.TRACE_RESULT_LINE_DIR + mutation.id + "/";
		File dir = new File(path);
		if (!dir.exists()) {
			System.out.println("NOT FOUND: " + mutation.shortString);
			return;
		}
		String[] mutatedTests = dir.list();

		HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();
		//HashMap<Integer, String> idMap = loadIdMap(mutation.id);
		for (String test : mutatedTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(
						new FileInputStream(path + test))));
				if (originalLineCoverageMaps.containsKey(test)) {
					processTestLineCoverage(originalLineCoverageMaps.get(test),
							null, ois, results, modified);
				} else {
					logger
							.warn("Got no coverage data of unmutated run for test: "
									+ test);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		}

		// Count the number of modified lines/methods/classes;
		int linesTotal = 0, linesModified = 0;
		int methodsTotal = 0, methodsModified = 0;

		HashSet<String> classesTotalHash = new HashSet<String>();
		HashSet<String> classesModifiedHash = new HashSet<String>();

		Iterator<String> itModified = modified.keySet().iterator();
		while (itModified.hasNext()) {
			String name = itModified.next();
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
			System.out.println(methodsTotal + ": " + name);
			classesTotalHash.add(className);
		}

		results.testsExecuted = (double) mutatedTests.length;
		results.testsTotal = (double) originalLineCoverageMaps.size();
		results.result = (results.result / (double) mutatedTests.length);
		results.linesTotal += (double) linesTotal;
		results.linesModified += (double) linesModified;
		results.methodsTotal += (double) methodsTotal;
		results.methodsModifiedLine += (double) methodsModified;
		results.classesTotal += (double) classesTotalHash.size();
		results.classesModified += (double) classesModifiedHash.size();
	}

	/* *************************************************************************
	 * Helper method that processes a single mutation for data coverage data.   
	 */
	private void processMutationDataCoverage(MutationCache mutation,
			TracerResult results, HashSet<String> modifiedMethods) {
		ObjectInputStream ois = null;

		String path = TracerConstants.TRACE_RESULT_DATA_DIR + mutation.id + "/";
		File dir = new File(path);
		if (!dir.exists()) {
			System.out.println("NOT FOUND: " + mutation.shortString);
			return;
		}
		String[] mutatedTests = dir.list();

		HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();
		//HashMap<Integer, String> idMap = loadIdMap(mutation.id);
		for (String test : mutatedTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(
						new FileInputStream(path + test))));
				if (originalDataCoverageMaps.containsKey(test)) {
					processTestDataCoverage(originalDataCoverageMaps.get(test),
							null, ois, results, modified);
				} else {
					logger
							.warn("Got no coverage data of unmutated run for test: "
									+ test);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		}

		// Count the number of modified data
		int dataTotal = 0, dataModified = 0;

		Iterator<String> itModified = modified.keySet().iterator();
		while (itModified.hasNext()) {
			String name = itModified.next();
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

			while (itLineSet.hasNext()) {
				Integer line = itLineSet.next();
				Boolean b = lineSet.get(line);
				if (b) {
					dataModified++;
					modifiedMethods.add(name);
				}
				dataTotal++;
			}
		}

		results.dataTotal += (double) dataTotal;
		results.dataModified += (double) dataModified;
	}

	/* *************************************************************************
	 * Helper method is called at the end of the whole process to give the user 
	 * a short summary (written out to the console) of the processed data.    
	 */
	public void writeShortResults(int counter, double epsilon) {
		sb.append("Mutations processed: " + counter + "\n");
		sb.append("\tEpsilon:        " + epsilon + "\n");

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

	/* *************************************************************************
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
			if (!mc.mutationResult.equals(TracerConstants.NO_RESULT)) {
				counter++;
				lbq.offer(mc);
			}
		}

		Runnable r = new Runnable() {
			public void run() {
				processMutation();
			}
		};

		Thread[] innerThread = new Thread[1];
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

		writeShortResults(counter, epsilon);

		return sb.toString();
	}
}
