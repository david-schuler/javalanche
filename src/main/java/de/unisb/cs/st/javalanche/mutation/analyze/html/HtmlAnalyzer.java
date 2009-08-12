package de.unisb.cs.st.javalanche.mutation.analyze.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.google.common.base.Join;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.unisb.cs.st.ds.util.io.DirectoryFileSource;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.MutationResultAnalyzer;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class HtmlAnalyzer implements MutationAnalyzer {

	private Set<File> files;

	private static Logger logger = Logger.getLogger(HtmlAnalyzer.class);

	private static final File REPORT_DIR = new File(
			MutationProperties.OUTPUT_DIR + "/report");

	public String analyze(Iterable<Mutation> mutations) {
		Multimap<String, Mutation> map = new HashMultimap<String, Mutation>();
		for (Mutation m : mutations) {
			map.put(getClassName(m), m);
		}
		Set<String> keySet = map.keySet();
		int count = 0;
		setupFiles();
		Map<String, String> fileMap = new HashMap<String, String>();
		for (String key : keySet) {
			Iterable<String> content = getClassContent(key);
			HtmlClass htmlClass = new HtmlClass(key, content, map.get(key));
			String filename = key + ".html";
			Io.writeFile(htmlClass.getHtml(), new File(REPORT_DIR, filename));
			fileMap.put(key, filename);
			count++;
		}
		File navigation = createNavigation(fileMap);
		String mutationSummary = new MutationResultAnalyzer()
				.analyze(mutations).replace("\n", "<br/>\n");
		File summary = createSummary(mutationSummary);
		createFrameFile(navigation, summary);
		return "Created report in " + REPORT_DIR + " saved " + count
				+ " files ";
	}

	private void createFrameFile(File navigationFile, File summaryFile) {
		String[] content = {
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">",
				"<html>",
				"<head>",
				"<title>",
				"Javalanche report",
				"</title>",
				"</head>",
				"<frameset cols=\"20%,80%\">",
				"<frame src=\"report-classes.html\" name=\"list\" title=\"All Reports\">",
				"<frame src=\"summary.html\" name=\"main\" title=\"Javalanche class reports\" scrolling=\"yes\">",
				"<noframes>",
				"<h2>frame alert</h2>",
				"<p>",
				"This document is designed to be viewed using the frames feature. ",
				"If you see this message, you are using a non-frame-capable web client.",
				"<br/>",
				"Link to <a href=\"summary.html\">non-frame version.</a>",
				"</noframes>", "</frameset>", "</html>"

		};
		Io.writeFile(Join.join("\n", content), new File(REPORT_DIR,
				"index.html"));
	}

	private File createSummary(String mutationSummary) {
		StringBuilder sb = new StringBuilder(
				"<html><head><title>Javalanche Sumary</title></head><body>\n");
		sb.append("<h2> Results for project:<br/>"
				+ MutationProperties.PROJECT_PREFIX + "</h2>\n");
		sb.append(mutationSummary);
		sb.append("\n</body></html>");
		File file = new File(REPORT_DIR, "summary.html");
		Io.writeFile(sb.toString(), file);
		return file;
	}

	private File createNavigation(Map<String, String> fileMap) {
		StringBuilder sb = new StringBuilder(
				"<html><head><title>Javalanche Report</title></head><body>");
		SortedSet<String> keySet = new TreeSet<String>(fileMap.keySet());
		for (String key : keySet) {
			sb.append(String.format(
					"<a href=\"%s\" target=\"main\" >%s</a><br/>", fileMap
					.get(key), key));

		}
		sb.append("</body></html>");
		File file = new File(REPORT_DIR, "report-classes.html");
		Io.writeFile(sb.toString(), file);
		return file;
	}

	private void setupFiles() {
		REPORT_DIR.mkdirs();
		copyFile("prettify.js");
		copyFile("prettify.css");
		copyFile("detected.png");
		copyFile("not_detected.png");

	}

	private void copyFile(String name) {
		URL systemResource = ClassLoader.getSystemResource("report/" + name);
		logger.info(systemResource);
		copyFile(systemResource, new File(REPORT_DIR, name));
	}

	private void copyFile(URL src, File dest) {
		try {
			InputStream in;
			in = src.openStream();
			OutputStream out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
