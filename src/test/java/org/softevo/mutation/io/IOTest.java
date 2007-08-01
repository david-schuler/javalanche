package org.softevo.mutation.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.softevo.mutation.io.ClassFileSource;
import org.softevo.mutation.properties.MutationProperties;

public class IOTest {

	@Test
	public void testIO() {
		ClassFileSource cfs = new ClassFileSource();
		try {
			List l = cfs.getClasses(new File(MutationProperties.ASPECTJ_DIR));
			assertTrue(l.size() > 10);
			System.out.println(l.size());
			for (Object o : l) {
				assertTrue(o.toString().endsWith(".class"));
				// System.out.println(o + " "+ ((File)o).length());
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO Exception was thrown");
		}

	}

}
