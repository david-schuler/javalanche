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

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

class MutationCache {
	private static final String NO_RESULT = "No Result";
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
		mc.shortString = m.toShortString();
		mc.killed = m.isKilled();
		mc.classInit = m.isClassInit();
		mc.className = m.getClassName();
		mc.lineNumber = m.getLineNumber();
		mc.mutationForLine = m.getMutationForLine();
		mc.mutationType = m.getMutationType().toString();
		mc.mutationResult = m.getMutationResult() != null ? m
				.getMutationResult().toShortString() : NO_RESULT;
		return mc;
	}
}

class TracerResult {
	// an artificial number
	public double result = 0;

	// The summarized number of classes called by the run/mutation
	public double classesTotal = 0;
	public double classesModified = 0;

	// The summarized number of classes called by the run/mutation;
	public double methodsTotal = 0;
	public double methodsModified = 0;

	// The summarized number of lines called by the run/mutation
	public double linesTotal = 0;
	public double linesModified = 0;

	// The number of tests
	public double testsTotal = 0;
	public double testsExecuted = 0;
}

public class NewTracerAnalyzer implements MutationAnalyzer {

	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalMaps = null;

	PrintStream out = null;
	StringBuffer sb = new StringBuffer();

	ArrayList<Number> killed = new ArrayList<Number>();
	ArrayList<Number> notKilled = new ArrayList<Number>();

	DecimalFormat dec = new DecimalFormat("###.##");
	DecimalFormat dec2 = new DecimalFormat("#.########");

	LinkedBlockingQueue<MutationCache> lbq = new LinkedBlockingQueue<MutationCache>();

	private void loadOriginalTraces() {
		ObjectInputStream ois = null;

		String path = TracerTestListener.TRACE_RESULT_DIR + "0/";
		File dir = new File(path);
		String[] originalTests = dir.list();

		int numClasses, numLines;
		String className;

		originalMaps = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
		HashMap<String, HashMap<Integer, Integer>> classMap; // = new
		// HashMap<String,
		// HashMap<Integer,
		// Integer>>();
		HashMap<Integer, Integer> lineMap; // = new HashMap<Integer,
		// Integer>();

		for (String test : originalTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(path + test)));
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
				originalMaps.put(test, classMap);
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void writeOut(MutationCache mutation,
			TracerResult results) {
		if (mutation.killed) {
			killed.add(results.result);
		} else {
			notKilled.add(results.result);
		}

		System.out.println("ID: " + mutation.id + "\tKilled: "
				+ mutation.killed + "\tValue: " + results.result + " ("
				+ mutation.mutationType + ")");
		out.println(mutation.id
				+ ";"
				+ mutation.killed
				+ ";"
				+ dec2.format(results.result)
				+ ";"
				+
				// dec2.format(maxExecutions) + ";" +
				// dec2.format(differenceExecutions) + ";" +
				dec2.format(results.classesTotal) + ";"
				+ dec2.format(results.classesModified) + ";"
				+ dec2.format(results.linesTotal) + ";"
				+ dec2.format(results.linesModified) + ";"
				+ dec2.format(results.testsTotal) + ";"
				+ dec2.format(results.testsExecuted) + ";"
				+ mutation.mutationType + ";" + mutation.className + ";"
				+ mutation.lineNumber + ";" + mutation.mutationForLine + ";"
				+ mutation.classInit + ";" + mutation.mutationResult);

	}

	private void processTest(
			HashMap<String, HashMap<Integer, Integer>> classMap,
			ObjectInputStream ois, TracerResult results,
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

	private void processMutation() {
		MutationCache mutation;
		while ((mutation = lbq.poll()) != null) {
			long mutation_id = mutation.id;
			ObjectInputStream ois;

			String path = TracerTestListener.TRACE_RESULT_DIR + mutation_id
					+ "/";
			File dir = new File(path);
			if (!dir.exists()) {
				System.out.println("NOT FOUND: " + mutation.shortString);
				continue;
			}
			String[] mutatedTests = dir.list();
			HashMap<String, Double> map = new HashMap<String, Double>();

			TracerResult results = new TracerResult();

			HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();

			for (String test : mutatedTests) {
				try {
					ois = new ObjectInputStream(new BufferedInputStream(
							new FileInputStream(path + test)));
					processTest(originalMaps.get(test), ois, results, modified);
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			results.testsExecuted = (double) mutatedTests.length;
			results.testsTotal = (double) originalMaps.size();
			results.result = results.result / (double) mutatedTests.length;

			int linesTotal = 0, linesModified = 0, classesTotal = 0, classesModified = 0;

			Iterator<String> itModified = modified.keySet().iterator();
			while (itModified.hasNext()) {
				classesTotal++;
				String className = itModified.next();
				HashMap<Integer, Boolean> lineSet = modified.get(className);

				Iterator<Integer> itLineSet = lineSet.keySet().iterator();

				boolean first = true;
				while (itLineSet.hasNext()) {
					Integer line = itLineSet.next();
					linesTotal++;
					Boolean b = lineSet.get(line);
					// System.out.println(className + ": " + line + " modified:
					// " +b);
					if (b) {
						if (first) {
							classesModified++;
							first = false;
							// System.out.println(className);
						}
						linesModified++;
						// System.out.println(line);
					}
				}
			}

			results.linesTotal += (double) linesTotal;
			results.linesModified += (double) linesModified;
			results.classesTotal += (double) classesTotal;
			results.classesModified += (double) classesModified;

			writeOut(mutation, results);
		}
	}

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
			counter++;
			Mutation m = mi.next();
			lbq.offer(MutationCache.create(m));
		}

		Runnable r = new Runnable() {
			public void run() {
				processMutation();
			}
		};

		Thread[] innerThread = new Thread[11];
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