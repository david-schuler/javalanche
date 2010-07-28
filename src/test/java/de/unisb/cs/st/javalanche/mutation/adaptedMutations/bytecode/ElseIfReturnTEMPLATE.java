package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import org.jaxen.JaxenException;
import org.jaxen.expr.DefaultFilterExpr;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FilterExpr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.PredicateSet;
import org.jaxen.expr.UnionExpr;
import org.jaxen.pattern.LocationPathPattern;
import org.jaxen.pattern.Pattern;
import org.jaxen.pattern.UnionPattern;

public class ElseIfReturnTEMPLATE {

	private static final boolean TRACE = false;

	public int m(int x) {
		if (TRACE) {
			System.out.println("TEST");
		}

		if (x == 1) {
			return 7;
		} else if (x == 1) {
			return 8;
		} else if (x == 3) {
			return 9;
		} else {
			return 10;
		}
	}
}
