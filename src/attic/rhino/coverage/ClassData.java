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
