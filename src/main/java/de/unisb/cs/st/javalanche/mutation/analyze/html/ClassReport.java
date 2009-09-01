package de.unisb.cs.st.javalanche.mutation.analyze.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Join;

public class ClassReport {

	private List<String> columns = new ArrayList<String>();

	private Map<Long, List<String>> entries = new HashMap<Long, List<String>>();

	private List<Long> entryOrder = new ArrayList<Long>();

	private final Iterable<String> content;

	private String className;
	private static Logger logger = Logger.getLogger(ClassReport.class);

	public ClassReport(String className, Iterable<String> content) {
		super();
		this.content = content;
		this.className = className;
	}

	public void addLine(Long id) {
		entryOrder.add(id);
		entries.put(id, new ArrayList<String>());
	}

	public void putEntry(Long id, String column, String value) {
		if (!columns.contains(column)) {
			throw new IllegalArgumentException("Illegal column given " + column
					+ "\nAllowed values: " + columns);
		}
		List<String> entry = entries.get(id);
		if (entry == null) {
			throw new IllegalArgumentException("No entry for id " + id
					+ "\n Ids" + entries.keySet());
		}
		int index = columns.indexOf(column);
		while (entry.size() <= index) {
			entry.add(null);
		}
		entry.add(index, value);
	}

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append(getHead());
		sb.append(getSourceHtml());
		sb.append(getMutationsHtml());
		sb.append(getTail());
		return sb.toString();
	}

	private String getTail() {
		return "</body>\n</html>";
	}

	private String getMutationsHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"mutation_table\" summary=\"Mutations\">\n");
		sb.append("<tr>");
		for (String s : columns) {
			sb.append("<th>");
			sb.append(s);
			sb.append("</th>");
		}
		sb.append("</tr>");
		logger.debug("Columns " + columns);
		for (Long id : entryOrder) {
			sb.append("<tr>");
			List<String> lineEntries = entries.get(id);
			for (int i = 0; i < columns.size(); i++) {
				sb.append("<td>");
				sb.append(lineEntries.get(i));
				sb.append("</td>");

			}
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");
		return sb.toString();
	}

	private String getSourceHtml() {
		StringBuilder sb = new StringBuilder();
		sb
				.append("<pre class=\"prettyprint\" style=\"border: 1px solid #888;padding: 2px\">");
		int linecount = 1;
		for (String line : content) {
			sb.append(String.format(
					"<span class=\"nocode\"><a name=\"%d\">%3d: </a></span>",
					linecount, linecount));
			linecount++;
			sb.append(StringEscapeUtils.escapeHtml(line));
			sb.append("\n");
		}
		sb.append("</pre>");
		return sb.toString();
	}

	private String getHead() {
		String[] head = {
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN",
				"\"http://www.w3.org/TR/html4/strict.dtd\">",
				"<html>",
				"<head>",
				"<title>",
				className,
				"</title>",
				"<link href=\"prettify.css\" type=\"text/css\" rel=\"stylesheet\" />",
				"<script type=\"text/javascript\" src=\"prettify.js\"></script>",
				"</head>", "<body onload=\"prettyPrint()\">",
				"<h2>Javalanche report for class:<br/> " + className + " </h2>" };
		return Join.join("\n", head);
	}

	public void addColumn(String columnName) {
		logger.debug("Adding column " + columnName);
		if (!columns.contains(columnName)) {
			columns.add(columnName);
		}
	}

	public String getClassName() {
		return className;
	}
}
