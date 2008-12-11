package de.unisb.cs.st.javalanche.mutation.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;

import de.unisb.cs.st.javalanche.mutation.analyze.tools.MutationDeleter;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class DBPerformanceTest {

	private static final String PREFIX = "PerformanceTestMutation";
	static {
		System.setProperty("mutation.package.prefix", PREFIX);
	}
	private static final int LIMIT = 1000;
	private List<Mutation> mutations;

	public static void main(String[] args) {
		DBPerformanceTest db = new DBPerformanceTest();
		testWrite();
		db.testInsert();
		db.queryMutations();
		db.testDelete();
	}

	private static void testWrite() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			File file = new File("test.txt");
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(fw);
			for (int i = 0; i < 50 * 1024 * 1024; i++) {
				w.append('x');
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stopWatch.stop();
		System.out.printf("Writing file took %s\n", DurationFormatUtils
				.formatDurationHMS(stopWatch.getTime()));

	}

	private void queryMutations() {
		List<Long> ids = new ArrayList<Long>();
		for (Mutation m : getMutations()) {
			ids.add(m.getId());
		}
		StringBuilder sb = new StringBuilder();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List<Mutation> mutations = QueryManager.getMutationsFromDbByID(ids
				.toArray(new Long[0]));
		for (Mutation mutation : mutations) {
			sb.append(mutation.getClassName());
		}
		stopWatch.stop();
		System.out.printf("Querying %d mutations took %s -- checkvalue: %d\n",
				LIMIT, DurationFormatUtils.formatDurationHMS(stopWatch
						.getTime()), sb.length());
	}

	private void testDelete() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		MutationDeleter.deleteAllWithPrefix();
		stopWatch.stop();
		System.out.printf("Deleting %d mutations took %s\n", LIMIT,
				DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
	}

	private void testInsert() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		QueryManager.saveMutations(getMutations());
		stopWatch.stop();
		System.out.println("Inserting " + LIMIT + " mutations took "
				+ DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));

	}

	private List<Mutation> getMutations() {
		if (mutations == null) {
			mutations = new ArrayList<Mutation>();
			for (int i = 0; i < LIMIT; i++) {
				mutations.add(new Mutation(PREFIX + i, i, i % 4,
						MutationType.RIC_MINUS_1, false));
			}
		}
		return mutations;

	}
}
