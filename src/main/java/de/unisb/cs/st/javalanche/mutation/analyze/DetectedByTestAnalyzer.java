/*
* Copyright (C) 2011 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

//de.unisb.cs.st.javalanche.mutation.analyze.DetectedByTestAnalyzer
public class DetectedByTestAnalyzer implements MutationAnalyzer {

	Map<String, Integer> testsIds = new HashMap<String, Integer>();
	int testId = 0;

	Joiner commaJoiner = Joiner.on(',');

	@Override
	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		List<String> lines = new ArrayList<String>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult result = mutation.getMutationResult();
				Set<TestMessage> detecting = new HashSet<TestMessage>();
				Collection<TestMessage> failures = result.getFailures();
				detecting.addAll(failures);
				Collection<TestMessage> errors = result.getErrors();
				detecting.addAll(errors);
				String tests = getIds(detecting);
				String line = mutation.getId() + "," + tests;
				lines.add(line);
			}
		}
		Set<Entry<String, Integer>> entrySet = testsIds.entrySet();
		lines.add("Ids");
		for (Entry<String, Integer> entry : entrySet) {
			lines.add(entry.getKey() + "," + entry.getValue());
		}
		try {
			FileUtils.writeLines(new File("detectedByTest.csv"), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Joiner.on("\n").join(lines);
	}

	private String getIds(Set<TestMessage> detecting) {
		Set<Integer> ids = new HashSet<Integer>();
		for (TestMessage testMessage : detecting) {
			int id = -1;
			if (testsIds.containsKey(testMessage.getTestCaseName())) {
				id = testsIds.get(testMessage.getTestCaseName());
			} else {
				testsIds.put(testMessage.getTestCaseName(), testId);
				id = testId;
				testId++;
			}
			ids.add(id);
		}
		return commaJoiner.join(ids);
	}

}
