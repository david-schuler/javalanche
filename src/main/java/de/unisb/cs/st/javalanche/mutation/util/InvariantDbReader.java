///*
//* Copyright (C) 2010 Saarland University
//* 
//* This file is part of Javalanche.
//* 
//* Javalanche is free software: you can redistribute it and/or modify
//* it under the terms of the GNU Lesser Public License as published by
//* the Free Software Foundation, either version 3 of the License, or
//* (at your option) any later version.
//* 
//* Javalanche is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* GNU Lesser Public License for more details.
//* 
//* You should have received a copy of the GNU Lesser Public License
//* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
//*/
//package de.unisb.cs.st.javalanche.mutation.util;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
//import de.unisb.cs.st.javalanche.invariants.invariants.checkers.InvariantChecker;
//import de.unisb.cs.st.javalanche.invariants.properties.InvariantProperties;
//import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
//import de.unisb.cs.st.javalanche.mutation.results.Invariant;
//import de.unisb.cs.st.javalanche.mutation.results.Mutation;
//import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
//import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
//
//public class InvariantDbReader {
//
//	private static Logger logger = Logger.getLogger(InvariantDbReader.class);
//
////	private static final Pattern p = Pattern.compile("(.*)\\((.*)\\)");
//
////	private static final String DEFAULT_FILENAME = "trace.out";
//
////	private static final String ADABU2_RESULTFILENAME = "adabu2.resultfilename";
//
//	public static void main(String[] args) {
//		invariantsToDB();
//		displayInvariants();
//	}
//
//	private static void invariantsToDB() {
//		Collection<InvariantChecker> allInvariants = InvariantProperties
//				.getClassInvariants().getInvariants();
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
//		int saved = 0;
//		for (InvariantChecker invariantChecker : allInvariants) {
//			Invariant in = new Invariant(invariantChecker);
//			session.save(in);
//			saved++;
//			if (saved % 20 == 0) { // 20, same as the JDBC batch size
//				// flush a batch of inserts and release memory:
//				// see
//				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
//				session.flush();
//				session.clear();
//			}
//		}
//		tx.commit();
//		session.close();
//	}
//
//	@SuppressWarnings("unchecked")
//	private static void displayInvariants() {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
//		Query query = session.createQuery("from Invariant");
//		query.setMaxResults(100);
//		List list = query.list();
//		for (Object object : list) {
//			System.out.println(object);
//
//		}
//		tx.commit();
//		session.close();
//	}
//
//	private static File getInvariantFileName(String value) {
//		logger.info("Getting invariant file for " + value);
//		File f = new File(value);
//		String name = f.getName();
//		String replace = name.replace("trace.out", "daikon-");
//		replace += ".inv.gz";
//
//		logger.info("Invariant file: " + replace);
//		return new File(f.getParentFile(), replace);
//	}
//
//	@SuppressWarnings("unchecked")
//	public void mapInvariants() {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
//		String projectPrefix = MutationProperties.PROJECT_PREFIX;
//		Query query = session.createQuery("from Mutation WHERE name LIKE '"
//				+ projectPrefix + "%'");
//		List<Mutation> list = query.list();
//		for (Mutation mutation : list) {
//			MutationTestResult mutationResult = mutation.getMutationResult();
//			if (mutationResult != null) {
//				int[] violatedInvariants = mutationResult
//						.getViolatedInvariants();
//				Query invariantQuery = session
//						.createQuery("from Invariant WHERE checkerId in (:ids)");
//				invariantQuery.setParameterList("ids", Arrays
//						.asList(violatedInvariants));
//				List<de.unisb.cs.st.javalanche.mutation.results.Invariant> invariantList = invariantQuery
//						.list();
//				for (de.unisb.cs.st.javalanche.mutation.results.Invariant invariant : invariantList) {
//					logger.info("adding invariant" + invariant);
//					mutationResult.addInvariant(invariant);
//				}
//			}
//			session.update(mutationResult);
//		}
//		tx.commit();
//		session.close();
//
//	}
//
//}
//