package de.unisb.cs.st.javalanche.mutation.util;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.HibernateServerUtil.Server;

/**
 * Class handles unsafe mutations, which are mutations that cause the JVM to
 * crash (e.g. by using {@link  sun.misc.Unsafe}. For these mutations a default
 * result is generated such that they are not executed anymore.
 *
 * @author David Schuler
 *
 */
public class HandleUnsafeMutations {

	@SuppressWarnings("serial")
	private static final List<Mutation> unsafes = new ArrayList<Mutation>() {
		{

			add(new Mutation(
					"xcom.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider ",
					111, 0, REMOVE_CALL, false));
			add(new Mutation(
					"xcom.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider ",
					109, 0, REMOVE_CALL, false));

			add(new Mutation(
					"com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider ",
					111, 0, REMOVE_CALL, false));
			add(new Mutation(
					"com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider ",
					109, 0, REMOVE_CALL, false));
//			add(new Mutation(
//					"xcom.thoughtworks.xstream.mapper.PackageAliasingMapper",
//					67, 0, ARITHMETIC_REPLACE, false));

		}

	};

	private static Logger logger = Logger
			.getLogger(HandleUnsafeMutations.class);

	public static void main(String[] args) {
		handleUnsafeMutations(HibernateServerUtil
				.getSessionFactory(Server.KUBRICK));
	}

	public static void handleUnsafeMutations(SessionFactory sessionFactory) {
		Session s = sessionFactory.openSession();
		Transaction tx = s.beginTransaction();
		for (Mutation m : unsafes) {
			Mutation dbMutation = QueryManager.getMutationOrNull(m, s, tx);
			if (dbMutation != null) {
//				if (dbMutation.getMutationResult() == null) {
					logger.info("Setting default result for mutation "
							+ dbMutation.getId());
					MutationTestResult defaultResult = new MutationTestResult();
					dbMutation.setMutationResult(defaultResult);
//				}
				System.out.println(dbMutation);
			}
		}
		tx.commit();
		s.close();
	}
}
