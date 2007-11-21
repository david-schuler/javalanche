package org.softevo.mutation.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.softevo.mutation.properties.MutationProperties;

public class IOTest {

	@Test
	public void testIO() {
		try {
			Collection<File> l = DirectoryFileSource.getFilesByExtension(new File(MutationProperties.ASPECTJ_DIR), ".class");
			assertTrue(l.size() > 10);
			for (File file : l) {
				assertTrue(file.toString().endsWith(".class"));
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO Exception was thrown");
		}

	}

}
