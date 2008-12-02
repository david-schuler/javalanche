package de.unisb.cs.st.javalanche.rhino.coverage;

import java.util.List;

public class ClassData {

	String className;

	List<Integer> coveredLines;

	public ClassData(String className, List<Integer> coveredLines) {
		super();
		this.className = className;
		this.coveredLines = coveredLines;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the coveredLines
	 */
	public List<Integer> getCoveredLines() {
		return coveredLines;
	}

	public int getNumberOfCoveredLines() {
		return coveredLines.size();
	}
}
