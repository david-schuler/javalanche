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
package de.unisb.cs.st.javalanche.mutation.tmp;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import de.unisb.cs.st.ds.util.io.SerializeIo;
import de.unisb.cs.st.javalanche.invariants.invariants.ClassInvariants;
import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;

@SuppressWarnings("serial")
public class ShowInvariantsServer {

	private static final String INVARIANT_FILENAME = "/Users/schuler/tmp/CHECKED-org.jaxen-invariant-checkers.ser";

	private static Logger logger = Logger.getLogger(ShowInvariantsServer.class);

	private static List<Mutation> top12 = new ArrayList<Mutation>() {
		{
			// add(new Mutation("org.jaxen.Context", 102, 0, RIC_MINUS_1,
			// false));
			//
			//
			// add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 615, 1,
			// RIC_ZERO, false));
			//
			// add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 615, 1,
			// RIC_MINUS_1, false));
			//
			// add(new Mutation("org.jaxen.saxpath.base.XPathReader", 1082, 0,
			// RIC_PLUS_1, false));
			// add(new Mutation("org.jaxen.pattern.LocationPathPattern", 107, 0,
			// NEGATE_JUMP, false));
			// add(new Mutation("org.jaxen.expr.DefaultLocationPath", 126, 0,
			// RIC_MINUS_1, false));
			// add(new Mutation("org.jaxen.expr.DefaultLocationPath", 126, 0,
			// RIC_PLUS_1, false));
			// add(new Mutation("org.jaxen.JaxenRuntimeException", 140, 0,
			// RIC_PLUS_1, false));
			// add(new Mutation("org.jaxen.JaxenRuntimeException", 140, 0,
			// NEGATE_JUMP, false));
			// add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 274, 0,
			// RIC_MINUS_1, false));
			// add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 274, 0,
			// RIC_ZERO, false));
			//
			// add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 649, 0,
			// ARITHMETIC_REPLACE, false));

		}
	};

	private static List<Mutation> nonEquivalents = new ArrayList<Mutation>() {
		{
			// // add(new Mutation("org.jaxen.saxpath.base.Verifier", 278, 0,
			// // RIC_MINUS_1, false));
			// // add(new Mutation("org.jaxen.expr.DefaultStep", 159, 0,
			// REMOVE_CALL,
			// // false));
			// // add(new Mutation("org.jaxen.pattern.LocationPathPattern", 103,
			// 0,
			// // REMOVE_CALL, false));
			// // add(new
			// Mutation("org.jaxen.function.ext.LocaleFunctionSupport",
			// // 98, 0, NEGATE_JUMP, false));
			// // add(new Mutation("org.jaxen.saxpath.base.XPathReader", 131, 1,
			// // REMOVE_CALL, false));
			// // add(new Mutation("org.jaxen.function.NumberFunction", 210, 0,
			// // RIC_MINUS_1, false));
			// // add(new Mutation("org.jaxen.JaxenRuntimeException", 66, 1,
			// // RIC_MINUS_1, false));
			// // add(new Mutation("org.jaxen.XPathSyntaxException", 135, 0,
			// // RIC_MINUS_1, false));
			// // add(new Mutation("org.jaxen.Context", 101, 0, RIC_MINUS_1,
			// false));
			// // add(new Mutation("org.jaxen.expr.DefaultMultiplyExpr", 75, 2,
			// // REMOVE_CALL, false));

		}
	};

	private List<Mutation> equivalents = new ArrayList<Mutation>() {
		{
			// add(new Mutation("org.jaxen.dom.DocumentNavigator", 360, 0,
			// REMOVE_CALL, false));
			// add(new Mutation("org.jaxen.pattern.UnionPattern", 62, 1,
			// RIC_PLUS_1, false));
			// add(new Mutation("org.jaxen.dom.NamespaceNode", 147, 0,
			// REMOVE_CALL, false));
			// add(new Mutation("org.jaxen.expr.NodeComparator", 147, 0,
			// RIC_PLUS_1, false));
			// add(new Mutation("org.jaxen.pattern.PatternParser", 104, 0,
			// REMOVE_CALL, false));
			// add(new Mutation("org.jaxen.expr.DefaultLocationPath", 147, 0,
			// RIC_ZERO, false));
			// add(new Mutation("org.jaxen.xom.DocumentNavigator", 377, 0,
			// RIC_ZERO, false));
			// add(new Mutation("org.jaxen.xom.DocumentNavigator$IndexIterator",
			// 217, 1, RIC_ZERO, false));

		}
	};

//	private List<Mutation> undecided = new ArrayList<Mutation>() {
//		{
//
//			add(new Mutation("org.jaxen.saxpath.base.XPathLexer", 491, 0,
//					REMOVE_CALL, false));
//			add(new Mutation("org.jaxen.jdom.DocumentNavigator", 256, 0,
//					REMOVE_CALL, false));
//
//		}
//	};

	public static void main(String[] args) {
		File f = new File(INVARIANT_FILENAME);
		System.out.println(f.exists());
		ClassInvariants classInvariants = SerializeIo.get(INVARIANT_FILENAME);
		if (classInvariants == null) {
			System.exit(0);
		}
		SessionFactory sessionFactory = HibernateServerUtil
				.getSessionFactory(Server.KUBRICK);
		System.out.println("SESSION FACTORY  " + sessionFactory);

		// SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		for (Mutation m : top12) {
			// Query query = session
			// .createQuery("FROM Mutation WHERE className=:name AND
			// lineNumber=:linenu");// WHERE
			// // message
			// // LIKE
			// // 'Mutation
			// // test
			// // thread
			// // could%'");
			// List<Mutation> list = query.list();
			// System.out.println(list.size());
			// for (Mutation mutation : list) {
			// System.out.println(mutation);
			// }
			Mutation dbMutation = QueryManager.getMutationOrNull(m, session);
			System.out.println(dbMutation.toShortString());
			int[] violatedInvariants = dbMutation.getMutationResult()
					.getViolatedInvariants();
			for (int id : violatedInvariants) {
				InvariantChecker invariantChecker = classInvariants
						.getInvariantChecker(id);
				System.out.println("\t" + invariantChecker);
			}
		}

		transaction.commit();
		session.close();
	}
}
