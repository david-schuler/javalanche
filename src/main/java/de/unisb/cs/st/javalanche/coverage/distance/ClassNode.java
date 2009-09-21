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
