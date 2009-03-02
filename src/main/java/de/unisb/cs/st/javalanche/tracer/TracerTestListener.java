 package de.unisb.cs.st.javalanche.tracer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.RunMode;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;


public class TracerTestListener implements MutationTestListener {

	private static Logger logger = Logger.getLogger(TracerTestListener.class);

	private static boolean isPermutated = false;
	private static HashSet<String> seenTests = new HashSet<String>();
	
	private static Long mutation_id = new Long(-1);
	private String testName = null;

	private boolean saveFiles = false;

	private static HashMap<String, HashMap<Integer, Integer>> classMap = new HashMap<String, HashMap<Integer, Integer>>((int)(2048 * 1.33));

	private static HashMap<String, HashMap<Integer, Integer>> valueMap = new HashMap<String, HashMap<Integer, Integer>>();

	private static HashMap<String, Long> profilerMap = new HashMap<String, Long>();
	
	private static HashSet<String> dontInstrumentSet = new HashSet<String>();

	//private static HashMap<String, Integer> idMap = new HashMap<String, Integer>();
	//private static int idMapMasterSize = 0;

	public static HashMap<String, HashMap<Integer, Integer>> getLineCoverageMap() {
		return classMap;
	}

	public static HashMap<String, HashMap<Integer, Integer>> getValueMap() {
		return valueMap;
	}

	public static HashMap<String, Long> getProfilerMap() {
		return profilerMap;
	}

	public static HashSet<String> getDontInstrumentSet() {
		return dontInstrumentSet;
	}

	
	public static Long getMutationId() {
		return mutation_id;
	}
	
	public static String getMutationIdFileName() {
		if (mutation_id < 0) {
			return "PERMUTATED_" + Math.abs(mutation_id);
		} else {
			return mutation_id.toString();
		}
	}


	/*
	public static HashMap<String, Integer> getIdMap() {
		return idMap;
	}
	*/

	public TracerTestListener() {
		File dir = new File(TracerConstants.TRACE_RESULT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(TracerConstants.TRACE_RESULT_DATA_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(TracerConstants.TRACE_RESULT_LINE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		if (MutationProperties.RUN_MODE == RunMode.TEST_PERMUTED) {
			isPermutated = true;
		}
	}

	private void createMutationDir() {		
		File dir = new File(TracerConstants.TRACE_RESULT_DATA_DIR + getMutationIdFileName());
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(TracerConstants.TRACE_RESULT_LINE_DIR + getMutationIdFileName());
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void start() {
		System.out.println("TracerTestListener.start()");
		mutation_id = new Long(0);	
		
		loadDontInstrument();
		classMap.clear();
		valueMap.clear();
		saveFiles = true;
	}

	public void end() {
		//serializeIdMap(mutation_id);
		writeProfilingData();
		writeDontInstrument();
		System.out.println("TracerTestListener.end()");
		classMap.clear();
		valueMap.clear();
		saveFiles = false;
	}


	public void testStart(String testName) {
		this.testName = testName;
		if (isPermutated) {
			if (seenTests.contains(testName)) {
				seenTests.clear();
				mutation_id--;
			}
			seenTests.add(testName);
		}
		classMap.clear();
		valueMap.clear();
		saveFiles = true;
	}

	public void testEnd(String testName) {
		createMutationDir();
		serializeHashMap();
		serializeValueMap();
		classMap.clear();
		valueMap.clear();
		saveFiles = false;
	}

	public void mutationStart(Mutation mutation) {
		mutation_id = mutation.getId();
		classMap.clear();
		valueMap.clear();
		createMutationDir();
		saveFiles = true;
	}

	public void mutationEnd(Mutation mutation) {
		//serializeIdMap(mutation_id);
		classMap.clear();
		valueMap.clear();
		saveFiles = false;
	}

	private void writeProfilingData() {
		if (mutation_id != 0) {
			return;
		}
		XmlIo.toXML(profilerMap, TracerConstants.TRACE_PROFILER_FILE);
	}
	
	private void loadDontInstrument() {
		if (new File(TracerConstants.TRACE_DONT_INSTRUMENT_FILE).exists()) {
			dontInstrumentSet =  XmlIo.get(TracerConstants.TRACE_DONT_INSTRUMENT_FILE);
		}
	}
	
	
	private void writeDontInstrument() {
		if (mutation_id != 0) {
			return;
		}
		XmlIo.toXML(dontInstrumentSet, TracerConstants.TRACE_DONT_INSTRUMENT_FILE);

	}


	private void serializeHashMap() {
		if (!saveFiles) {
			logger.warn("Double Call to serializeHashMap");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(TracerConstants.TRACE_RESULT_LINE_DIR + getMutationIdFileName() + "/" + testName + ".gz");
			GZIPOutputStream gos = new GZIPOutputStream(fos);
		    BufferedOutputStream bos = new BufferedOutputStream(gos);
		    ObjectOutputStream oos = new ObjectOutputStream(gos);

		    HashMap<Integer, Integer> lineMap = new HashMap<Integer, Integer>();

			Set<String> ks = classMap.keySet();
			Iterator<String> it = ks.iterator();
			String s;
			Set<Integer> ks2;
			Iterator<Integer> it2;
			Integer i;

			oos.writeInt(classMap.size());
			//System.out.print(testName+":");
			while (it.hasNext()) {
				s = it.next();
				oos.writeUTF(s);
				lineMap = classMap.get(s);
				ks2 = lineMap.keySet();
				it2 = ks2.iterator();
				oos.writeInt(lineMap.size());
				//System.out.println(s);
				while (it2.hasNext()) {
					i = it2.next();
					oos.writeInt(i);
					oos.writeInt(lineMap.get(i));
					//System.out.println(i);
				}
			}
			oos.close();
			bos.close();
			gos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void serializeValueMap() {
		if (!saveFiles) {
			logger.warn("Double Call to serializeValueMap");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(TracerConstants.TRACE_RESULT_DATA_DIR + getMutationIdFileName() + "/" + testName + ".gz");
			GZIPOutputStream gos = new GZIPOutputStream(fos);
		    BufferedOutputStream bos = new BufferedOutputStream(gos);
		    ObjectOutputStream oos = new ObjectOutputStream(bos);

		    HashMap<Integer, Integer> lineMap = new HashMap<Integer, Integer>();

			Set<String> ks = valueMap.keySet();
			Iterator<String> it = ks.iterator();
			String s;
			Set<Integer> ks2;
			Iterator<Integer> it2;
			Integer i;

			oos.writeInt(valueMap.size());
			//System.out.print(testName+":");
			while (it.hasNext()) {
				s = it.next();
				oos.writeUTF(s);
				lineMap = valueMap.get(s);
				ks2 = lineMap.keySet();
				it2 = ks2.iterator();
				oos.writeInt(lineMap.size());
				//System.out.println(s);
				while (it2.hasNext()) {
					i = it2.next();
					oos.writeInt(i);
					oos.writeInt(lineMap.get(i));
					//System.out.println(i);
				}
			}
			oos.close();
			bos.close();
			gos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
