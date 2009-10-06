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
package de.unisb.cs.st.javalanche.coverage;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

public class JaxenChecker {

	private static Mutation[] jaxenMutations = new Mutation[] {
			new Mutation("org.jaxen.saxpath.base.Verifier", 278, 0,
					RIC_MINUS_1, false),
			new Mutation("org.jaxen.dom.DocumentNavigator", 360, 0,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.pattern.UnionPattern", 62, 1, RIC_PLUS_1,
					false),
			new Mutation("org.jaxen.jdom.DocumentNavigator", 256, 0,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.expr.DefaultStep", 159, 0, REMOVE_CALL,
					false),
			new Mutation("org.jaxen.dom.NamespaceNode", 147, 0, REMOVE_CALL,
					false),
			new Mutation("org.jaxen.pattern.LocationPathPattern", 103, 0,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.saxpath.base.XPathLexer",/* 491 moved */482,
					0,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.expr.NodeComparator", 147, 0, RIC_PLUS_1,
					false),
			new Mutation("org.jaxen.function.ext.LocaleFunctionSupport", 98, 0,
					NEGATE_JUMP, false),
			new Mutation("org.jaxen.pattern.PatternParser", 104, 0,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.expr.DefaultLocationPath", 147, 0,
					RIC_ZERO, false),
			new Mutation("org.jaxen.saxpath.base.XPathReader", 131, 1,
					REMOVE_CALL, false),
			new Mutation("org.jaxen.xom.DocumentNavigator", 377, 0, RIC_ZERO,
					false),
			new Mutation("org.jaxen.xom.DocumentNavigator$IndexIterator", 217,
					1, RIC_ZERO, false),
			new Mutation("org.jaxen.function.NumberFunction", 210, 0,
					RIC_MINUS_1, false),
			new Mutation("org.jaxen.JaxenRuntimeException", 66, 1, RIC_MINUS_1,
					false),
			new Mutation("org.jaxen.XPathSyntaxException", 135, 0, RIC_MINUS_1,
					false),
			new Mutation("org.jaxen.Context", 101, 0, RIC_MINUS_1, false),
			new Mutation("org.jaxen.expr.DefaultMultiplyExpr", 75, 2,
					REMOVE_CALL, false) };

	public static void main(String[] args) {
		SessionFactory sessionFactory = HibernateServerUtil
				.getSessionFactory(Server.QUENTIN);
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		transaction.begin();
		for (Mutation m : jaxenMutations) {
			Mutation mutationOrNull = QueryManager
					.getMutationOrNull(m, session);
			if (mutationOrNull == null) {
				System.out.println("Mutation not found " + m);
			} else {
				System.out.println("Mutation found "
						+ mutationOrNull.toShortString());
				System.out.println(mutationOrNull.isKilled() ? "deteced"
						: "not detected");
				MutationTestResult result = mutationOrNull.getMutationResult();
				boolean touched = false;
				if (result != null && result.isTouched()) {
					touched = true;
				} 
					System.out.println(touched ? "touched" : "not touched");
			}
		}
		transaction.commit();
		session.close();
	}
}
