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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import de.unisb.cs.st.javalanche.mutation.debug.MissedMutationTest;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class ArtihmeticPossibilitiesTest {

	@Test
	public void testPossibilities() {
		InputStream is = MissedMutationTest.class.getClassLoader()
				.getResourceAsStream(TestProperties.ADVICE_CLAZZ);
		System.out.println(is);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		BytecodeTransformer bt = new ArithmeticReplaceCollectorTransformer(mpc);
		try {
			bt.transformBytecode(new ClassReader(is));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(mpc.size() > 10);
	}
}
