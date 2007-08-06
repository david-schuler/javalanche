package org.softevo.mutation.coverageResults;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.softevo.mutation.io.Io;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;


public class TestSuiteCoverageResult {

	private Map<String, CoverageResult> results;

	public TestSuiteCoverageResult(Map<String, CoverageResult> results) {
		super();
		this.results = results;
	}

	public List<String> getTestsForLine(String className, int line) {
		CoverageResult coverageResult = results.get(className);
		if (coverageResult == null) {
			throw new RuntimeException("Classname not found:  " + className
					+ " Classes contained: " + results.keySet().toString());
		}
		return coverageResult.getTestCasesForLine(line);

	}

	@SuppressWarnings("unchecked")
	public static TestSuiteCoverageResult getFromXml() {
		Map<String, CoverageResult> map = (Map<String, CoverageResult>) XmlIo
				.fromXml(new File(MutationProperties.CLOVER_RESULTS_FILE));
		TestSuiteCoverageResult tscvr = new TestSuiteCoverageResult(map);
		return tscvr;
	}

	public static void main(String[] args) {
		TestSuiteCoverageResult ts = getFromXml();
		Mutations mutations = Mutations.fromXML();
		StringBuilder sb = new StringBuilder();
		Set<String> testNames = new HashSet<String>();
		for (Mutation mutation : mutations) {
			List<String> tests = ts.getTestsForLine(mutation.getClassName(), mutation
					.getLineNumber());
			for (String testName : tests) {
				testNames.add(testName);
			}
		}
		for(String testName : testNames){
			sb.append(testName);
			sb.append("\n");
		}
		File f = new File(MutationProperties.TESTS_TO_EXECUTE_FILE);
		Io.writeFile(sb.toString(), f);
	}
}
