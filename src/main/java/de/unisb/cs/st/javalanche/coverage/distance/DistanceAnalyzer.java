package de.unisb.cs.st.javalanche.coverage.distance;

import java.util.Set;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.analyze.MutationAnalyzer;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

public class DistanceAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(DistanceAnalyzer.class);

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

		ConnectionData data = ConnectionData.read();
		Hierarchy hierarchy = new Hierarchy();
		MethodDistances md = new MethodDistances(data, hierarchy);
		Set<MethodDescription> all = md.getAll();
		StringBuilder sb = new StringBuilder();
		// for (MethodDescription start : all) {
		// for (MethodDescription end : all) {
		// int distance = md.getDistance(start, end);
		// if (distance != Integer.MAX_VALUE) {
		// String s = String.format("Distance %s - %s : %d ", start
		// .toString(), end.toString(), distance);
		// sb.append(s);
		// sb.append('\n');
		// logger.info(s);
		// }
		// }
		// }
		// AbstractCaller m1 -ConcreteClass m1
		MethodDescription m1 = new MethodDescription(
				"de/data/trace/test/AbstractCaller", "m1", "()V");
		MethodDescription m2 = new MethodDescription(
				"de/data/trace/test/ConcreteClass", "m1", "()I");
		int distance = md.getDistance(m1, m2);
		String s = String.format("Check distance: %d \n", distance);
		sb.append(s);
		logger.info(s);
		return sb.toString();
	}

}
