package de.unisb.cs.st.javalanche.mutation.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;

import de.unisb.cs.st.ds.util.io.SerializeIo;

public class FilePerformanceTest {

	public static void main(String[] args) throws IOException {
		int limit = 1000;
		int total = 0;
		StopWatch stp = new StopWatch();
		stp.start();
		File dir = new File("mutation-files/tmp");
		dir.mkdir();
		for (int i = 0; i < limit; i++) {
			Map<String, Set<Integer>> map = getMap();
			File tempFile = new File(dir, "test-" + i + ".ser");
			if (!tempFile.exists()) {
				SerializeIo.serializeToFile(map, tempFile);
			} else {
				Map<String, Set<Integer>> deserialize = SerializeIo
						.get(tempFile);
				total += deserialize.size();
			}
		}
		System.out.println("Handling " + limit + " files took "
				+ DurationFormatUtils.formatDurationHMS(stp.getTime()));
	}

	private static Map<String, Set<Integer>> getMap() {
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		for (int i = 0; i < 100; i++) {
			map.put("key" + i, getSet(10));
		}
		return map;
	}

	static Random r = new Random();

	private static Set<Integer> getSet(int limit) {
		Set<Integer> s = new HashSet<Integer>();
		for (int i = 0; i < limit; i++) {
			s.add(r.nextInt());
		}
		return s;
	}
}
