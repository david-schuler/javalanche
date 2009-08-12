package de.unisb.cs.st.javalanche.mutation.analyze.html;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class HtmlAnalyzer {

	private static Logger logger = Logger.getLogger(HtmlAnalyzer.class);

	private Set<File> files;

	public HtmlReport analyze(Iterable<Mutation> mutations) {
		HtmlReport report = new HtmlReport();
		Multimap<String, Mutation> map = new HashMultimap<String, Mutation>();
		for (Mutation m : mutations) {
			map.put(getClassName(m), m);
		}
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			Iterable<String> content = getClassContent(key);
			ClassReport classReport = ClassReportFactory.getClassReport(key,
					content, new ArrayList<Mutation>(map.get(key)));
			report.add(classReport);
		}
		return report;
	}

	private Iterable<String> getClassContent(String className) {
		if (files == null) {
			initFiles();
		}
		String s = className.substring(className.lastIndexOf('.') + 1);
		s = getClassName(s);
		for (File f : files) {
			String name = f.getAbsolutePath() + "." + f.getName();
			name = name.replace('/', '.');
			if (name.contains(className)) {
				List<String> linesFromFile = Io.getLinesFromFile(f);
				logger.debug("Got file for " + className + "  -  " + f);
				return linesFromFile;
			}
		}
		return Arrays.asList("No source found for " + className);
	}

	private void initFiles() {
		try {
			Collection<File> javaFiles = DirectoryFileSource
					.getFilesByExtension(new File("."), "java");
			files = new HashSet<File>(javaFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getClassName(Mutation m) {
		String name = m.getClassName();
		return getClassName(name);
	}

	private String getClassName(String name) {
		if (name.contains("$")) {
			name = name.substring(0, name.indexOf('$'));
		}
		return name;
	}
}
