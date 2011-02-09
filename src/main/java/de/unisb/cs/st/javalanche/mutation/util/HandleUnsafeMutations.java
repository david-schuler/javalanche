///*
// * Copyright (C) 2011 Saarland University
// * 
// * This file is part of Javalanche.
// * 
// * Javalanche is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * Javalanche is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser Public License for more details.
// * 
// * You should have received a copy of the GNU Lesser Public License
// * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
// */
//package de.unisb.cs.st.javalanche.mutation.util;
//
//import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.classic.Session;
//
//import de.unisb.cs.st.javalanche.mutation.results.Mutation;
//import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
//import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
//import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;
//
///**
// * Class handles unsafe mutations, which are mutations that cause the JVM to
// * crash (e.g. by using {@link sun.misc.Unsafe}. For these mutations a default
// * result is generated such that they are not executed anymore.
// * 
// * @author David Schuler
// * 
// */
//public class HandleUnsafeMutations {
//
//	@SuppressWarnings("serial")
//	private static final List<Mutation> unsafes = new ArrayList<Mutation>() {
//		{
//			add(new Mutation(
//					"com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider",
//					111, 0, REMOVE_CALL, false));
//			add(new Mutation(
//					"com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider",
//					109, 0, REMOVE_CALL, false));
//			add(new Mutation(
//					"com.thoughtworks.xstream.converters.reflection.SerializationMethodInvoker",
//					118, 0, REMOVE_CALL, false));
//			add(new Mutation(
//					"com.thoughtworks.xstream.converters.reflection.SerializationMethodInvoker",
//					125, 0, NEGATE_JUMP, false));
//
//		}
//
//	};
//
//	static {
//		int size = unsafes.size();
//		for (int i = 0; i < size; i++) {
//			Mutation mutation = unsafes.get(i);
//			Mutation xMutation = prependX(mutation);
//			unsafes.add(xMutation);
//		}
//	}
//
//	private static Logger logger = Logger
//			.getLogger(HandleUnsafeMutations.class);
//
//	public static void main(String[] args) {
//		handleUnsafeMutations(HibernateServerUtil
//				.getSessionFactory(Server.KUBRICK));
//	}
//
//	private static Mutation prependX(Mutation m) {
//		return new Mutation("x" + m.getClassName(), m.getLineNumber(), m
//				.getMutationForLine(), m.getMutationType(), m.isClassInit());
//	}
//
//	public static void handleUnsafeMutations(SessionFactory sessionFactory) {
//		Session s = sessionFactory.openSession();
//		Transaction tx = s.beginTransaction();
//		for (Mutation m : unsafes) {
//			Mutation dbMutation = QueryManager.getMutationOrNull(m, s);
//			if (dbMutation != null) {
//				// if (dbMutation.getMutationResult() == null) {
//				logger.info("Setting default result for mutation "
//						+ dbMutation.getId());
//				MutationTestResult defaultResult = new MutationTestResult();
//				dbMutation.setMutationResult(defaultResult);
//				// }
//				System.out.println(dbMutation);
//			}
//		}
//		tx.commit();
//		s.close();
//	}
//}
//
