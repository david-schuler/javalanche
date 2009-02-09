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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class NewTracerAnalyzer implements MutationAnalyzer {



	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalMaps = null;

	PrintStream out = null;
	StringBuffer sb = new StringBuffer();

	ArrayList<Number> killed = new ArrayList<Number>();
	ArrayList<Number> notKilled = new ArrayList<Number>();

	DecimalFormat dec = new DecimalFormat("###.##");
	DecimalFormat dec2 = new DecimalFormat("#.########");

	LinkedBlockingQueue<Mutation> lbq = new LinkedBlockingQueue<Mutation>();

	private void loadOriginalTraces() {
		ObjectInputStream ois = null;


		String path = TracerTestListener.TRACE_RESULT_DIR+"0/";
		File dir = new File(path);
		String[] originalTests = dir.list();

		int numClasses, numLines;
		String className;

		originalMaps = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
		HashMap<String, HashMap<Integer, Integer>> classMap; // = new HashMap<String, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> lineMap; // = new HashMap<Integer, Integer>();

		for (String test : originalTests) {
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path + test)));
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

	private synchronized void writeOut(Mutation mutation, HashMap<String, Double> map) {
		double result = map.get("result");
		//double differenceExecutions = map.get("differenceExecutions");
		//double maxExecutions = map.get("maxExecutions");
		double numClassesTotal = map.get("classesTotal");
		double numClassesModified = map.get("classesModified");
		double numLinesTotal = map.get("linesTotal");
		double numLinesModified = map.get("linesModified");
		double testsExecuted = map.get("testsExecuted");
		double testsTotal = map.get("testsTotal");
		if (mutation.isKilled()) {
			killed.add(result);
		} else {
			notKilled.add(result);
		}

		System.out.println(	"ID: " + mutation.getId() +
							"\tKilled: " + mutation.isKilled() +
							"\tValue: " + result +
							" (" + mutation.getMutationType() + ")");
		out.println(	mutation.getId() + ";" +
						mutation.isKilled() + ";" +
						dec2.format(result) + ";" +
						//dec2.format(maxExecutions) + ";" +
						//dec2.format(differenceExecutions) + ";" +
						dec2.format(numClassesTotal) + ";" +
						dec2.format(numClassesModified) + ";" +
						dec2.format(numLinesTotal) + ";" +
						dec2.format(numLinesModified) + ";" +
						dec2.format(testsTotal) + ";" +
						dec2.format(testsExecuted) + ";" +
						mutation.getMutationType() + ";" +
						mutation.getClassName() + ";" +
						mutation.getLineNumber() + ";" +
						mutation.getMutationForLine()+ ";" +
						mutation.isClassInit() +";" +
						mutation.getMutationResult().toShortString());

	}

	private void processTest(Mutation mutation, HashMap<String, HashMap<Integer, Integer>> classMap, ObjectInputStream ois, HashMap<String, Double> map, HashMap<String, HashMap<Integer, Boolean>> modified) throws IOException {
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
				} else 	if (lineSet.get(lineKey) == null || !lineSet.get(lineKey)) {
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



		//process Classes only found in the original map
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
			while(itI.hasNext()) {
				tmp = itI.next();
				lineSet.put(tmp, true);
				result += lineMap.get(tmp);
				maxresult += lineMap.get(tmp);
			}
			modified.put(tmpS, lineSet);
		}

		addToMap(map, "result", (result / maxresult));
		addToMap(map, "differenceExecutions", result);
		addToMap(map, "maxExecutions", maxresult);
	}

	private void addToMap(HashMap<String, Double> map, String key, Double value) {
		if (map.get(key) == null) {
			map.put(key, value);
		} else {
			map.put(key, map.get(key) + value);
		}
	}

	private void processMutation() {
		Mutation mutation;
		while ((mutation = lbq.poll()) != null) {
			long mutation_id = mutation.getId();
			ObjectInputStream ois;

			String path = TracerTestListener.TRACE_RESULT_DIR + mutation_id + "/";
			File dir = new File(path);
			if (!dir.exists()) {
				System.out.println("NOT FOUND: " + mutation.toShortString());
				continue;
			}
			String[] mutatedTests = dir.list();
			HashMap<String, Double> map = new HashMap<String, Double>();
			HashMap<String, HashMap<Integer, Boolean>> modified = new HashMap<String, HashMap<Integer, Boolean>>();

			for (String test : mutatedTests) {
				try {
					ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path + test)));
					processTest(mutation, originalMaps.get(test), ois, map, modified);
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			map.put("testsExecuted", (double) mutatedTests.length);
			map.put("testsTotal", (double) originalMaps.size());

			map.put("result", (map.get("result") / (double) mutatedTests.length));


			int linesTotal=0, linesModified=0, classesTotal=0, classesModified=0;

			Iterator<String> itModified = modified.keySet().iterator();
			while(itModified.hasNext()) {
				classesTotal++;
				String className = itModified.next();
				HashMap<Integer, Boolean> lineSet = modified.get(className);

				Iterator<Integer> itLineSet = lineSet.keySet().iterator();

				boolean first = true;
				while(itLineSet.hasNext()) {
					Integer line = itLineSet.next();
					linesTotal++;
					Boolean b = lineSet.get(line);
					//System.out.println(className + ": " + line + " modified: " +b);
					if (b) {
						if (first) {
							classesModified++;
							first=false;
							System.out.println(className);
						}
						linesModified++;
						System.out.println(line);
					}
				}
			}
			addToMap(map, "linesTotal", (double)linesTotal);
			addToMap(map, "linesModified", (double)linesModified);
			addToMap(map, "classesTotal", (double)classesTotal);
			addToMap(map, "classesModified", (double)classesModified);
			writeOut(mutation, map);
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

		int[] x = {	183, 2500, 7677, 9435, 2405,
					9277, 8345, 8022, 8076, 1691,
					184, 7777, 7773, 402, 9813,
					9447, 8149, 7932, 7226, 7041 };

		while (mi.hasNext()) {
			counter ++;
			Mutation m = mi.next();
			for (int i = 0; i < x.length; i++) {
				if (m.getId() == x[i]) {
					lbq.offer(m);
				}
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
			sb.append("\tKilled:         " + killed.size() +  "\tover epsilon: " + dec.format(over / (double)killed.size() * 100) + "%\t under epsilon: " + dec.format(under / (double)killed.size() * 100) + "%\n");
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
		if (notKilled.size() >0) {
			sb.append("\tNOT Killed:     " + notKilled.size() + "\tover epsilon: " + dec.format(over / (double)notKilled.size() * 100) + "%\t under epsilon: " + dec.format(under / (double)notKilled.size() * 100) + "%\n");
		}
		return sb.toString();
	}

	private void testf() {
		ArrayList al1 = new ArrayList();
		ArrayList al2 = new ArrayList();
		if (al1 instanceof Collection || al2 instanceof Collection || (ArrayList<String>)null instanceof Collection) {
		}
	}
}