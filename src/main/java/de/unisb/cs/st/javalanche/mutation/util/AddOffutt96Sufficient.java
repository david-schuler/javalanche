/*
 * Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.util;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
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
import org.objectweb.asm.Opcodes;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.AbstractArithmeticMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.ReplaceMap;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.AbstractNegateJumpsAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.JumpReplacements;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.PossibilitiesRicMethodAdapter;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.NegateJumpsMethodAdapter.*;
import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.AbstractArithmeticMethodAdapter.*;

/**
 * 
 * @author David Schuler
 * 
 */
public class AddOffutt96Sufficient {

	private static final Logger logger = Logger
			.getLogger(AddOffutt96Sufficient.class);

	private static Random r = new Random();

	private static SessionFactory sessionFactory = HibernateUtil
			.getSessionFactory();

	private static final int[] aorIntOpcodes = new int[] { IADD, ISUB, IMUL,
			IDIV, IREM, REMOVE_LEFT_VALUE_SINGLE, REMOVE_RIGHT_VALUE_SINGLE };
	// ISHL, ISHR, IUSHR, IAND, IOR, IXOR };

	private static final int[] aorLongOpcodes = new int[] { LADD, LSUB, LMUL,
			LDIV, LREM, REMOVE_RIGHT_VALUE_DOUBLE, REMOVE_LEFT_VALUE_DOUBLE };
	// , LAND LOR, LXOR };

	private static final int[] aorFloatOpcodes = new int[] { FADD, FSUB, FMUL,
			FDIV, FREM, REMOVE_LEFT_VALUE_SINGLE, REMOVE_RIGHT_VALUE_SINGLE };

	private static final int[] aorDoubleOpcodes = new int[] { DADD, DSUB, DMUL,
			DDIV, DREM, REMOVE_RIGHT_VALUE_DOUBLE, REMOVE_LEFT_VALUE_DOUBLE };
	private static final int[] longShiftOpcodes = new int[] { LSHL, LSHR, LUSHR };

	public static final int[] rorSingleOpcodes = new int[] { IFEQ, IFNE, IFLT,
			IFGT, IFLE, IFGE, POP_ONCE_TRUE, POP_ONCE_FALSE };

	public static final int[] rorObjectNullOpcodes = new int[] { IFNULL,
			IFNONNULL, POP_ONCE_TRUE, POP_ONCE_FALSE }; // Not needed as this
														// replacement
														// corresponds to the
	// Javalanche standard replacement

	public static final int[] rorObjectCompareOpcodes = new int[] { IF_ACMPEQ,
			IF_ACMPNE }; // Not needed (see above)

	public static final int[] rorIntegerCompareOpcodes = new int[] { IF_ICMPEQ,
			IF_ICMPNE, IF_ICMPLT, IF_ICMPGT, IF_ICMPLE, IF_ICMPGE,
			POP_TWICE_TRUE, POP_TWICE_FALSE };

	public static final int[] lorIntegerOpcodes = new int[] { IAND, IOR, IXOR,
			REMOVE_LEFT_VALUE_SINGLE, REMOVE_RIGHT_VALUE_SINGLE };
	public static final int[] lorLongOpcodes = new int[] { LOR, LAND, LXOR,
			REMOVE_RIGHT_VALUE_DOUBLE, REMOVE_LEFT_VALUE_DOUBLE };

	public static final List<String> shiftOpcodes = Arrays.asList(ISHL + "",
			ISHR + "", LSHL + "", LSHR + "", IUSHR + "", LUSHR + "");

