package de.st.cs.unisb.javalanche.testutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import de.st.cs.unisb.javalanche.bytecodeMutations.MutationScannerTransformer;
import de.st.cs.unisb.javalanche.bytecodeMutations.MutationTransformer;
import de.st.cs.unisb.javalanche.mutationPossibilities.MutationPossibilityCollector;
import de.st.cs.unisb.javalanche.properties.TestProperties;
import de.st.cs.unisb.javalanche.results.persistence.MutationManager;

import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;

public class LangUtilTest {

	private static Logger logger = Logger.getLogger(LangUtilTest.class);

	private static final String LANG_UTIL = "keyForLangUtil";

	private static class TestClassLoader extends ClassLoader {

		private final byte[] transformed;

		public TestClassLoader(byte[] transformed) {
			this.transformed = transformed;
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.equals(LANG_UTIL)) {
				return defineClass(TestProperties.LANG_UTIL_CLASS_NAME, transformed, 0,
						transformed.length);
			}
			return super.loadClass(name);
		}
	}

	@Ignore("AspectJ has to be on the classpath")
	@Test
	public void testLangUtil() {
		try {
			System.out.println("Test Started");
			MutationPossibilityCollector mpc = new MutationPossibilityCollector();
			BytecodeTransformer bt = new MutationScannerTransformer(mpc);
			InputStream is = LangUtilTest.class
					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
			bt.transformBytecode(new ClassReader(is));
			mpc.toDB();
			MutationManager.setApplyAllMutation(true);
			logger.info("Mutations added");
			BytecodeTransformer bct = new MutationTransformer();
			InputStream is2 = LangUtilTest.class
					.getResourceAsStream(TestProperties.LANG_UTIL_CLAZZ);
			byte[] transformed;
			transformed = bct.transformBytecode(new ClassReader(is2));
			TestClassLoader tcl = new TestClassLoader(transformed);
			tcl.loadClass(LANG_UTIL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		MutationManager.setApplyAllMutation(false);
	}
}
