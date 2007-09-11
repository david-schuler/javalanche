package org.softevo.mutation.replaceIntegerConstant;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicTransformer;
import org.softevo.mutation.properties.MutationProperties;

public class RicTest {

//	private static Mutations mutations = Mutations.fromXML();

	private static class HelperLoader extends ClassLoader {

		public void define(String className, byte[] bytecode) {
			defineClass(className, bytecode, 0, bytecode.length);
		}

	}

	@Test(expected = NoClassDefFoundError.class)
	public void testForOneClass() {
		ClassReader classReader = null;
		try {
			classReader = new ClassReader(new BufferedInputStream(
					new FileInputStream(MutationProperties.SAMPLE_FILE)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		RicTransformer ricTransformer = new RicTransformer();
		byte[] bytes = ricTransformer.transformBytecode(classReader);
		HelperLoader helperLoader = new HelperLoader();
		helperLoader.define(MutationProperties.SAMPLE_FILE_NAME, bytes);
	}

}
