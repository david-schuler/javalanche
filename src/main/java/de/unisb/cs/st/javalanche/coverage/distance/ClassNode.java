package de.unisb.cs.st.javalanche.coverage.distance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassNode {

	private final String name;

	private List<ClassNode> supers;

	public ClassNode(String name) {
		this.name = name;
	}

	public Set<String> getAllSupers() {
		Set<String> result = new HashSet<String>();
		if (supers != null) {
			for (ClassNode cn : supers) {
				result.add(cn.getName());
				result.addAll(cn.getAllSupers());
			}
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public List<ClassNode> getSupers() {
		return supers;
	}

	public void setSupers(List<ClassNode> supers) {
		this.supers = supers;
	}
}
