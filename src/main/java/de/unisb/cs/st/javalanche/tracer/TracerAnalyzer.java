package de.unisb.cs.st.javalanche.tracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;


public class TracerAnalyzer implements MutationAnalyzer {

//	class SerializedFilesFilenameFilter implements FilenameFilter {
//
//		public boolean accept(File file, String name) {
//			return name.contains(".ser");
//		}
//	}



	private static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> originalMaps = null;
	private HashMap<String, HashMap<String, HashMap<Integer, Integer>>> mutatedMaps = null;
	private Set<String> mutatedTestKeys;

	private Set<String> doneTests = Collections.synchronizedSet(new HashSet<String>());

	ArrayList<Number> killed = new ArrayList<Number>();
	ArrayList<Number> notKilled = new ArrayList<Number>();

	private Double mutationResult = new Double(0);

	PrintStream out = null;

	@SuppressWarnings("unchecked")
	private void loadOriginalTraces() {
		String file = TracerTestListener.TRACE_RESULT_DIR +"0.ser";
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			originalMaps = (HashMap<String, HashMap<String, HashMap<Integer, Integer>>>)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

	@SuppressWarnings("unchecked")
	private boolean loadMutatedTraces(Long id) {
		File file = new File(TracerTestListener.TRACE_RESULT_DIR+id+".ser");
		if (file.exists()) {
			ObjectInputStream in = null;
			mutatedMaps = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
			try {
				in = new ObjectInputStream(new FileInputStream(file.toString()));
				mutatedMaps = (HashMap<String, HashMap<String, HashMap<Integer, Integer>>>)in.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			};
			return true;
		}
		return false;
	}


	private double processMutation() {

		HashMap<String, HashMap<Integer, Integer>> originalClassMap = null;
		HashMap<String, HashMap<Integer, Integer>> mutatedClassMap = null;

		double result = 0;
		double counter = 0;

		for (String key : mutatedTestKeys) {
			if (!doneTests.add(key)) {
				continue;
			}
			//counter++;
			originalClassMap = originalMaps.get(key);
			mutatedClassMap = mutatedMaps.get(key);
			result += processTest(originalClassMap, mutatedClassMap);

		}
		//return result / counter;
		return result;
	}

	private double processTest(HashMap<String, HashMap<Integer, Integer>> originalClassMap, HashMap<String, HashMap<Integer, Integer>> mutatedClassMap) {
		Set<String> originalClassKeys = originalClassMap.keySet();
		Set<String> mutatedClassKeys = mutatedClassMap.keySet();
		Set<String> done = new HashSet<String>();

		HashMap<Integer, Integer> originalLineMap = null;
		HashMap<Integer, Integer> mutatedLineMap = null;

		if (originalClassMap.hashCode() == mutatedClassMap.hashCode()) {
			return 0;
		}

		double result = 0;
		double counter = 0;

		for (String key : originalClassKeys) {
			counter++;
			done.add(key);
			originalLineMap = originalClassMap.get(key);
			mutatedLineMap = mutatedClassMap.get(key);

			result += processLines(originalLineMap, mutatedLineMap);
		}

		for (String key : mutatedClassKeys) {
			if (done.contains(key)) {
				continue;
			}
			counter++;
			done.add(key);
			originalLineMap = originalClassMap.get(key);
			mutatedLineMap = mutatedClassMap.get(key);
			result += processLines(originalLineMap, mutatedLineMap);
		}

		return result / counter;

	}

	/*
	 * different lines / max(originalLineMap executed total, mutatedLineMap executed total)
	 *
	 */
	private double processLines(HashMap<Integer, Integer> originalLineMap, HashMap<Integer, Integer> mutatedLineMap) {

		Set<Integer> originalLineKeys = null;
		Set<Integer> mutatedLineKeys = null;

		if (mutatedLineMap == null && originalLineMap == null) {
			return 0;
		}

		 if (mutatedLineMap != null && originalLineMap != null && originalLineMap.hashCode() == mutatedLineMap.hashCode()) {
			return 0;
		 }

		if (originalLineMap == null) {
			originalLineMap = mutatedLineMap;
		}
		originalLineKeys = originalLineMap.keySet();

		if (mutatedLineMap != null) {
			mutatedLineKeys = mutatedLineMap.keySet();
		} else {
			mutatedLineKeys = originalLineKeys;
			mutatedLineMap = originalLineMap;
		}

		originalLineKeys = originalLineMap.keySet();

		Set<Integer> done = new HashSet<Integer>();

		double executionsTotal = 0;
		double executionsDifferent = 0;

		Integer v1 = null, v2 = null;

		for (Integer key : originalLineKeys) {
			done.add(key);
			v1 = originalLineMap.get(key);
			if (v1 == null) {
				v1 = 0;
			}
			v2 = mutatedLineMap.get(key);
			if (v2 == null) {
				v2 = 0;
			}

			if (v2 > v1) {
				executionsTotal += v2;
				executionsDifferent += v2 - v1;
			} else {
				executionsTotal += v1;
				executionsDifferent += v1 - v2;
			}
		}

		for (Integer key : mutatedLineKeys) {
			if (done.contains(key)) {
				continue;
			}
			done.add(key);
			executionsTotal += v2;
			executionsDifferent += v2;
		}
		if (executionsTotal > 0) {
			return executionsDifferent / executionsTotal;
		} else {
			return 0;
		}
	}

	public String analyze(Iterable<Mutation> mutations) {
		loadOriginalTraces();

		try {
			out = new PrintStream("analyze.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		boolean found = false;

		double epsilon = 0;
		double counter = 0;

		DecimalFormat dec = new DecimalFormat("###.##");
		DecimalFormat dec2 = new DecimalFormat("#.########");
		StringBuffer sb = new StringBuffer();

		Runnable r = new Runnable() {
			Double result = 0.0;
			public void run() {
				result = processMutation();
				synchronized(mutationResult) {
					if (!result.isNaN()) {
						mutationResult += result;
					}
				}
			}
		};

		Thread innerThread1, innerThread2, innerThread3, innerThread4;

		for (Mutation mutation : mutations) {
			found = loadMutatedTraces(mutation.getId());

			if (found) {
				counter++;
				doneTests.clear();
				mutatedTestKeys = mutatedMaps.keySet();
				mutationResult = 0.0;
				innerThread1 = new Thread(r);
				innerThread2 = new Thread(r);
				innerThread3 = new Thread(r);
				innerThread4 = new Thread(r);
				innerThread1.start();
				innerThread2.start();
				innerThread3.start();
				innerThread4.start();
				while (innerThread1.isAlive() || innerThread2.isAlive() || innerThread3.isAlive() || innerThread4.isAlive()) {
					// wait until all tests are done
				}


				// better?? mutationResult = mutationResult / originalMaps.size() * mutatedTestKeys.size();
				mutationResult /= mutatedTestKeys.size();

				System.out.println("ID: " + mutation.getId() + "\tKilled: " + mutation.isKilled() + "\tValue: " + mutationResult + " (" + mutation.getMutationType() + ")");
				out.println(mutation.getId() + ";" + mutation.isKilled() + ";" + dec2.format(mutationResult) + ";" + mutation.getMutationType() + ";" + mutation.getClassName() + ";" + mutation.getLineNumber() + ";" + mutation.getMutationForLine()+ ";" + mutation.isClassInit() +";" + mutation.getMutationResult().toShortString());

				if (mutation.isKilled()) {
					killed.add(mutationResult);
				} else {
					notKilled.add(mutationResult);
				}
			}
		}
		out.close();


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

}
