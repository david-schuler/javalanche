package de.unisb.cs.st.javalanche.mutation.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class AddMutations {

	private static final Logger logger = Logger.getLogger(AddMutations.class);
	private static Random r = new Random();
	private static SessionFactory sessionFactory = HibernateUtil
			.getSessionFactory();

	public static void addMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = MutationProperties.PROJECT_PREFIX;
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.REPLACE_CONSTANT);
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId())
					&& m.getBaseMutationId() == null) {
				System.out.println(m);
				String addInfo = m.getAddInfo();
				int originalValue = getOriginalValue(addInfo);
				System.out.println(originalValue);
				Set<Integer> values = getRandomValues(originalValue, 10);
				for (Integer val : values) {
					Mutation m2 = new Mutation(m.getClassName(),
							m.getMethodName(), m.getLineNumber(),
							m.getMutationForLine(), m.getMutationType());
					m2.setOperatorAddInfo(val + "");
					m2.setAddInfo("Replace " + originalValue + " with " + val);
					m2.setBaseMutationId(m.getId());
					System.out
							.println("AddMutations.addMutations() - Adding mutation"
									+ m2);
					logger.info("Adding mutation" + m2);
					QueryManager.saveMutation(m2);
					MutationCoverageFile.addDerivedMutation(m.getId(),
							m2.getId());
				}
			}

		}
		tx.commit();
		session.close();
		MutationCoverageFile.update();
	}

	private static Set<Integer> getRandomValues(int originalValue, int i) {
		Set<Integer> result = new HashSet<Integer>();
		while (result.size() < i) {
			int nextInt = r.nextInt();
			if (nextInt != originalValue && nextInt != 0
					&& nextInt != originalValue + 1
					&& nextInt != originalValue - 1) {
				result.add(nextInt);
			}
		}
		return result;
	}

	private static int getOriginalValue(String addInfo) {
		String substring = addInfo.substring("Replace ".length());
		int index = substring.indexOf(' ');
		String s = substring.substring(0, index);
		return Integer.parseInt(s);
	}

	public static void main(String[] args) {
		System.out.println("AA");
		addMutations();
	}
}
