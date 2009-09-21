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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class MethodDistances {
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
		Set<Tuple> keySet = distances.keySet();
		boolean changed = true;
		int count = 1;
		while (changed) {
			logger.info(" Round " + count++);

			changed = false;
			for (Tuple tuple : keySet) {
				MethodDescription start = tuple.getStart();
				MethodDescription end = tuple.getEnd();
				int distance = getDistance(start, end);
				for (MethodDescription md : allMethods) {
					int distance2 = getDistance(md, end);
					if (distance2 != Integer.MAX_VALUE && distance2 < distance) {
						int distance3 = getDistance(start, md);
						if (distance3 != Integer.MAX_VALUE) {
							int shorterDistance = distance2 + distance3;
							if (shorterDistance < distance) {
								logger.info("Found shorter " + start + " - "
										+ md + " - " + end + "instead of "
										+ tuple + " was " + distance + " now "
										+ shorterDistance);
								distances.put(tuple, shorterDistance);
								distance = shorterDistance;
								changed = true;
							}
						}
					}
				}
			}
			checkSupers();
		}
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
		for (MethodDescription m1 : allMethods) {
			for (MethodDescription m2 : allMethods) {
				Integer distance = Integer.MAX_VALUE;
				if (m1.equals(m2)) {
					distance = 0;
				}
				if (data.hasConnection(m1, m2)) {
					distance = 1;
				}
				Tuple tuple = new Tuple(m1, m2);
				logger.info("Putting distance " + tuple + " " + distance);
				distances.put(tuple, distance);
			}
		}
	}

	public int getDistance(MethodDescription m1, MethodDescription m2) {
		Integer integer = distances.get(new Tuple(m1, m2));
		if (integer != null) {
			return integer;
		}
		Set<String> allSupers = hierarchy.getAllSupers(m2.getClassName());
		for (String superName : allSupers) {
			logger.info("Checking: " + superName);
			MethodDescription super1 = m2.getSuper(superName);
			Tuple superTuple = new Tuple(m1, super1);
			logger.info("Checking connection" + superTuple);
			Integer superDistance = distances.get(superTuple);
			logger.info("Got " + superDistance);
			if (superDistance != null) {
				return superDistance;
			}
		}
		return Integer.MAX_VALUE;
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
}
