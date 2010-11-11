package de.unisb.cs.st.javalanche.coverage.distance;

import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class DistanceGraph {

	private static Logger logger = Logger.getLogger(DistanceGraph.class);
	// private ConnectionData data;
	private final Hierarchy hierarchy;
	private UndirectedGraph<MethodDescription, DefaultEdge> g;
	private Set<MethodDescription> allMethods;

	public DistanceGraph(ConnectionData data, Hierarchy hierarchy) {
		// this.data = data;
		this.hierarchy = hierarchy;
		allMethods = data.getAllMethods();
		g = new SimpleGraph<MethodDescription, DefaultEdge>(DefaultEdge.class);
		Set<Tuple> connections = data.getConnections();
		for (Tuple tuple : connections) {
			MethodDescription start = tuple.getStart();
			MethodDescription end = tuple.getEnd();
			if (!start.equals(end)) {
				if (!g.containsVertex(start)) {
					g.addVertex(start);
				}
				if (!g.containsVertex(end)) {
					g.addVertex(end);
				}
				g.addEdge(start, end);
			}
		}
	}

	public int getDistance(MethodDescription start, MethodDescription end) {
		if (!(g.containsVertex(start) && g.containsVertex(end))) {
			return -1;
		}
		DijkstraShortestPath<MethodDescription, DefaultEdge> sp = new DijkstraShortestPath<MethodDescription, DefaultEdge>(
				g, start, end);
		double pathLength = sp.getPathLength();
		System.out.println("DistanceGraph.getDistance() "
				+ sp.getPathEdgeList());
		if (pathLength == Double.POSITIVE_INFINITY) {
			pathLength = 0;
			Set<String> allSupers = hierarchy.getAllSupers(end.getClassName());
			for (String sup : allSupers) {
				MethodDescription super1 = end.getSuper(sup);
				int distance = getDistance(start, super1);
				if (distance > 0) {
					return distance;
				}
			}
		}
		if (pathLength < 0.) {
			pathLength = 0;
		}
		return (int) pathLength;
	}

	public static DistanceGraph getDefault() {
		ConnectionData data = ConnectionData.read();
		Hierarchy hierarchy = Hierarchy.readFromDefaultLocation();
		return new DistanceGraph(data, hierarchy);
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

}
