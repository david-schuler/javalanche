package org.softevo.mutation;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;
import org.softevo.mutation.io.Io;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.persistence.MutationManager;

public class TestLangUtil {

	private String CLASS_LOCATION = "/scratch/schuler/aspectj/util/bin/org/aspectj/util/LangUtil.class";

	private static class TestClassLoader extends ClassLoader {

		private final byte[] transformed;

		public TestClassLoader(byte[] transformed) {
			this.transformed = transformed;
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.equals("TEST")) {
				return defineClass("org.aspectj.util.LangUtil", transformed, 0,
						transformed.length);
			}
			return super.loadClass(name);
		}
	}

	@Test
	public void testLangUtil() {
		System.out.println("Test Started");
		FileTransformer ft = new FileTransformer(new File(CLASS_LOCATION));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new MutationScannerTransformer(mpc));
		mpc.toDB();
		MutationManager.setApplyAllMutation(true);
		System.out.println("Mutations Addded");
		BytecodeTransformer bct = new MutationTransformer();
		byte[] bytes = null;
		try {
			bytes = Io.getBytesFromFile(new File(CLASS_LOCATION));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] transformed = bct.transformBytecode(bytes);
		TestClassLoader tcl = new TestClassLoader(transformed);
		try {
			tcl.loadClass("TEST");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		MutationManager.setApplyAllMutation(false);
	}
}
