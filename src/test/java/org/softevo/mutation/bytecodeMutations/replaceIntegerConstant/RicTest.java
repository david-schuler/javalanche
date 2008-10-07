package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.softevo.mutation.properties.TestProperties;

public class RicTest {

	private static class HelperLoader extends ClassLoader {

		public void define(String className, byte[] bytecode) {
			defineClass(className, bytecode, 0, bytecode.length);
		}

	}


	@Test(expected = NoClassDefFoundError.class)
	public void testForOneClass() {
		ClassReader classReader = null;
		try {
			classReader = new ClassReader(RicTest.class.getClassLoader()
					.getResourceAsStream(TestProperties.ADVICE_CLAZZ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		RicTransformer ricTransformer = new RicTransformer();
		byte[] bytes = ricTransformer.transformBytecode(classReader);
		HelperLoader helperLoader = new HelperLoader();
		helperLoader.define(TestProperties.SAMPLE_FILE_CLASS_NAME, bytes);
	}

}
