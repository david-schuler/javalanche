package de.unisb.cs.st.javalanche.tracer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class CompareTraces extends NewTracerAnalyzer {
	HashMap<String, HashMap<String, HashMap<Integer, Integer>>> trace1 = null;
	HashMap<String, HashMap<String, HashMap<Integer, Integer>>> trace2 = null;
	
	Set<String> differences = (Set<String>) new HashSet<String>();
	
	private enum Mode { LINE, DATA };
	private long mutation_id1 = -1;
	private long mutation_id2 = 0;
	
	public CompareTraces(String mode, String id1, String id2) {
		if (new File(TracerConstants.TRACE_DIFFERENCES_FILE).exists()) {
			differences =  (Set<String>) XmlIo.get(TracerConstants.TRACE_DIFFERENCES_FILE);
		}
		
		mutation_id1 = Long.parseLong(id1);
		mutation_id2 = Long.parseLong(id2);		

		if (mode.equals("line") || mode.equals("both")) {
			compare(Mode.LINE);
		}
		if (mode.equals("data") || mode.equals("both")) {
			compare(Mode.DATA);
		}
		
		System.out.println(differences);
		XmlIo.toXML(differences, TracerConstants.TRACE_DIFFERENCES_FILE);
	}
	
	private void loadTraces(Mode mode) {
		if (mode == Mode.LINE) {
			trace1 = loadLineCoverageTrace(mutation_id1);
			trace2 = loadLineCoverageTrace(mutation_id2);
		} else {
			trace1 = loadDataCoverageTrace(mutation_id1);
			trace2 = loadDataCoverageTrace(mutation_id2);
			
		}
	}
	
	private void iterate(HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map1, HashMap<String, HashMap<String, HashMap<Integer, Integer>>> map2) {
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
				} else {
					foundDifference = false;
				}
				while(!foundDifference && valueMap1 != null && it3.hasNext()) {
					Integer valueKey = it3.next(); 
					if (valueMap1.get(valueKey) != valueMap2.get(valueKey)) {
						foundDifference = true;
					}	
				}
				if (foundDifference) {
					differences.add(className);
				}
			}
		}
	}
	
	private void compare(Mode mode) {
		loadTraces(mode);
		iterate(trace1, trace2);
		iterate(trace2, trace1);
	}
	
	public static void main(String[] args) {
		boolean exit = false;
		if (args.length < 1) {
			exit = true;
		}
		StringTokenizer st = new StringTokenizer(args[0]);
		if (st.countTokens() < 3) {
			exit = true;
		}
		
		if (exit) { 
			System.out.println("Error: 3 parameters needed!");
		}
		
		CompareTraces ct = new CompareTraces(st.nextToken(), st.nextToken(), st.nextToken());
		
	}

}
