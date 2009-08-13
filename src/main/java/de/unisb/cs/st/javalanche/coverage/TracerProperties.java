package de.unisb.cs.st.javalanche.coverage;

/**
 * @author Bernhard Gruen
 * 
 */
public final class TracerProperties {

	public final static String TRACER_CLASS_NAME = "de/unisb/cs/st/javalanche/tracer/Trace";

	public final static String NO_RESULT = "No Result";
	public final static String TRACE_RESULT_DIR = "mutation-files/tracer/";

	public final static String TRACE_RESULT_DATA_DIR = TRACE_RESULT_DIR
			+ "data/";
	public final static String TRACE_RESULT_LINE_DIR = TRACE_RESULT_DIR
			+ "line/";
	public final static String TRACE_CLASS_MASTERIDS = TRACE_RESULT_DIR
			+ "ids.list";
	public final static String TRACE_CLASS_IDFILE = "ids.list";

	public final static String TRACE_PROFILER_FILE = TRACE_RESULT_DIR
			+ "profiler-map.xml";

	public final static String TRACE_DONT_INSTRUMENT_FILE = TRACE_RESULT_DIR
			+ "dont-instrument-set.xml";
	public final static String TRACE_DIFFERENCES_FILE = TRACE_RESULT_DIR
			+ "differences-set.xml";


	public static final boolean TRACE_RETURNS = false;

	public static final boolean TRACE_LINES = true;

	
}
