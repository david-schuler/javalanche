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
package de.unisb.cs.st.javalanche.mutation.testutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.ScanVariablesTransformer;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;

public class TestUtil {

	public static List<Mutation> getMutationsForClazzOnClasspath(Class<?> clazz)
			throws IOException {
		String fileName = clazz.getCanonicalName().replace('.', '/') + ".class";
		InputStream is = TestUtil.class.getClassLoader()
				.getResourceAsStream(fileName);
		byte[] byteArray = IOUtils.toByteArray(is);
		return getMutations(byteArray, clazz.getCanonicalName());
	}



	public static List<Mutation> getMutations(byte[] classBytes,
			String className) {
		ScanVariablesTransformer sTransformer = new ScanVariablesTransformer();
		sTransformer.scanClass(className.replace('.', '/'), classBytes);
		sTransformer.write();

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		MutationsCollectorClassAdapter mcca = new MutationsCollectorClassAdapter(
				cw, mutationPossibilityCollector);
		ClassReader cr = new ClassReader(classBytes);
		cr.accept(mcca, ClassReader.EXPAND_FRAMES);
		List<Mutation> possibilies = mutationPossibilityCollector
				.getPossibilities();
		return possibilies;
	}

	public static List<Mutation> getMutations(File f, String className)
			throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(f);
		return getMutations(bytes, className);
	}

	public static List<Mutation> filterMutations(List<Mutation> mutations,
			MutationType t) {
		List<Mutation> result = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation != null && mutation.getMutationType().equals(t)) {
				result.add(mutation);
			}
		}
		return result;
	}

	public static List<Mutation> filterMutations(List<Mutation> mutations,
			int lineNumber) {
		List<Mutation> result = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation != null && mutation.getLineNumber() == lineNumber) {
				result.add(mutation);
			}
		}
		return result;
	}

}
