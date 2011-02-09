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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

public class DetectTestCaseAnalyzer implements MutationAnalyzer {

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		StringBuilder sb = new StringBuilder();
		for (Mutation m : mutations) {
			if (m.isKilled()) {
				sb.append("Mutation " + m.getId()
						+ " is detected by following tests: ");
				MutationTestResult mr = m.getMutationResult();
				Collection<TestMessage> errors = mr.getErrors();
				Collection<TestMessage> failures = mr.getFailures();
				Set<TestMessage> all = new HashSet<TestMessage>();
				all.addAll(errors);
				all.addAll(failures);
				for (TestMessage testMessage : all) {
					String testCaseName = testMessage.getTestCaseName();
					sb.append(testCaseName);
					sb.append(',');
				}
				sb.append('\n');
			}
		}
		return sb.toString();
	}

}
