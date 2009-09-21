/*
* Copyright (C) 2009 Saarland University
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Join;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class HtmlReport {

	private static final String SUMMARY_FILE_NAME = "summary.html";

	private static Logger logger = Logger.getLogger(HtmlReport.class);

	private static final File REPORT_DIR = new File(
			MutationProperties.OUTPUT_DIR + "/report");

	private List<String> summary = new ArrayList<String>();

	private Map<String, ClassReport> classes = new HashMap<String, ClassReport>();

	public void report() {
		setupFiles();

		int count = 0;

		Map<String, String> fileMap = new HashMap<String, String>();
		Set<Entry<String, ClassReport>> entrySet = classes.entrySet();
		for (Entry<String, ClassReport> entry : entrySet) {
			String key = entry.getKey();
			ClassReport classReport = entry.getValue();
			String filename = key + ".html";
			fileMap.put(key, filename);
			Io.writeFile(classReport.getHtml(), new File(REPORT_DIR, filename));
			count++;
		}
		File navigation = createNavigation(fileMap);
		String mutationSummary = getSummary();
		File summary = createSummary(mutationSummary);
		createFrameFile(navigation, summary);
		logger.info("Created report in " + REPORT_DIR + " saved " + count
				+ " files ");

	}

	private String getSummary() {
		StringBuilder sb = new StringBuilder();
		for (String s : summary) {
			sb.append(s);
			sb.append("\n<hr />\n");

		}
		return sb.toString();
	}

	public void add(ClassReport classReport) {
		classes.put(classReport.getClassName(), classReport);
	}

	//
	// 
	// count++;

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
		File file = new File(REPORT_DIR, SUMMARY_FILE_NAME);
		Io.writeFile(sb.toString(), file);
		return file;
	}

	private File createNavigation(Map<String, String> fileMap) {
		StringBuilder sb = new StringBuilder(
				"<html><head><title>Javalanche Report</title></head><body>");
		SortedSet<String> keySet = new TreeSet<String>(fileMap.keySet());
		sb.append(String.format(
				"<a href=\"%s\" target=\"main\" >Summary</a><br/>",
				SUMMARY_FILE_NAME));
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
		logger.debug("Copying from resource: " + systemResource);
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

	public void addSummary(String heading, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>");
		sb.append(StringEscapeUtils.escapeHtml(heading));
		sb.append("</h3>");
		sb.append("<p>");
		content = StringEscapeUtils.escapeHtml(content);
		content = content.replace("\n", "<br /> \n");
		sb.append(content);
		sb.append("</p>");
		summary.add(sb.toString());
	}

	public ClassReport getClassReport(String className) {
		return classes.get(className);
	}
}
