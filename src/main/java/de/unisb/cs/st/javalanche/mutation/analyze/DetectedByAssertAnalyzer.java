package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

//    -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.mutation.analyze.DetectedByAssertAnalyzer
public class DetectedByAssertAnalyzer implements MutationAnalyzer {

	private static final Logger logger = Logger
			.getLogger(DetectedByAssertAnalyzer.class);

	private static class Location {
		String className;

		int line;

		private Location(String className, int line) {
			super();
			this.className = className;
			this.line = line;
		}

		@Override
		public String toString() {
			return className + "-" + line;
		}

	}

	private String prefix = ConfigurationLocator.getJavalancheConfiguration()
			.getProjectPrefix();

	@Override
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		HashMultimap<String, Long> mutationPerAssert = HashMultimap.create();
		int totalMutations = 0;
		for (Mutation m : mutations) {
			totalMutations++;
			MutationTestResult mutationResult = m.getMutationResult();
			mutationPerAssert.put("non.existing.location-1", m.getId());
			if (mutationResult != null) {
				Collection<TestMessage> failures = mutationResult.getFailures();
				boolean foundByAssert = false;
				for (TestMessage tm : failures) {
					String message = tm.getMessage();
					List<Location> locations = parseLocation(message);
					for (Location loc : locations) {
						if (loc == null) {
							logger.warn("No location found for failing test "
									+ tm);
						} else {
							mutationPerAssert.put(loc.toString(), m.getId());
							foundByAssert = true;
						}
					}
				}
				if (!foundByAssert && m.isDetected()) {
					mutationPerAssert.put("implicit", m.getId());
				}
			}

		}
		String message = "Assert locations: "
				+ mutationPerAssert.keySet().size();
		message += "\nTotal mappings: " + mutationPerAssert.size();
		message += "\nTotal mutations: " + totalMutations;
		try {
			SerializationUtils.serialize(mutationPerAssert,
					new FileOutputStream(new File("mutationPerAssert.ser")));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return message;
	}

	private List<Location> parseLocation(String message) {
		List<String> lines = getRelevantStackTraceLine(message);

		List<Location> result = new ArrayList<Location>();
		for (String line : lines) {
			// logger.info("Getting location info for line " + line);
			Location location = parseLocationFromLine(line);
			if (location != null && location.className.startsWith(prefix)) {
				result.add(location);
			}
		}
		return result;
	}

	public Location parseLocationFromLine(String line) {
		int s = line.indexOf('(');
		if (s < 0) {
			return null;
		}
		String sub1 = line.substring(3, s);
		int e1 = sub1.lastIndexOf('.');
		if (e1 < 0) {
			return null;
		}
		String className = sub1.substring(0, e1);
		int s2 = line.indexOf(':');
		int e2 = line.indexOf(')');
		if (s2 < 0 || e2 < 0) {
			return null;
		}
		String lineNumStr = line.substring(s2 + 1, e2);
		int lineNumber = Integer.parseInt(lineNumStr);
		return new Location(className, lineNumber);
	}

	private List<String> getRelevantStackTraceLine(String message) {
		String[] split = message.split("\n");

		List<String> result = new ArrayList<String>();
		for (String string : split) {
			String trimmed = string.trim();
			if (trimmed.startsWith("at")) {
				if (!(trimmed.contains("org.junit") || trimmed
						.contains("junit.framework."))) {
					result.add(trimmed);
				}
			}
		}
		return result;
	}

}
