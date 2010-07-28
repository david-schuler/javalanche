package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class SiemensMutationAnalyzer implements MutationAnalyzer {

	private static final String NO_TEST = "NO_TEST";
	private static final String FILE_NAME = "detected-by-test-multimap.xml";

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		Multimap<String, Long> detectedByTestCase = HashMultimap.create();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				putAll(detectedByTestCase, mutation, mutationResult.getErrors());
				putAll(detectedByTestCase, mutation, mutationResult.getFailures());
			} else {
				detectedByTestCase.put(NO_TEST, mutation.getId());
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
		return "Results for " + detectedByTestCase.keySet().size();
	}

	private File getOutFile() {
		String property = System.getProperty("prioritization.dir");
		if (property != null) {
			File dir = new File(property);
			if (dir.exists()) {
				return new File(dir, FILE_NAME);
			}
		}
		return new File(FILE_NAME);
	}

	private void putAll(Multimap<String, Long> detectedByTestCase,
			Mutation mutation, Collection<TestMessage> errors) {
		Long id = mutation.getId();
		for (TestMessage testMessage : errors) {
			detectedByTestCase.put(testMessage.getTestCaseName(), id);
		}
	}

}
