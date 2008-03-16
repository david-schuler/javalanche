package org.softevo.mutation.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

public class IOTest {

	@Test
	public void testIO() {
		try {
			Collection<File> l = DirectoryFileSource.getFilesByExtension(new File("target/classes"), ".class");
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
