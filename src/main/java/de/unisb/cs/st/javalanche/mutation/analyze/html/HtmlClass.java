package de.unisb.cs.st.javalanche.mutation.analyze.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Join;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class HtmlClass {

	private final Iterable<String> content;
	private final String className;
	private final List<Mutation> mutations;

	public HtmlClass(String className, Iterable<String> content,
			Collection<Mutation> collection) {
		this.className = className;
		this.content = content;
		this.mutations = new ArrayList<Mutation>(collection);
		sortMutations();
	}

	private void sortMutations() {
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

	private Object getMutationsHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"mutation_table\" summary=\"Mutations\">");
		sb
				.append("<tr><th>ID</th><th>Line</th><th>Type</th><th>Detected</th><th>Impact</th></tr>\n");
		for (Mutation m : mutations) {
			sb.append("<tr><td>");
			String s = String
					.format(
							"\t<tr><td>%d</td><td><a href=\"#%d\">%s</a></td><td>%s</td><td> <img src=\"%s\"/></td><td>%s</td></tr>\n",
							m.getId(), m.getLineNumber(),
							getLineNumberString(m), m.getMutationType()
									.getDesc(), getDetected(m), getImpact(m));
			sb.append(s);
		}
		sb.append("</table>\n");

		return sb.toString();
	}

	private String getDetected(Mutation m) {
		return m.isKilled() ? "detected.png" : "not_detected.png";
	}

	private Object getLineNumberString(Mutation m) {
		if (m.getMutationForLine() == 0) {
			return m.getLineNumber() + "";
		}
		return m.getLineNumber() + "(" + m.getMutationForLine() + ")";
	}

	private String getImpact(Mutation m) {
		if (m.getMutationResult() != null) {
			MutationTestResult mr = m.getMutationResult();
			return "" + mr.getDifferentViolatedInvariants();
		}
		return "not executed by tests";
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
}
