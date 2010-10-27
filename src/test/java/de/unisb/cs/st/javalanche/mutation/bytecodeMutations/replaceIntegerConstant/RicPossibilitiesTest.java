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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;

public class RicPossibilitiesTest {

	@Test
	public void testForOneClass() throws Exception {
		File file = new File(TestProperties.SAMPLE_FILE);
		ClassReader cr = new ClassReader(new FileInputStream(file));
		ClassWriter cw = new ClassWriter(0);
		MutationPossibilityCollector mutationPossibilityCollector = new MutationPossibilityCollector();
		PossibilitiesRicClassAdapter possibilitiesRicClassAdapter = new PossibilitiesRicClassAdapter(
				cw, mutationPossibilityCollector);
		cr.accept(possibilitiesRicClassAdapter, 0);
		System.out.println(mutationPossibilityCollector.size());
		int expectedMutations = 140;
		assertEquals("Expecting " + expectedMutations + " mutations",
				mutationPossibilityCollector.size(), expectedMutations);
	}

	private static class HelperLoader extends ClassLoader {

		public void define(String className, byte[] bytecode) {
			defineClass(className, bytecode, 0, bytecode.length);
		}

	}


}
