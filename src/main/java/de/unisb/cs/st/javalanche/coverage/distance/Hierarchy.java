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
package de.unisb.cs.st.javalanche.coverage.distance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer.ClassEntry;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class Hierarchy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(Hierarchy.class);

	private Map<String, ClassNode> hierarchyMap;

	private Set<String> notFound = new HashSet<String>();

	private Hierarchy(Map<String, ClassNode> hierarchyMap) {
		this.hierarchyMap = hierarchyMap;
	}

	public Set<String> getAllSupers(String className) {
		ClassNode classNode = hierarchyMap.get(className);
		if (classNode == null) {
			if (!notFound.contains(className)) {
				logger.warn("Did not find class node " + className);
				notFound.add(className);
			}
			return Collections.emptySet();
		}
		return classNode.getAllSupers();
	}

	private static Map<String, ClassNode> read(String fileName) {
		Map<String, ClassNode> result = new HashMap<String, ClassNode>();
		Set<ClassEntry> entries = XmlIo.get(fileName);
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

	public static Hierarchy readFromDefaultLocation() {
		Map<String, ClassNode> map = read(MutationProperties.INHERITANCE_DATA_FILE);
		return new Hierarchy(map);
	}

}
