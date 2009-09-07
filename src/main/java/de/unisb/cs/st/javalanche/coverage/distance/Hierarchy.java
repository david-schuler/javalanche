package de.unisb.cs.st.javalanche.coverage.distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer.ClassEntry;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class Hierarchy {

	Map<String, ClassNode> hierarchyMap = read();

	public Hierarchy() {

	}

	public Set<String> getAllSupers(String className) {
		ClassNode classNode = hierarchyMap.get(className);
		return classNode.getAllSupers();
	}

	private static Map<String, ClassNode> read() {
		Map<String, ClassNode> result = new HashMap<String, ClassNode>();
		Set<ClassEntry> entries = XmlIo
				.get(MutationProperties.INHERITANCE_DATA_FILE);
		for (ClassEntry classEntry : entries) {
			ClassNode cn = result.get(classEntry.getName());
			if (cn == null) {
				cn = new ClassNode(classEntry.getName());
				result.put(classEntry.getName(), cn);
			}
			List<ClassNode> supers = new ArrayList<ClassNode>();
			for (String s : classEntry.getSupers()) {
				ClassNode classNode = result.get(s);
				if (classNode == null) {
					classNode = new ClassNode(s);
					result.put(s, classNode);
				}
				supers.add(classNode);
			}
			cn.setSupers(supers);
		}
		return result;
	}
}
