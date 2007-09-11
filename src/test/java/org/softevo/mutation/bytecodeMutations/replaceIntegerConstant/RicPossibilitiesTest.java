package org.softevo.mutation.bytecodeMutations.replaceIntegerConstant;

import java.io.File;

import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.softevo.bytecodetransformer.processFiles.BytecodeTransformer;
import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.PossibilitiesRicClassAdapter;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RicCollectorTransformer;
import org.softevo.mutation.mutationPossibilities.MutationPossibilityCollector;
import org.softevo.mutation.properties.MutationProperties;



public class RicPossibilitiesTest {

	@Test
	public void testForOneClass(){
		FileTransformer ft = new FileTransformer(new File(MutationProperties.SAMPLE_FILE));
		ft.process(new RICTransformer());
	}


	@Test
	public void testForOneClass2(){
		FileTransformer ft = new FileTransformer(new File(MutationProperties.SAMPLE_FILE));
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
