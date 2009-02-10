package de.unisb.cs.st.javalanche.tracer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

public class TracerTestListener implements MutationTestListener {

	static final String TRACE_RESULT_DIR = "mutation-files/tracer/";
	
	private static Logger logger = Logger.getLogger(TracerTestListener.class);

	private Long mutation_id = new Long(0);
	private String testName = null;
	
	private boolean saveFiles = false;

	private static HashMap<String, HashMap<Integer, Integer>> classMap = new HashMap<String, HashMap<Integer, Integer>>((int)(2048 * 1.33));


	public static HashMap<String, HashMap<Integer, Integer>> getMap() {
		return classMap;
	}

	public TracerTestListener() {
		File dir = new File(TRACE_RESULT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private void createMutationDir() {
		File dir = new File(TRACE_RESULT_DIR+mutation_id);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void start() {		
		System.out.println("TracerTestListener.start()");
		mutation_id = new Long(0);
		classMap.clear();
		createMutationDir();
		saveFiles = true;
	}

	public void end() {
		System.out.println("TracerTestListener.end()");
		classMap.clear();
		saveFiles = false;
	}


	public void testStart(String testName) {
		this.testName = testName;
		classMap.clear();
		saveFiles = true;
	}

	public void testEnd(String testName) {
		serializeHashMap();
		classMap.clear();
		saveFiles = false;
	}

	public void mutationStart(Mutation mutation) {
		this.mutation_id = mutation.getId();
		classMap.clear();
		createMutationDir();
		saveFiles = true;
	}

	public void mutationEnd(Mutation mutation) {
		classMap.clear();
		saveFiles = false;
	}

	private void serializeHashMap() {
		if (!saveFiles) {
			logger.warn("Double Call to serializeHashMap");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(TRACE_RESULT_DIR + mutation_id + "/" + testName + ".dat");
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

}
