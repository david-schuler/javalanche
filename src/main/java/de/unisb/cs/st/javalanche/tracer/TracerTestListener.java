 package de.unisb.cs.st.javalanche.tracer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;


public class TracerTestListener implements MutationTestListener {

	private static Logger logger = Logger.getLogger(TracerTestListener.class);

	private static Long mutation_id = new Long(-1);
	private String testName = null;

	private boolean saveFiles = false;

	private static HashMap<String, HashMap<Integer, Integer>> classMap = new HashMap<String, HashMap<Integer, Integer>>((int)(2048 * 1.33));

	private static HashMap<String, HashMap<Integer, Integer>> valueMap = new HashMap<String, HashMap<Integer, Integer>>();

	private static HashMap<String, Long> profilerMap = new HashMap<String, Long>();

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

	public static Long getMutationId() {
		return mutation_id;
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
	}

	private void createMutationDir() {
		File dir = new File(TracerConstants.TRACE_RESULT_DATA_DIR + mutation_id);
		if (!dir.exists()) {
			dir.mkdir();
		}

		dir = new File(TracerConstants.TRACE_RESULT_LINE_DIR + mutation_id);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void start() {
		System.out.println("TracerTestListener.start()");
		mutation_id = new Long(0);
		classMap.clear();
		valueMap.clear();
		createMutationDir();
		saveFiles = true;
	}

	public void end() {
		//serializeIdMap(mutation_id);
		writeProfilingData();
		System.out.println("TracerTestListener.end()");
		classMap.clear();
		valueMap.clear();
		saveFiles = false;
	}


	public void testStart(String testName) {
		this.testName = testName;
		classMap.clear();
		valueMap.clear();
		saveFiles = true;
	}

	public void testEnd(String testName) {
		serializeHashMap();
		serializeValueMap();
		classMap.clear();
		valueMap.clear();
		saveFiles = false;
	}

	public void mutationStart(Mutation mutation) {
		this.mutation_id = mutation.getId();
		/*
		idMap.clear();
		loadIdMap(0);
		loadIdMap(mutation_id);
		*/
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


	/*
	private void loadIdMap(long mutation_id) {
		if (mutation_id != 0) {
			File tmp = new File(TracerConstants.TRACE_RESULT_DIR + mutation_id + "-" + TracerConstants.TRACE_CLASS_IDFILE);
			if (!tmp.exists()) {
				return;
			}
		} else {
			File tmp = new File(TracerConstants.TRACE_CLASS_MASTERIDS);
			if (!tmp.exists()) {
				return;
			}
		}
		ObjectInputStream ois = null;
		try {
			if (mutation_id == 0 ) {
			ois = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(TracerConstants.TRACE_CLASS_MASTERIDS)));
			} else {
				ois = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(TracerConstants.TRACE_RESULT_DIR + mutation_id + "-" + TracerConstants.TRACE_CLASS_IDFILE)));
			}
			int numIds = ois.readInt();
			idMap = new HashMap<String, Integer>();
			for (int i = 0; i < numIds; i++) {
				String className = ois.readUTF();
				int id = ois.readInt();
				idMap.put(className, id);
			}
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mutation_id == 0) {
			idMapMasterSize = idMap.size();
		}
	}
	*/

	private void serializeHashMap() {
		if (!saveFiles) {
			logger.warn("Double Call to serializeHashMap");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(TracerConstants.TRACE_RESULT_LINE_DIR + mutation_id + "/" + testName);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    ObjectOutputStream oos = new ObjectOutputStream(bos);

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
			FileOutputStream fos = new FileOutputStream(TracerConstants.TRACE_RESULT_DATA_DIR + mutation_id + "/" + testName);
			//GZIPOutputStream gos = new GZIPOutputStream();
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
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
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	private void serializeIdMap(long mutation_id) {
		if (idMap.size() == idMapMasterSize) {
			return;
		}
		try {
			FileOutputStream fos;
			if (mutation_id == 0) {
				 fos = new FileOutputStream(TracerConstants.TRACE_CLASS_MASTERIDS);
			} else {
				fos = new FileOutputStream(TracerConstants.TRACE_RESULT_DIR + mutation_id + "-" + TracerConstants.TRACE_CLASS_IDFILE);
			}
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    ObjectOutputStream oos = new ObjectOutputStream(bos);
		    Set<String> ks = idMap.keySet();
		    Iterator<String> it = ks.iterator();
		    oos.writeInt(idMap.size());

		    String s = "";

		    while (it.hasNext()) {
		    	s = it.next();
				oos.writeUTF(s);
				oos.writeInt(idMap.get(s));
		    }
			oos.close();
			bos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
