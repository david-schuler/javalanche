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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;

public class ProjectVariables {

	private static final Logger logger = Logger
			.getLogger(ProjectVariables.class);

	private static final File DEFAULT_LOCATION = new File(ConfigurationLocator
			.getJavalancheConfiguration().getOutputDir(), "/variable-info.xml");

	private static ProjectVariables fromDefault;

	private Map<String, List<VariableInfo>> classVariables = new HashMap<String, List<VariableInfo>>();

	private Map<String, List<VariableInfo>> staticVariables = new HashMap<String, List<VariableInfo>>();

	public List<VariableInfo> getClassVariables(String className) {
		return classVariables.get(className);
	}

	public List<VariableInfo> getStaticVariables(String className) {
		// logger.info("Getting variables for " + className);
		return staticVariables.get(className);
	}

	public void addClassVariables(String className, List<VariableInfo> vInfo) {
		classVariables.put(className, vInfo);
	}

	public void addStaticVariables(String className, List<VariableInfo> vInfo) {
		// logger.info("Adding variables for class " + className);
		staticVariables.put(className, vInfo);
	}

	public void write() {
		XmlIo.toXML(this, DEFAULT_LOCATION);
	}

	public static ProjectVariables read() {
		// if (fromDefault == null) {
		fromDefault = (ProjectVariables) XmlIo.fromXml(DEFAULT_LOCATION);
		// }
		return fromDefault;
	}

}
