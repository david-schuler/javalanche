package de.unisb.cs.st.javalanche.mutation.util;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.ReplaceMap;
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

	private static final int[] integerOpcodes = new int[] { IADD, ISUB, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR };

	private static final int[] longOpcodes = new int[] { LADD, LSUB, LMUL, LDIV, LREM, LAND, LOR, LXOR };

	private static final int[] longShiftOpcodes = new int[] { LSHL, LSHR, LUSHR };
	
	private static final int[] floatOpcodes = new int[] { FADD, FSUB, FMUL, FDIV, FREM };

	private static final int[] doubleOpcodes = new int[] { DADD, DSUB, DMUL, DDIV, DREM };

	private List<Mutation> insertAdditionalMutations(Mutation m) {
		List<Mutation> result = new ArrayList<Mutation>();
		String operatorAddInfo = m.getOperatorAddInfo();
		int operator = Integer.parseInt(operatorAddInfo);
		int[] replaceOperators = getReplaceOperators(operator);
		for (int op : replaceOperators) {
			Mutation m2 = new Mutation(m.getClassName(), m.getMethodName(),
					m.getLineNumber(), m.getMutationForLine(),
					m.getMutationType());
			m2.setOperatorAddInfo(op + "");
			QueryManager.saveMutation(m2);
			result.add(m2);
		}
		return result;
	}



	public static void addReplaceConstantMutations() {
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

	public static void addArithmeticReplaceMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = MutationProperties.PROJECT_PREFIX;
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.ARITHMETIC_REPLACE);
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId())
					&& m.getBaseMutationId() == null) {
				System.out.println(m);
				String addInfo = m.getAddInfo();
				int originalValue = getOriginalValue(addInfo);
				System.out.println(originalValue);
				Set<Integer> values = getOpcodeReplacements(originalValue);
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

	private static Set<Integer> getOpcodeReplacements(int operator) {
		Set<Integer> result = new HashSet<Integer>();
		int[] replaceOperators = getReplaceOperators(operator);
		for (int i : replaceOperators) {
			result.add(i);
		}
		result.remove(operator);
		Integer alreadyUsedReplacement = ReplaceMap.getReplaceMap().get(
				operator);
		result.remove(alreadyUsedReplacement);
		return result;
	}

	private static int[] getReplaceOperators(int operator) {
		if (ArrayUtils.contains(integerOpcodes, operator)) {
			return integerOpcodes;
		}
		if (ArrayUtils.contains(longOpcodes, operator)) {
			return longOpcodes;
		}
		if (ArrayUtils.contains(longShiftOpcodes, operator)) {
			return longShiftOpcodes;
		}

		if (ArrayUtils.contains(floatOpcodes, operator)) {
			return floatOpcodes;
		}
		if (ArrayUtils.contains(doubleOpcodes, operator)) {
			return doubleOpcodes;
		}
		return new int[0];
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
		addReplaceConstantMutations();
		addArithmeticReplaceMutations();
	}
}
