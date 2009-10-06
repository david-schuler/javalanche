/*
* Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

import static de.unisb.cs.st.javalanche.mutation.properties.MutationProperties.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.unisb.cs.st.ds.util.io.Io;

public class ExcludesTest {

	static {
		EXCLUDE_FILE.delete();
		TEST_EXCLUDE_FILE.delete();
	}

	private static final String CLASS_1 = "a.b.c1";
	private static final String CLASS_2 = "a.b.c2";
	private static final String CLASS_3 = "a.b.c.d3";

	Excludes instance = Excludes.getInstance();

	@Test
	public void testNotExcludeNotAddedFile() {
		boolean excl = instance.shouldExclude(CLASS_1);
		assertFalse(excl);
	}

	@Test
	public void testNotExcludeAddedFile() {
		instance.addClasses(Arrays.asList(CLASS_2));
		assertFalse(instance.shouldExclude(CLASS_2));
	}

	@Test
	public void testExcludeFile() {
		instance.exclude(CLASS_3);
		assertTrue(instance.shouldExclude(CLASS_3));
	}

	@Test
	public void testWriteFiles() {
		EXCLUDE_FILE.delete();
		TEST_EXCLUDE_FILE.delete();

		assertFalse(EXCLUDE_FILE.exists());
		assertFalse(TEST_EXCLUDE_FILE.exists());

		instance.addClasses(Arrays.asList(CLASS_1, CLASS_2));
		instance.exclude(CLASS_3);
		instance.writeFile();
		assertTrue(EXCLUDE_FILE.exists());
		assertTrue(TEST_EXCLUDE_FILE.exists());

		List<String> lines = Io.getLinesFromFile(EXCLUDE_FILE);
		System.out.println(lines);
		assertTrue(lines.contains("# " + CLASS_1));
		assertTrue(lines.contains("# " + CLASS_2));
		assertTrue(lines.contains(CLASS_3));

	}

}
