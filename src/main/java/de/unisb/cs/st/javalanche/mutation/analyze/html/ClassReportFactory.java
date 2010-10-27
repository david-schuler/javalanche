/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.analyze.html;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class ClassReportFactory {

	private static final String DETECTED = "Detected";
	private static final String TYPE = "Type";
	private static final String LINE = "Line";
	private static final String ID = "ID";

	public static ClassReport getClassReport(String className,
			Iterable<String> content, List<Mutation> mutations) {
		sortMutations(mutations);
		ClassReport cr = new ClassReport(className, content);

		cr.addColumn(ID);
		cr.addColumn(LINE);
		cr.addColumn(TYPE);
		cr.addColumn(DETECTED);

		for (Mutation m : mutations) {
			Long id = m.getId();
			cr.addLine(id);
			cr.putEntry(id, ID, "" + m.getId());
			cr.putEntry(id, LINE, String.format("<a href=\"#%d\">%s</a>", m
					.getLineNumber(), getLineNumberString(m)));
			cr.putEntry(id, TYPE, "" + m.getMutationType().getDesc());
			cr.putEntry(id, DETECTED, String.format("<img src=\"%s\"/>",
					getDetected(m)));

		}
		return cr;
	}

	private static void sortMutations(List<Mutation> mutations) {
		Collections.sort(mutations, new Comparator<Mutation>() {

			public int compare(Mutation o1, Mutation o2) {
				if (o1.getLineNumber() - o2.getLineNumber() != 0) {
					return o1.getLineNumber() - o2.getLineNumber();
				}
				if (o1.getMutationType().ordinal()
						- o2.getMutationType().ordinal() != 0) {
					return o1.getMutationType().ordinal()
							- o2.getMutationType().ordinal();
				}
				if (o1.getMutationForLine() - o2.getMutationForLine() != 0) {
					return o1.getMutationForLine() - o2.getMutationForLine();
				}
				return 0;
			}

		});
	}

	private static String getDetected(Mutation m) {
		return m.isKilled() ? "detected.png" : "not_detected.png";
	}

	private static String getLineNumberString(Mutation m) {
		if (m.getMutationForLine() == 0) {
			return m.getLineNumber() + "";
		}
		return m.getLineNumber() + "(" + m.getMutationForLine() + ")";
	}

	private static String getImpact(Mutation m) {
		if (m.getMutationResult() != null) {
			MutationTestResult mr = m.getMutationResult();
			return "" + mr.getDifferentViolatedInvariants();
		}
		return "not executed by tests";
	}
}
