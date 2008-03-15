package org.softevo.mutation.bytecodeMutations.negateJumps;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import de.unisb.st.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.TestProperties;

public class NegateJumpsPossibilitiesTest {

	@Test
	public void testNegateJumps() {
		FileTransformer ft = new FileTransformer(new File(
				TestProperties.SAMPLE_FILE));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new NegateJumpsCollectorTransformer(mpc));
		Assert.assertTrue(mpc.size() > 40);
	}

}
