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
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

// -Djavalanche.mutation.analyzers=de.unisb.cs.st.javalanche.mutation.analyze.RandomUndetectedAnalyzer 
public class RandomUndetectedAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger
			.getLogger(RandomUndetectedAnalyzer.class);

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		List<Mutation> undetected = new ArrayList<Mutation>();
		Set<String> allClasses = new HashSet<String>();
		for (Mutation mutation : mutations) {
			if (mutation.getMutationResult() != null) {
				if (!mutation.isKilled()) {
					undetected.add(mutation);
					String clazz = getContainingClass(mutation.getClassName());
					allClasses.add(clazz);
				}
			}
		}
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		List<String> classes = new ArrayList<String>();
		int i = 1;
		StringBuilder sbcsv = new StringBuilder();
		List<Mutation> second = new ArrayList<Mutation>();
		while (i <= 50 && undetected.size() > 0) {
			int index = r.nextInt(undetected.size());
			Mutation mutation = undetected.remove(index);

			String clazz = getContainingClass(mutation.getClassName());
			if (!classes.contains(clazz)) {
				classes.add(clazz);
				sb.append(String.format("%2d. ", i));
				sb.append(mutation);
				sbcsv.append(mutation.getCsvString());
				sbcsv.append('\n');
				for (int j = 0; j < 80; j++) {
					sb.append('-');
				}
				i++;
			} else {
				second.add(mutation);
			}
			if (classes.size() == allClasses.size() || undetected.size() == 0) {
				logger.info("Got mutation from every class - next round");
				classes.clear();
				undetected.addAll(second);
				second.clear();
			}
		}
		Io.writeFile(sbcsv.toString(), new File("random-undetected.csv"));
		Io.writeFile(sb.toString(), new File("random-undetected.txt"));
		return sb.toString();
	}

	private String getContainingClass(String className) {
		if (className.contains("$")) {
			int i = className.indexOf('$');
			return className.substring(0, i);
		}
		return className;
	}

}
