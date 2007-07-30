package org.softevo.mutation.coverageResults;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.softevo.mutation.io.Io;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.mutationPossibilities.MutationPossibility;
import org.softevo.mutation.mutationPossibilities.Mutations;
import org.softevo.mutation.properties.MutationProperties;

public class TestSuiteCoverageResult {

	private Map<String, CoverageResult> results;

	public TestSuiteCoverageResult(Map<String, CoverageResult> results) {
		super();
		this.results = results;
	}

	public List<String> getTestsForLine(String className, int line) {
		CoverageResult coverageResult = results.get(className);
		return coverageResult.getTestCasesForLine(line);

	}

	@SuppressWarnings("unchecked")
	private static TestSuiteCoverageResult getFromXml() {
		Map<String, CoverageResult> map = (Map<String, CoverageResult>) XmlIo
				.fromXml(new File(MutationProperties.TEST_FILE));
		TestSuiteCoverageResult tscvr = new TestSuiteCoverageResult(map);
		return tscvr;
	}

	public static void main(String[] args) {
		TestSuiteCoverageResult ts = getFromXml();
		Mutations mutations = Mutations.fromXML();
		StringBuilder sb = new StringBuilder();
		for (MutationPossibility mp : mutations) {

			List<String> tests = ts.getTestsForLine(mp.getClassName(), mp
					.getLineNumber());
			for (String s : tests) {
				sb.append(s);
				sb.append('\n');
			}
		}
		File f = new File(MutationProperties.TEST_FILE);
		Io.writeFile(sb.toString(), f);
	}
}
