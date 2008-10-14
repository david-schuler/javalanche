package de.unisb.cs.st.javalanche.mutation.coverageResults;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.unisb.cs.st.javalanche.mutation.coverageResults.db.TestCoverageClassResult;
import de.unisb.cs.st.javalanche.mutation.coverageResults.db.TestCoverageLineResult;
import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

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

	public void writeToDB() {
		for (Entry<String, CoverageResult> entry : results.entrySet()) {
			CoverageResult cr = entry.getValue();
			String className = entry.getKey();
			if (className.startsWith("config.clover_html.")) {
				className = className.replace("config.clover_html.", "");
			}
			Set<Entry<Integer, List<String>>> lineDataSet = cr.lineData
					.entrySet();
			List<TestCoverageLineResult> lineResults = new ArrayList<TestCoverageLineResult>();
			for (Entry<Integer, List<String>> lineDataEntry : lineDataSet) {
				int lineNumber = lineDataEntry.getKey();
				List<String> testNames = lineDataEntry.getValue();
				TestCoverageLineResult testCoverageLineResult = new TestCoverageLineResult(
						lineNumber, testNames);
				lineResults.add(testCoverageLineResult);
				QueryManager.save(testCoverageLineResult);
			}
			TestCoverageClassResult testCoverageClassResult = new TestCoverageClassResult(
					className, lineResults);
			QueryManager.save(testCoverageClassResult);

		}
	}

	public static void main(String[] args) {
		toDB();
	}

	public static void toDB() {
		TestSuiteCoverageResult tscr = getFromXml();
		tscr.writeToDB();
	}
}
