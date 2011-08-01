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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class PossibilitiesRicMethodAdapter extends AbstractRicMethodAdapter {

	private static final Logger logger = Logger
			.getLogger(PossibilitiesRicMethodAdapter.class);

	MutationPossibilityCollector mutationPossibilityCollector;

	public PossibilitiesRicMethodAdapter(MethodVisitor mv, String className,
			String methodName,
			MutationPossibilityCollector mutationPossibilityCollector,
			Map<Integer, Integer> possibilities, String desc) {
		super(mv, className, methodName, true, possibilities, desc);
		this.mutationPossibilityCollector = mutationPossibilityCollector;
	}

	private void countMutation(int i) {
		List<String> replaceValues = new ArrayList<String>();
		replaceValues.add((i + 1) + "");
		replaceValues.add((i - 1) + "");
		if (i != 0 && i != 1 && i != -1) {
			replaceValues.add("0");
		}
		countMutation(i + "", replaceValues.toArray(new String[0]));
	}

	private void countMutation(String originalVal, String... replacementValues) {
		replacementValues = removeDuplicates(replacementValues); // e.g. NaN - 1 = NaN;
		if (!mutationCode) {
			Mutation baseMutation = null;
			int possibilitiesForLine = getPossibilityForLine();
			for (String replaceValue : replacementValues) {

				Mutation mutation = new Mutation(className, getMethodName(),
						getLineNumber(), possibilitiesForLine,
						Mutation.MutationType.REPLACE_CONSTANT);
				setAddInfo(mutation, originalVal, replaceValue);
				if (baseMutation == null) {
					baseMutation = mutation;
					QueryManager.saveMutation(mutation);
				} else {
					Long baseId = baseMutation.getId();
					mutation.setBaseMutationId(baseId);
					QueryManager.saveMutation(mutation);
					Long mId = mutation.getId();
					if (baseId == null || mId == null) {
						throw new RuntimeException(
								"Expected that no id is null. Base id: "
										+ baseId + " Mutation id: " + mId);
					}
					MutationCoverageFile.addDerivedMutation(baseId, mId);
				}
				mutationPossibilityCollector.addPossibility(mutation);
			}

			if (baseMutation != null) {
				CoverageDataUtil.insertCoverageCalls(mv, baseMutation);
			}
			addPossibilityForLine();
		}
		// if (i != 0 && i != 1 && i != -1) {
	}

	private String[] removeDuplicates(String[] replacementValues) {
		return (new HashSet<String>(Arrays.asList(replacementValues)))
				.toArray(new String[0]);

	}

	public static void setAddInfo(Mutation mutation, String originalValue,
			String replaceValue) {
		mutation.setOperatorAddInfo(replaceValue);
		mutation.setAddInfo("Replace " + originalValue + " with "
				+ replaceValue);
	}

	@Override
	protected void biOrSiPush(int operand) {
		countMutation(operand);
	}

	@Override
	protected void doubleConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void floatConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void longConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void intConstant(int i) {
		countMutation(i);
	}

	@Override
	protected void ldc(Number constant) {
		Class<? extends Number> clazz = constant.getClass();
		if (clazz.equals(Double.class)) {
			double d = (Double) constant;
			if (d != 0. && d != 1. && d != -1.) {
				countMutation(constant + "", (d - 1) + "", (d + 1) + "", "0");
			} else {
				countMutation(constant + "", (d - 1) + "", (d + 1) + "");
			}
		} else if (clazz.equals(Float.class)) {
			float f = (Float) constant;
			if (f != 0.f && f != 1.f && f != -1.f) {
				countMutation(constant + "", (f - 1) + "", (f + 1) + "", "0");
			} else {
				countMutation(constant + "", (f - 1) + "", (f + 1) + "");
			}
		} else if (clazz.equals(Long.class)) {
			long l = (Long) constant;
			if (l != 0l && l != 1l && l != -1l) {
				countMutation(constant + "", (l - 1) + "", (l + 1) + "", "0");
			} else {
				countMutation(constant + "", (l - 1) + "", (l + 1) + "");
			}
		} else {
			countMutation(constant.intValue());
		}
	}

	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

}
