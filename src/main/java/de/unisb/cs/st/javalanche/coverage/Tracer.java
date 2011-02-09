/*
 * Copyright (C) 2011 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.coverage;

//import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * @author Bernhard Gruen
 * 
 */
public class Tracer {
	private static Logger logger = Logger.getLogger(Tracer.class);

	private static Tracer trace = null;

	// private Map<String, Map<Integer, Integer>> classMap = null;
	// private Map<String, Map<Integer, Integer>> valueMap = null;
	// private Map<String, Long> profilerMap = null;
	// private HashMap<String, Integer> idMap = null;
	// private boolean isLineCoverageDeactivated = false;
	// private boolean isDataCoverageDeactivated = false;

	public AtomicBoolean tracingDeacivated = new AtomicBoolean();

	private Tracer() {
	}

	public static Tracer getInstance() {
		if (trace == null) {
			trace = new Tracer();
		}
		return trace;
	}

	/*
	 * This code is executed at the beginning of a method
	 */
	public void begin(String className, String methodName,
			boolean instrumentLine, boolean instrumentData) {
		// Integer key = getId(className + "@" + methodName);
		String key = className + "@" + methodName;
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> classMap = CoverageMutationListener.lineMapRef
				.get();
		if (instrumentLine && !classMap.containsKey(key)) {
			ConcurrentMap<Integer, Integer> lineMap = new ConcurrentHashMap<Integer, Integer>();
			classMap.put(key, lineMap);

		}
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> valueMap = CoverageMutationListener.valueMapRef
				.get();
		if (instrumentData && !valueMap.containsKey(key)) {
			valueMap.put(key, new ConcurrentHashMap<Integer, Integer>());
		}

		// if (CoverageMutationListener.getMutationId() == 0) {
		// if (!profilerMap.containsKey(key)) {
		// profilerMap.put(key, 1L);
		// } else {
		// profilerMap.put(key, 1L + profilerMap.get(key));
		// }
		// }
	}

	/*
	 * This code is executed at the end of a method
	 */
	public void end(String className, String methodName) {
		// System.out.println("end called: "+ className + "@" + methodName);
	}

	/*
	 * This function is executed for every LineNumber
	 */
	public void logLineNumber(int line, String className, String methodName) {
		if (tracingDeacivated.get()) {
			// logger.info("Excluding line " + line + "  " + className + "."
			// + methodName);
			return;
		}
		// Integer key = getId(className + "@" + methodName);
		// logger.info("Line " + line + "  " + className + "." + methodName);
		String key = className + "@" + methodName;
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> classMap = CoverageMutationListener.lineMapRef
				.get();
		if (!classMap.containsKey(key)) {
			ConcurrentMap<Integer, Integer> lineMap = new ConcurrentHashMap<Integer, Integer>();
			ConcurrentMap<Integer, Integer> putIfAbsent = classMap.putIfAbsent(
					key, lineMap);
			if (putIfAbsent != null) {
				lineMap = putIfAbsent;
			}
		}
		Map<Integer, Integer> lineMap = classMap.get(key);
		Integer intline = Integer.valueOf(line);
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
		if (InstrumentExclude.shouldExcludeReturns(className, methodName)
				|| tracingDeacivated.get()) {
			return;
		}
		if (value == null) {
			logData(0, className, methodName);
			return;
		}
		StringBuilder tmp = null;
		try {
			// setLineCoverageDeactivated(true);
			tracingDeacivated.set(true);
			tmp = new StringBuilder(value.toString());
		} catch (Throwable t) {
			InstrumentExclude.addExcludeReturn(className, methodName);
			logger.warn(
					"To string for return object throws an exception. Class: "
							+ className + " MethodName: " + methodName, t);
			InstrumentExclude.save();
			return;
		} finally {
			tracingDeacivated.set(false);
		}
		int index = 0;
		int position = 0;
		boolean found = false;
		boolean deleteAddresses = true;
		char c = ' ';
		// quite fast method to detect memory addresses in Strings.
		while ((position = tmp.indexOf("@", index)) > 0) {
			for (index = position + 1; index < position + 17
					&& index < tmp.length(); index++) {
				c = tmp.charAt(index);
				if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')
						|| (c >= 'A' && c <= 'F')) {
					found = true;
				} else {
					break;
				}
			}
			if (deleteAddresses && found) {
				tmp.delete(position + 1, index);
			}
		}
		// if (deleteAddresses || !found) {
		logData(tmp.toString().hashCode(), className, methodName);
		// }
	}

	private void logData(int value, String className, String methodName) {
		if (InstrumentExclude.shouldExcludeReturns(className, methodName)
				|| tracingDeacivated.get()) {
			return;
		}
		String key = className + "@" + methodName;
		ConcurrentMap<String, ConcurrentMap<Integer, Integer>> valueMap = CoverageMutationListener.valueMapRef
				.get();
		Map<Integer, Integer> tmpMap = valueMap.get(key);
		if (tmpMap == null) {
			// logger.warn("Not logging return ");
			return;
		}

		Integer intvalue = new Integer(value);
		if (!valueMap.containsKey(intvalue)) {
			tmpMap.put(intvalue, 1);
		} else {
			tmpMap.put(intvalue, 1 + tmpMap.get(intvalue));
		}
	}

	public void deactivateTrace() {
		tracingDeacivated.set(true);
	}

	public void activateTrace() {
		tracingDeacivated.set(false);
	}

	/*
	 * private Integer getId(String key) { if (idMap.containsKey(key)) { return
	 * idMap.get(key); } else { int id = idMap.size() + 1; idMap.put(key, id);
	 * return id; } }
	 */

}
