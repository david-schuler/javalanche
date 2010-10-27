package de.unisb.cs.st.javalanche.mutation.adaptedMutations.analyze;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

//analyzeResults -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.mutation.adaptedMutations.analyze.DetectedByTestAnalyzer  -Djavalanche.maxmemory=5g 
public class DetectedByTestAnalyzer implements MutationAnalyzer {

	private static final String NO_TEST = "NO_TEST";
	private static final String FILE_NAME = "detected-by-test-multimap.xml";

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		if (MutationProperties.STOP_AFTER_FIRST_FAIL) {
			throw new RuntimeException("Make sure that property "
					+ MutationProperties.STOP_AFTER_FIRST_FAIL_KEY
					+ "is set to false");
		}
		Multimap<String, Long> detectedByTestCase = HashMultimap.create();
		int detectedMutations = 0;
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				putAll(detectedByTestCase, mutation, mutationResult.getErrors());
				putAll(detectedByTestCase, mutation, mutationResult
						.getFailures());
				detectedMutations++;
			} else {
				// detectedByTestCase.put(NO_TEST, mutation.getId());
			}
		}
		Map<String, Set<Long>> serializeMap = new HashMap<String, Set<Long>>();
		for (String key : detectedByTestCase.keySet()) {
			Collection<Long> collection = detectedByTestCase.get(key);
			Set<Long> values = new HashSet<Long>();
			values.addAll(collection);
			serializeMap.put(key, values);
		}
		XmlIo.toXML(serializeMap, getOutFile());
		return "Results for " + detectedByTestCase.keySet().size()
				+ " tests and " + detectedMutations;
	}

	public static File getOutFile() {
		File outDir = new File(MutationProperties.OUTPUT_DIR);
		return new File(outDir, FILE_NAME);
	}

	private void putAll(Multimap<String, Long> detectedByTestCase,
			Mutation mutation, Collection<TestMessage> errors) {
		Long id = mutation.getId();
		for (TestMessage testMessage : errors) {
			detectedByTestCase.put(testMessage.getTestCaseName(), id);
		}
	}

	public static Map<String, Set<Long>> getDetectedByTestCaseMap() {
		File f = getOutFile();
		return (Map<String, Set<Long>>) XmlIo.fromXml(f);
	}

}
