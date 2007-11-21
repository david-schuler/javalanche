package org.softevo.mutation.debug;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.MutationScannerTransformer;
import org.softevo.mutation.bytecodeMutations.MutationTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.results.persistence.MutationManager;

public class MissedMutationTest {

private	String CLASS_LOCATION = "/scratch/schuler/aspectJ/util/bin/org/aspectj/util/LangUtil.class";

	@Test
	public void testLangUtil() {
		FileTransformer ft = new FileTransformer(new File(CLASS_LOCATION));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new MutationScannerTransformer(mpc));
		Assert.assertTrue(mpc.size() > 40);
		mpc.toDB();
		//MutationManager.setApplyAllMutation(true);
		FileTransformer ft2 = new FileTransformer(new File(CLASS_LOCATION));
		ft2.process(new MutationTransformer());

	}

}
