package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;

import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import de.unisb.st.bytecodetransformer.processFiles.BytecodeTransformer;
import de.unisb.st.bytecodetransformer.processFiles.FileTransformer;

import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.PossibilitiesRicClassAdapter;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicCollectorTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.TestProperties;



public class RicPossibilitiesTest {

	@Test
	public void testForOneClass(){
		FileTransformer ft = new FileTransformer(new File(TestProperties.SAMPLE_FILE));
		ft.process(new RICTransformer());
	}


	@Test
	public void testForOneClass2(){
		FileTransformer ft = new FileTransformer(new File(TestProperties.SAMPLE_FILE));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new RicCollectorTransformer(mpc));
	}
	private static class RICTransformer extends BytecodeTransformer{

		@Override
		protected ClassVisitor classVisitorFactory(ClassWriter cw) {
			return new PossibilitiesRicClassAdapter(cw,new MutationPossibilityCollector());
		}

	}

}
