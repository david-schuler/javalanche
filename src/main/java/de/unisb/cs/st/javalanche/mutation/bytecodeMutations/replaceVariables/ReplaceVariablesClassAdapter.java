/*
 ** Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

public class ReplaceVariablesClassAdapter extends ClassAdapter {

	private String className;

	private MutationPossibilityCollector mutationPossibilityCollector;

	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

	private List<VariableInfo> staticVariables = new ArrayList<VariableInfo>();

	private MutationManager mm = new MutationManager();

	private ProjectVariables projectVariables = ProjectVariables.read();

	public ReplaceVariablesClassAdapter(ClassVisitor cv,
			MutationPossibilityCollector collector) {
		super(cv);
		this.mutationPossibilityCollector = collector;
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		ReplaceVariablesMethodAdapter actualAdapter = new ReplaceVariablesMethodAdapter(
				super.visitMethod(access, name, desc, signature, exceptions),
				className, name, possibilities, desc, mm,
				projectVariables.getStaticVariables(className),
				projectVariables.getClassVariables(className));
		AnalyzerAdapter analyzerAdapter = new AnalyzerAdapter(className,
				access, name, desc, actualAdapter);
		actualAdapter.setAnlyzeAdapter(analyzerAdapter);
		return analyzerAdapter;
	}

}
