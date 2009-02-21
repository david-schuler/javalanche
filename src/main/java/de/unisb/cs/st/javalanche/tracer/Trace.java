package de.unisb.cs.st.javalanche.tracer;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class Trace {
	private static Logger logger = Logger.getLogger(Trace.class);

	private static Trace trace = null;
	private HashMap<String, HashMap<Integer, Integer>> classMap = null;
	private HashMap<String, HashMap<Integer, Integer>> valueMap = null;
	private HashMap<String, Long> profilerMap = null;
	//private HashMap<String, Integer> idMap = null;

	private boolean isLineCoverageDeactivated = false;
	private boolean isDataCoverageDeactivated = false;


	private Trace() {
	}

	private void setMap() {
		if (classMap == null) {
			classMap = TracerTestListener.getLineCoverageMap();
		}
		if (valueMap == null) {
			valueMap = TracerTestListener.getValueMap();
		}
		if (profilerMap == null) {
			profilerMap = TracerTestListener.getProfilerMap();
		}

		/*
		if (idMap == null) {
			idMap = TracerTestListener.getIdMap();
		}
		*/

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
	public synchronized void begin(String className, String methodName, boolean instrumentLine, boolean instrumentData) {
		//Integer key = getId(className + "@" + methodName);
		String key = className + "@" + methodName;
		if (instrumentLine && !classMap.containsKey(key)) {
			HashMap<Integer, Integer> lineMap = new HashMap<Integer, Integer>((int)(1024 * 1.33));
			classMap.put(key, lineMap);

		}
		if (instrumentData && !valueMap.containsKey(key)) {
			valueMap.put(key, new HashMap<Integer, Integer>());
		}

		if (TracerTestListener.getMutationId() == 0) {
			if (!profilerMap.containsKey(key)) {
				profilerMap.put(key, 1L);
			} else {
				profilerMap.put(key, 1L + profilerMap.get(key));
			}
		}
	}

	/*
	 * This code is executed at the end of a method
	 */
	public void end(String className, String methodName) {
		//System.out.println("end called: "+ className + "@" + methodName);
	}

	/*
	 * This function is executed for every LineNumber
	 */
	public synchronized void logLineNumber(int line, String className, String methodName) {
		if (isLineCoverageDeactivated) {
			return;
		}
		//Integer key = getId(className + "@" + methodName);
		String key = className + "@" + methodName;
		HashMap<Integer, Integer> lineMap = classMap.get(key);
		Integer intline = new Integer(line);
		if (!lineMap.containsKey(intline)) {
			lineMap.put(intline, 1);
		} else {
			lineMap.put(intline, 1 + lineMap.get(intline));
		}

	}

	/*
	 * This code is executed at the end of a method
	 */
	public void logIReturn(int value, String className, String methodName) {
		logData(value, className, methodName);
	}

	public void logLReturn(int value, String className, String methodName) {
		logData(value, className, methodName);
	}

	public void logDReturn(int value, String className, String methodName) {
		logData(value, className, methodName);
	}

	public void logFReturn(int value, String className, String methodName) {
		logData(value, className, methodName);
	}


	public void logAReturn(Object value, String className, String methodName) {
		if(value == null){
			return; // TODO handle nulls
		}
		StringBuffer tmp = null;
		try{
		tmp = new StringBuffer(value.toString());
		}catch(Throwable t){
			logger.warn("To string for return object throws an exception. Class: " + className  + " MethodName: " + methodName,t);
			return;
		}
		int index = 0;
		int position = 0;
		boolean found = false;
		boolean deleteAddresses = false;
		char c = ' ';
		// quite fast method to detect memory addresses in Strings.
		while ((position = tmp.indexOf("@", index)) > 0) {
			for (index = position + 1; index < position + 17; index++) {
				c = tmp.charAt(index);

				if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
					found = true;
					if (!deleteAddresses) {
						break;
					}
				} else {
					break;
				}
			}
			if (deleteAddresses && found) {
				tmp.delete(position + 1, index);
			}
		}
		if (deleteAddresses || !found) {
			logData(tmp.toString().hashCode(), className, methodName);
		}
	}



	private synchronized void logData(int value, String className, String methodName) {
		if (isDataCoverageDeactivated) {
			return;
		}
		isLineCoverageDeactivated = true;

		//Integer key = getId(className + "@" + methodName);
		String key = className + "@" + methodName;
		HashMap<Integer, Integer> tmpMap = valueMap.get(key);

		// safety - test if the value is there
		if (tmpMap == null) {
			return;
		}

		Integer intvalue = new Integer(value);
		if (!valueMap.containsKey(intvalue)) {
			tmpMap.put(intvalue, 1);
		} else {
			tmpMap.put(intvalue, 1 + tmpMap.get(intvalue));
		}

		isLineCoverageDeactivated = false;
	}
	/*
	private Integer getId(String key) {
		if (idMap.containsKey(key)) {
			return idMap.get(key);
		} else {
			int id = idMap.size() + 1;
			idMap.put(key, id);
			return id;
		}
	}
	*/

}
