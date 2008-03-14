package org.softevo.mutation.debug;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;

public class MissedMutationTest {

	private String CLASS_LOCATION = "/scratch/schuler/aspectJ/util/bin/org/aspectj/util/LangUtil.class";

	@Test
	public void testLangUtil() {
		URL url = MissedMutationTest.class.getClassLoader()
				.getResource("LangUtil.clazz");
		try {
		URI uri = url.toURI();
		File file = new File(uri);
		System.out.println(file);
		FileTransformer ft = new FileTransformer(file);
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new MutationScannerTransformer(mpc));
		Assert.assertTrue(mpc.size() > 40);
		// mpc.toDB();
		// MutationManager.setApplyAllMutation(true);
		// FileTransformer ft2 = new FileTransformer(new File(CLASS_LOCATION));
		// ft2.process(new MutationTransformer());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
