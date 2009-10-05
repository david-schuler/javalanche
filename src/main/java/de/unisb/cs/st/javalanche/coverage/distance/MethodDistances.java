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

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

public class MethodDistances implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final File DEFAULT_LOCATION = new File(
			MutationProperties.OUTPUT_DIR + "/method_distances.ser");
	private static Logger logger = Logger.getLogger(MethodDistances.class);
	Map<Tuple, Integer> distances = new HashMap<Tuple, Integer>();
	private Set<MethodDescription> allMethods;
	private final Hierarchy hierarchy;

	public MethodDistances(ConnectionData data, Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
		allMethods = data.getAllMethods();
		initMap(data);
		makeTransitiveConnections();
	}

	private void makeTransitiveConnections() {
		boolean changed = true;
		int i = 0;
		Map<Tuple, Integer> copy = new HashMap<Tuple, Integer>(distances);
		Set<Tuple> changedSet = new HashSet<Tuple>(distances.keySet());
		while (i < allMethods.size() && changed) {
			i++;
			changed = false;
			logger.info("Checking distance " + i);
			Set<Tuple> next = new HashSet<Tuple>();
			for (Tuple distanceTuple : copy.keySet()) {
				Set<MethodDescription> startMds = getFrom(distanceTuple
						.getEnd(), changedSet);
				for (MethodDescription methodDescription : startMds) {
					Tuple t = new Tuple(distanceTuple.getStart(),
							methodDescription);
					if (!distances.containsKey(t)) {
						distances.put(t, i + 1);
						next.add(t);
						changed = true;
					}
				}
				checkSupers();
			}
			changedSet = next;
		}
	}

	private Set<MethodDescription> getFrom(MethodDescription md,
			Set<Tuple> changedSet) {
		Set<MethodDescription> result = new HashSet<MethodDescription>();
		for (Tuple t : changedSet) {
			if (t.getStart().equals(md)) {
				result.add(t.getEnd());
			}
		}
		return result;
	}

	private Set<MethodDescription> getFrom(MethodDescription start, int count) {
		Set<Tuple> keySet = distances.keySet();
		Set<MethodDescription> result = new HashSet<MethodDescription>();
		for (Tuple tuple : keySet) {
			if (tuple.getStart().equals(start) && distances.get(tuple) == count) {
				result.add(tuple.getEnd());
			}
		}
		return result;
	}

	private Set<MethodDescription> getTo(MethodDescription end) {
		Set<Tuple> keySet = distances.keySet();
		Set<MethodDescription> result = new HashSet<MethodDescription>();
		for (Tuple tuple : keySet) {
			if (tuple.getEnd().equals(end)) {
				result.add(tuple.getStart());
			}
		}
		return result;
	}

	private void checkSupers() {
		Set<Tuple> keySet = distances.keySet();
		for (Tuple t : keySet) {
			MethodDescription start = t.getStart();
			MethodDescription second = t.getEnd();
			String className = second.getClassName();
			Set<String> allSupers = hierarchy.getAllSupers(className);

			for (String superClass : allSupers) {
				MethodDescription superDesc = second.getSuper(superClass);
				Tuple superTuple = new Tuple(start, superDesc);
				if (distances.containsKey(superTuple)) {
					Integer actualDistance = distances.get(t);
					Integer superDistance = distances.get(superTuple);
					if (actualDistance > superDistance) {
						distances.put(t, superDistance);
					}
				}
			}
		}
	}

	private void initMap(ConnectionData data) {
		Set<Tuple> connections = data.getConnections();
		for (Tuple tuple : connections) {
			if (!tuple.getStart().equals(tuple.getEnd())) {
				distances.put(tuple, 1);
			}
		}
		// for (MethodDescription m1 : allMethods) {
		// for (MethodDescription m2 : allMethods) {
		// Integer distance = Integer.MAX_VALUE;
		// if (m1.equals(m2)) {
		// distance = 0;
		// }
		// if (data.hasConnection(m1, m2)) {
		// distance = 1;
		// }
		// Tuple tuple = new Tuple(m1, m2);
		// logger.debug("Putting distance " + tuple + " " + distance);
		// distances.put(tuple, distance);
		// }
		// }
	}

	public int getDistance(MethodDescription m1, MethodDescription m2) {
		int dist = getSimpleDistance(m1, m2);
		if (dist == -1) {
			dist = search(m1, m2);
		}
		return dist;
	}

	private int search(MethodDescription m1, MethodDescription m2) {
		Set<MethodDescription> seen = new HashSet<MethodDescription>();
		Set<MethodDescription> checkNext = new HashSet<MethodDescription>();
		checkNext.add(m1);
		seen.add(m1);
		int i = 1;
		while (i < maxDist()) {
			Set<MethodDescription> to = new HashSet<MethodDescription>();
			for (MethodDescription md : checkNext) {
				Set<MethodDescription> to2 = getTo(md);
				for (MethodDescription mdX : to2) {
					if (!seen.contains(mdX)) {
						to.add(mdX);
					}
				}
			}
			for (MethodDescription md : to) {
				int simpleDistance = getSimpleDistance(md, m2);
				if (simpleDistance != -1) {
					logger.warn("Found complex route");
					return simpleDistance + i * 4;
				} else {
					if (!seen.contains(md)) {
						seen.add(md);
						checkNext.add(md);
					}
				}
			}
			i++;
		}
		return -1;
	}

	private int maxDist() {
		Collection<Integer> values = distances.values();
		Integer max = Collections.max(values);
		return max.intValue();
	}

	private int getSimpleDistance(MethodDescription m1, MethodDescription m2) {
		Integer integer = distances.get(new Tuple(m1, m2));
		if (integer != null) {
			return integer;
		}
		Set<String> allSupers = hierarchy.getAllSupers(m2.getClassName());
		for (String superName : allSupers) {
			logger.info("Checking superclass : " + superName + " for: "
					+ m2.getClassName());
			MethodDescription super1 = m2.getSuper(superName);
			Tuple superTuple = new Tuple(m1, super1);
			logger.info("Checking connection: " + superTuple);
			Integer superDistance = distances.get(superTuple);
			logger.info("Got: " + superDistance);
			if (superDistance != null) {
				return superDistance;
			}
		}
		return -1;
	}

	public Set<MethodDescription> getAll() {
		Set<MethodDescription> result = new HashSet<MethodDescription>();
		Set<Tuple> keySet = distances.keySet();
		for (Tuple tuple : keySet) {
			result.add(tuple.getStart());
			result.add(tuple.getEnd());
		}
		return result;
	}

	public MethodDescription getMetodDesc(String fullMethodName) {
		String className = fullMethodName.substring(0, fullMethodName
				.indexOf('@'));
		String methodName = fullMethodName.substring(fullMethodName
				.indexOf('@') + 1);

		for (MethodDescription md : allMethods) {
			if (className.equals(md.getClassName())) {
				if (methodName.equals(md.getMethodName())) {
					return md;
				}
			}

		}
		logger.warn("Not Found " + className + "   " + methodName + " "
				+ fullMethodName);
		return null;
	}

	public static void makeDistances() {
		ConnectionData data = ConnectionData.read();
		Hierarchy hierarchy = Hierarchy.readFromDefaultLocation();
		MethodDistances md = new MethodDistances(data, hierarchy);
		SerializeIo.serializeToFile(md, DEFAULT_LOCATION);
	}

	public static MethodDistances getFromDefault() {
		return SerializeIo.get(DEFAULT_LOCATION);
	}

	public static void main(String[] args) {
		makeDistances();
	}
}
