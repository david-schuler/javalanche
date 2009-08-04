package de.unisb.cs.st.javalanche.rhino.coverage;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.Io;

public class FailureMatrix {

	private static Logger logger = Logger.getLogger(FailureMatrix.class);

	private Multimap<String, String> failureMap;

	public FailureMatrix(Multimap<String, String> failureMap) {
		super();
		this.failureMap = failureMap;
	}

	public static FailureMatrix parseFile(File f) {
		Multimap<String, String> failureMatrix = new HashMultimap<String, String>();
		List<String> linesFromFile = Io.getLinesFromFile(f);
		for (String line : linesFromFile) {
			String[] split = line.split(",");
			if (split.length > 0) {
				String failureId = split[0];
				for (int i = 1; i < split.length; i++) {
					failureMatrix.put(split[i],failureId);
					logger.info("Put: " + failureId + "  " + split[i]);
				}
			}
		}
		return new FailureMatrix(failureMatrix);
	}

	public int getDetectedFailures(Iterable<String> tests) {
		Set<String> failures = new HashSet<String>();
		for (String test : tests) {
			if (failureMap.containsKey(test)) {
				Collection<String> collection = failureMap.get(test);
				failures.addAll(collection);
			}
		}
		return failures.size();
	}

	public int getNumberOfFailures() {
		Set<String> failures = new HashSet<String>();
		Collection<String> values = failureMap.values();
		failures.addAll(values);
		return failures.size();
	}

	public static void main(String[] args) {
		String[] tests = new String[] { "js1_5/Regress/regress-114491.js" };
		FailureMatrix fm = parseFile(new File("/scratch/schuler/subjects/ibugs_rhino-0.1/failureMatrix.csv"));
		int detectedFailures = fm.getDetectedFailures(Arrays.asList(tests));
		System.out.println(detectedFailures);
	}
}
