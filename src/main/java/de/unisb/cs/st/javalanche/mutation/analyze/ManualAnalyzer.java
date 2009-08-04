package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

@SuppressWarnings("serial")
public class ManualAnalyzer implements MutationAnalyzer {

	private static Logger logger = Logger.getLogger(ManualAnalyzer.class);

	private List<Mutation> nonEquivalents = new ArrayList<Mutation>() {
		{
			add(new Mutation("org.jaxen.saxpath.base.Verifier", 278, 0,
					RIC_MINUS_1, false));
			add(new Mutation("org.jaxen.expr.DefaultStep", 159, 0, REMOVE_CALL,
					false));
			add(new Mutation("org.jaxen.pattern.LocationPathPattern", 103, 0,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.function.ext.LocaleFunctionSupport",
					98, 0, NEGATE_JUMP, false));
			add(new Mutation("org.jaxen.saxpath.base.XPathReader", 131, 1,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.function.NumberFunction", 210, 0,
					RIC_MINUS_1, false));
			add(new Mutation("org.jaxen.JaxenRuntimeException", 66, 1,
					RIC_MINUS_1, false));
			add(new Mutation("org.jaxen.XPathSyntaxException", 135, 0,
					RIC_MINUS_1, false));
			add(new Mutation("org.jaxen.Context", 101, 0, RIC_MINUS_1, false));
			add(new Mutation("org.jaxen.expr.DefaultMultiplyExpr", 75, 2,
					REMOVE_CALL, false));

		}
	};
	private List<Mutation> equivalents = new ArrayList<Mutation>() {
		{
			add(new Mutation("org.jaxen.dom.DocumentNavigator", 360, 0,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.pattern.UnionPattern", 62, 1,
					RIC_PLUS_1, false));
			add(new Mutation("org.jaxen.dom.NamespaceNode", 147, 0,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.expr.NodeComparator", 147, 0,
					RIC_PLUS_1, false));
			add(new Mutation("org.jaxen.pattern.PatternParser", 104, 0,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.expr.DefaultLocationPath", 147, 0,
					RIC_ZERO, false));
			add(new Mutation("org.jaxen.xom.DocumentNavigator", 377, 0,
					RIC_ZERO, false));
			add(new Mutation("org.jaxen.xom.DocumentNavigator$IndexIterator",
					217, 1, RIC_ZERO, false));

		}
	};

	private List<Mutation> undecided = new ArrayList<Mutation>() {
		{

			add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 491, 0,
					REMOVE_CALL, false));
			add(new Mutation("org.jaxen.jdom.DocumentNavigator", 256, 0,
					REMOVE_CALL, false));

		}
	};

	public String analyze(Iterable<Mutation> mutations) {
		StringBuilder sb = new StringBuilder();
		List<Long> equivalentIds = getIds(equivalents);
		List<Long> nonEquivalentIds = getIds(nonEquivalents);
		List<Long> undecidedIds = getIds(undecided);
		List<Mutation> equivalentViolating = new ArrayList<Mutation>();
		List<Mutation> equivalentNonViolating = new ArrayList<Mutation>();
		List<Mutation> nonEquivalentViolating = new ArrayList<Mutation>();
		List<Mutation> nonEquivalentNonViolating = new ArrayList<Mutation>();
		List<Mutation> undecidedViolating = new ArrayList<Mutation>();
		List<Mutation> undecidedNonViolating = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			Long id = mutation.getId();
			if (equivalentIds.contains(id)) {
				if (mutation.getMutationResult() != null) {
					if (mutation.getMutationResult()
							.getDifferentViolatedInvariants() > 0) {
						equivalentViolating.add(mutation);
					} else {
						equivalentNonViolating.add(mutation);
					}
				} else {
					logger.warn("No result for mutation" + mutation);
				}
			}

			if (nonEquivalentIds.contains(id)) {
				if (mutation.getMutationResult()
						.getDifferentViolatedInvariants() > 0) {
					nonEquivalentViolating.add(mutation);
				} else {
					nonEquivalentNonViolating.add(mutation);
				}
			}

			if (undecidedIds.contains(id)) {
				if (mutation.getMutationResult()
						.getDifferentViolatedInvariants() > 0) {
					undecidedViolating.add(mutation);
				} else {
					undecidedNonViolating.add(mutation);
				}
			}

		}
		sb.append("Equivalent violating: " + equivalentViolating.size());
		sb.append("\n");
		sb.append("Equivalent non violating: " + equivalentNonViolating.size());
		sb.append("\n");
		sb.append("Non equivalent violating: " + nonEquivalentViolating.size());
		sb.append("\n");
		sb.append("Non equivalent non violating: "
				+ nonEquivalentNonViolating.size());
		sb.append("\n");
		sb.append("Undecided violating: " + undecidedViolating.size());
		sb.append("\n");
		sb.append("Undecided non violating: " + undecidedNonViolating.size());
		sb.append("\n");

		return sb.toString();
	}

	private List<Long> getIds(List<Mutation> mutations) {
		List<Long> ids = new ArrayList<Long>();
		for (Mutation mutation : mutations) {
			Mutation mutationFromDB = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDB != null) {
				ids.add(mutationFromDB.getId());
			} else {
				logger.info("Mutation not found " + mutation);
			}
		}
		return ids;
	}

}
