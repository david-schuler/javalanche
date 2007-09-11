package org.softevo.mutation.bytecodeMutations.arithmetic;

import java.io.File;

import org.junit.Test;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.MutationProperties;

public class ArtihmeticPossibilitiesTest {

	@Test
	public void testNegateJumps(){
		FileTransformer ft = new FileTransformer(new File(MutationProperties.SAMPLE_FILE));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new ArithmeticReplaceCollectorTransformer(mpc));
		System.out.println(mpc.size());
		mpc.toDB();
	}

}
