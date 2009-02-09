package de.unisb.cs.st.javalanche.tracer;

import java.util.HashMap;

public class Trace {
	private static Trace trace = null; 
	private HashMap<String, HashMap<Integer, Integer>> classMap = null;
	private Trace() {
	}
		
	private void setMap() {
		if (classMap == null) {
			classMap = TracerTestListener.getMap();
		}
	}
		
	public static Trace getInstance() {
		if (trace == null) {
			trace = new Trace();
			trace.setMap();
		}
		return trace;
	}
	
	/*
	 * This code is executed at the beginning of a method
	 */
	public synchronized void begin(String className, String methodName) {
		String key = className; //+ "." + methodName;
		if (!classMap.containsKey(key)) {
			HashMap<Integer, Integer> lineMap = new HashMap<Integer, Integer>((int)(1024 * 1.33));
			classMap.put(key, lineMap);
		}
	}
	
	/*
	 * This code is executed at the end of a method
	 */
	public synchronized void end(String className, String methodName) {		
	}
	
	/*
	 * This function is executed for every LineNumber
	 */
	public synchronized void logLineNumber(int line, String className, String methodName) {
		String key = className; // + "." + methodName;
		HashMap<Integer, Integer> lineMap = classMap.get(key);
		Integer intline = new Integer(line);
		if (!lineMap.containsKey(intline)) {
			lineMap.put(intline, 1);
		} else {
			lineMap.put(intline, 1 + lineMap.get(intline));
		}
	
	}
}