	public static void addUoiForConstants() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.REPLACE_CONSTANT);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId())
					&& m.getBaseMutationId() == null) {
				String addInfo = m.getAddInfo();
				String origValue = getRicOriginalValue(addInfo);
				String[] replaceValues;
				if (origValue.contains(".")) {
					Double d = Double.valueOf(origValue);
					replaceValues = new String[] { -d + "" };
				} else {
					Integer i = Integer.valueOf(origValue);
					replaceValues = new String[] { -i + "", ~i + "" };
				}

				boolean baseMutationUsed = false;
				for (String replaceVal : replaceValues) {
					if (baseMutationUsed) {
						Mutation m2 = Mutation.copyMutation(m);
						PossibilitiesRicMethodAdapter.setAddInfo(m2, origValue,
								replaceVal);
						QueryManager.saveMutation(m2);
						MutationCoverageFile.addDerivedMutation(m.getId(),
								m2.getId());
					} else {
						PossibilitiesRicMethodAdapter.setAddInfo(m, origValue,
								replaceVal);
						// QueryManager.updateMutation(m, null);
						session.update(m);
						baseMutationUsed = true;
					}
				}
			} else {
				session.delete(m);
			}
		}
		tx.commit();
		session.close();
		MutationCoverageFile.update();

	}

	private static String getRicOriginalValue(String addInfo) {
		String s = addInfo.substring("Replace ".length());
		int end = s.indexOf(' ');
		String result = s.substring(0, end);
		return result;
	}

	// TODO currently only covered mutations are considered
	public static void addRorMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.NEGATE_JUMP);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId())
					&& m.getBaseMutationId() == null) {
				String operatorAddinfo = m.getOperatorAddInfo();
				int standardReplaceOpcode = Integer.parseInt(operatorAddinfo);
				int originalOpcode = JumpReplacements.getReplacementMap().get(
						standardReplaceOpcode);
				int[] rorOpcodes = getRorOpcodes(standardReplaceOpcode);
				for (int replaceOpcode : rorOpcodes) {
					if (replaceOpcode != standardReplaceOpcode
							&& replaceOpcode != originalOpcode) {
						Mutation m2 = new Mutation(m.getClassName(),
								m.getMethodName(), m.getLineNumber(),
								m.getMutationForLine(), m.getMutationType());
						AbstractNegateJumpsAdapter.generateAddInfo(m2,
								originalOpcode, replaceOpcode);
						logger.info("Adding mutation" + m2);
						QueryManager.saveMutation(m2);
						MutationCoverageFile.addDerivedMutation(m.getId(),
								m2.getId());
					}
				}
			}
		}
		tx.commit();
		session.close();
		MutationCoverageFile.update();

	}

	private static int[] getRorOpcodes(int opcode) {
		if (ArrayUtils.contains(rorSingleOpcodes, opcode)) {
			return rorSingleOpcodes;
		}
		if (ArrayUtils.contains(rorIntegerCompareOpcodes, opcode)) {
			return rorIntegerCompareOpcodes;
		}
		return new int[0];

	}

	/**
	 * Adds new mutations of type REPLACE_CONSTANT for project.
	 */
	public static void addReplaceConstantMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.REPLACE_CONSTANT);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId())
					&& m.getBaseMutationId() == null) {
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

	public static void addAorMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.ARITHMETIC_REPLACE);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId()) // TODO also account
															// for not covered
					&& m.getBaseMutationId() == null) {
				String addInfo = m.getAddInfo();
				int originalValue = getOriginalValue(addInfo);

				Set<Integer> values = getAorReplacements(originalValue);
				for (Integer val : values) {
					Mutation m2 = new Mutation(m.getClassName(),
							m.getMethodName(), m.getLineNumber(),
							m.getMutationForLine(), m.getMutationType());
					AbstractArithmeticMethodAdapter.addInfoToMutation(m2,
							originalValue, val);
					// m2.setOperatorAddInfo(val + "");
					// m2.setAddInfo("Replace " + originalValue + " with " +
					// val);
					m2.setBaseMutationId(m.getId());
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

	private static Set<Integer> getAorReplacements(int operator) {
		int[] replaceOperators = getAorReplaceOperators(operator);
		Integer standardReplaceOperator = ReplaceMap.getReplaceMap().get(
				operator);
		return getReplacments(operator, standardReplaceOperator,
				replaceOperators);
	}

	private static Set<Integer> getReplacments(int operator,
			int standardReplaceOperator, int[] replaceOperators) {
		Set<Integer> result = new HashSet<Integer>();
		for (int i : replaceOperators) {
			result.add(i);
		}
		result.remove(operator);
		result.remove(standardReplaceOperator);
		return result;
	}

	private static int[] getAorReplaceOperators(int operator) {
		return getContainingArray(operator, aorIntOpcodes, aorLongOpcodes,
				aorFloatOpcodes, aorDoubleOpcodes);

	}

	private static int[] getContainingArray(int operator, int[]... arrays) {
		for (int[] array : arrays) {
			if (ArrayUtils.contains(array, operator)) {
				return array;
			}
		}
		return new int[0];
	}

	public static void addLorMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.ARITHMETIC_REPLACE);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		for (Mutation m : results) {
			if (MutationCoverageFile.isCovered(m.getId()) // TODO also account
															// for not covered
					&& m.getBaseMutationId() == null) {
				String addInfo = m.getAddInfo();
				int originalValue = getOriginalValue(addInfo);

				Set<Integer> values = getLorReplacements(originalValue);
				for (Integer val : values) {
					Mutation m2 = new Mutation(m.getClassName(),
							m.getMethodName(), m.getLineNumber(),
							m.getMutationForLine(), m.getMutationType());
					AbstractArithmeticMethodAdapter.addInfoToMutation(m2,
							originalValue, val);
					// m2.setOperatorAddInfo(val + "");
					// m2.setAddInfo("Replace " + originalValue + " with " +
					// val);
					m2.setBaseMutationId(m.getId());
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

	private static Set<Integer> getLorReplacements(int operator) {
		int[] replaceOperators = getLorReplaceOperators(operator);
		Integer standardReplaceOperator = ReplaceMap.getReplaceMap().get(
				operator);
		return getReplacments(operator, standardReplaceOperator,
				replaceOperators);
	}

	private static int[] getLorReplaceOperators(int operator) {
		return getContainingArray(operator, lorIntegerOpcodes, lorLongOpcodes);
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
		try {
			Field field = Opcodes.class.getField(s);
			int val = (Integer) field.get(null);
			return val;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(s);
	}

	public static void main(String[] args) {
		generateOffutt96Sufficient();
	}

	public static void generateOffutt96Sufficient() {
		addRorMutations();
		addAorMutations();
		addLorMutations();
		addUoiForConstants();
		removeUnecessaryOperators();
	}

	public static void removeUnecessaryOperators() {
		deleteMutations(MutationType.REMOVE_CALL);
		deleteMutations(MutationType.REPLACE_VARIABLE);
		deleteShiftMutations();
	}

	private static void deleteShiftMutations() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", MutationType.ARITHMETIC_REPLACE);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		int deletes = 0;
		int flushs = 0;
		for (Mutation m : results) {
			if (shiftOpcodes.contains(m.getOperatorAddInfo())) {
				session.delete(m);
				deletes++;
				if (deletes % 20 == 0) {
					// 20, same as the JDBC batch size
					// flush a batch of inserts and release memory:
					// see
					// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
					long startFlush = System.currentTimeMillis();
					flushs++;
					logger.info("Doing temporary flush " + flushs);
					session.flush();
				}
			}
		}
		tx.commit();
		session.close();
		MutationCoverageFile.update();
	}

	private static void deleteMutations(MutationType type) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String projectPrefix = ConfigurationLocator
				.getJavalancheConfiguration().getProjectPrefix();
		Query query = session
				.createQuery("from Mutation as m where className LIKE '"
						+ projectPrefix + "%' and m.mutationType=:type");
		query.setParameter("type", type);
		@SuppressWarnings("unchecked")
		List<Mutation> results = query.list();
		int deletes = 0;
		int flushs = 0;
		for (Mutation m : results) {
			session.delete(m);
			deletes++;
			if (deletes % 20 == 0) {
				// 20, same as the JDBC batch size
				// flush a batch of inserts and release memory:
				// see
				// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
				long startFlush = System.currentTimeMillis();
				flushs++;
				logger.info("Doing temporary flush " + flushs);
				session.flush();
			}
		}
		tx.commit();
		session.close();
		MutationCoverageFile.update();
	}

}
