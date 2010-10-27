/*
* Copyright (C) 2009 Saarland University
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

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * @author Bernhard Gruen
 * 
 */
public final class CoverageProperties {

	public final static String TRACER_CLASS_NAME = "de/unisb/cs/st/javalanche/coverage/Tracer";

	public final static String NO_RESULT = "No Result";
	public static String TRACE_RESULT_DIR = "mutation-files/tracer/";

	public static String TRACE_RESULT_DATA_DIR = TRACE_RESULT_DIR
			+ "data/";
	public static String TRACE_RESULT_LINE_DIR = TRACE_RESULT_DIR
			+ "line/";
	public static String TRACE_CLASS_MASTERIDS = TRACE_RESULT_DIR
			+ "ids.list";
	public static String TRACE_CLASS_IDFILE = "ids.list";

	public static String TRACE_PROFILER_FILE = TRACE_RESULT_DIR
			+ "profiler-map.xml";

	public static String TRACE_DIFFERENCES_FILE = TRACE_RESULT_DIR
			+ "differences-set.xml";


	private static final String TRACE_RETRURNS_KEY = "javalanche.trace.returns";
	public static final boolean TRACE_RETURNS = MutationProperties
			.getPropertyOrDefault(TRACE_RETRURNS_KEY, true);

	private static final String TRACE_LINES_KEY = "javalanche.trace.lines";
	public static final boolean TRACE_LINES = MutationProperties
			.getPropertyOrDefault(TRACE_LINES_KEY, true);

	public static final String EPSILON_KEY = "javalanche.coverage.epsilon";

	public static final double EPSILON = MutationProperties
			.getPropertyOrDefault(EPSILON_KEY, 0);

	
	public static void updateBaseDir(String base) {
		TRACE_RESULT_DIR = base + "/mutation-files/tracer/";
		TRACE_RESULT_DATA_DIR = TRACE_RESULT_DIR
				+ "data/";
		TRACE_RESULT_LINE_DIR = TRACE_RESULT_DIR
				+ "line/";
		TRACE_CLASS_MASTERIDS = TRACE_RESULT_DIR
				+ "ids.list";
		TRACE_PROFILER_FILE = TRACE_RESULT_DIR
				+ "profiler-map.xml";
		
		TRACE_DIFFERENCES_FILE = TRACE_RESULT_DIR
				+ "differences-set.xml";
	}


	static final String PERMUTED_PREFIX = "PERMUTED_";
}
