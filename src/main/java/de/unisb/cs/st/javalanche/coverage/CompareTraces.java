package de.unisb.cs.st.javalanche.coverage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class CompareTraces extends CoverageAnalyzer {

	class FilenameFilterImpl implements FilenameFilter {
		public boolean accept(File arg0, String arg1) {
			if (arg1.startsWith("PERMUTATED_")) {
				return true;
			}
			return false;
		}
	}

	private static Logger logger = Logger.getLogger(CompareTraces.class);

	HashMap<String, HashMap<String, HashMap<Integer, Integer>>> trace1 = null;
	HashMap<String, HashMap<String, HashMap<Integer, Integer>>> trace2 = null;
	Set<String> differences = (Set<String>) new HashSet<String>();

	private enum Mode { LINE, DATA };
	private String mutation_dir1 = "0";
	private String mutation_dir2 = "0";

	public CompareTraces() {
		this("both");
	}

	public CompareTraces(String mode) {
		File dir = new File(TracerProperties.TRACE_RESULT_LINE_DIR);
		String[] files = dir.list(new FilenameFilterImpl());
		HashSet<String> diffComplete = new HashSet<String>();
		int count =0;
		for (String file : files) {
			calculateDifferences(mode, "0", file);
			count++;
			int preValue = diffComplete.size();
			diffComplete.addAll(differences);
			logger.info("Added " + (diffComplete.size() - preValue) + " methods. Total methods now: " + diffComplete.size());
			if(count %10 == 0){
				XmlIo.toXML(diffComplete, TracerProperties.TRACE_DIFFERENCES_FILE + count);
			}
			differences.clear();
		}
		System.out.println("Methods that have differences in at least on run:" + diffComplete);
		XmlIo.toXML(diffComplete, TracerProperties.TRACE_DIFFERENCES_FILE);
	}

	public CompareTraces(String mode, String id1, String id2) {
		calculateDifferences(mode, id1, id2);
	}

	private void calculateDifferences(String mode, String id1, String id2) {
		//if (new File(TracerConstants.TRACE_DIFFERENCES_FILE).exists()) {
		//	differences =  (Set<String>) XmlIo.get(TracerConstants.TRACE_DIFFERENCES_FILE);
		//}
		differences  = new HashSet<String>();
		mutation_dir1 = id1;
		mutation_dir2 = id2;

		System.out.println(id1 + " VS. " + id2 + ": ");
		if (mode.equals("line") || mode.equals("both")) {
			logger.info("Comparing lines");
			compare(Mode.LINE);
			logger.info("Differences " + differences.size());
		}
		if (mode.equals("data") || mode.equals("both")) {
			logger.info("Comparing data");
			compare(Mode.DATA);
			logger.info("Differences " + differences.size());
		}
		XmlIo.toXML(differences, TracerProperties.TRACE_DIFFERENCES_FILE);
		System.out.println(differences);
	}

	protected void loadTraces(Mode mode) {
		if (mode == Mode.LINE) {
			trace1 = loadLineCoverageTrace(mutation_dir1);
			trace2 = loadLineCoverageTrace(mutation_dir2);
		} else {
			trace1 = loadDataCoverageTrace(mutation_dir1);
			trace2 = loadDataCoverageTrace(mutation_dir2);

		}
	}

	protected void iterate(HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map1, HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map2) {
		Iterator<String> it1 = map1.keySet().iterator();

		boolean foundDifference = false;
		while (map1 != null && it1.hasNext()) {
			String testName = it1.next();
			HashMap<String, HashMap<Integer, Integer>> testMap1 = map1.get(testName);
			HashMap<String, HashMap<Integer, Integer>> testMap2 = map2.get(testName);
			Iterator<String> it2 = testMap1.keySet().iterator();
			while (testMap1 != null && testMap2 != null && it2.hasNext()) {
				String className = it2.next();
				HashMap<Integer, Integer> valueMap1 = testMap1.get(className);
				HashMap<Integer, Integer> valueMap2 = testMap2.get(className);
				Iterator<Integer> it3 = valueMap1.keySet().iterator();
				if (valueMap2 == null && valueMap1 != null) {
					foundDifference = true;
					logger.info("Map2 is null for test: " + testName + "  - "  + className);
				} else {
					foundDifference = false;
				}
				while(!foundDifference && valueMap1 != null && it3.hasNext()) {
					Integer valueKey = it3.next();
					if (!valueMap1.get(valueKey).equals(valueMap2.get(valueKey))) {
						foundDifference = true;
						logger.info("Difference for test "  + testName + "  "  + className + " key " +  valueKey + " Value1: " + valueMap1.get(valueKey) +  " Value2:  " + valueMap2.get(valueKey)   );
					}
				}
				if (foundDifference) {
					differences.add(className);
				}
			}
		}
	}

	protected void compare(Mode mode) {
		loadTraces(mode);
		logger.info("Run 1");
		iterate(trace1, trace2);
		logger.info("Run 2");
		iterate(trace2, trace1);
	}

	public static void main(String[] args) {
		boolean exit = false;
		if (args.length < 1) {
			exit = true;
		}
		if (exit) {
			System.out.println("Error - read help");
		}

		StringTokenizer st = new StringTokenizer(args[0]);
		CompareTraces ct = null;

		if (!args[0].contains("cmpid") && st.countTokens() >= 3) {
			ct = new CompareTraces(st.nextToken(), st.nextToken(), st.nextToken());
		} else if (!args[0].contains("cmpmode") && st.countTokens() >= 1) {
			ct = new CompareTraces(st.nextToken());
		} else {
			ct = new CompareTraces();
		}

	}

}
