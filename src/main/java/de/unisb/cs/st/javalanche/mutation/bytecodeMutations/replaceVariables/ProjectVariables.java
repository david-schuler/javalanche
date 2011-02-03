package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class ProjectVariables {

	private static final Logger logger = Logger
			.getLogger(ProjectVariables.class);

	private static final File DEFAULT_LOCATION = new File(
			MutationProperties.OUTPUT_DIR + "/variable-info.xml");

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
