package de.st.cs.unisb.javalanche.testDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import de.st.cs.unisb.ds.util.io.DirectoryFileSource;
import de.st.cs.unisb.ds.util.io.XmlIo;
import de.st.cs.unisb.javalanche.properties.MutationProperties;

public class TestCaseDetector {

	public static void main(String[] args) throws IOException {
		Set<String> testcases = new HashSet<String>();
		String dir = "/scratch/schuler/jtopas/source/";
		Collection<File> files = DirectoryFileSource.getFilesByExtension(
				new File(dir), "class");
		for (File f : files) {
			ClassReader cr = new ClassReader(new FileInputStream(f));
			DetectTestClassAdapter detectTestClassAdapter = new DetectTestClassAdapter(
					new ClassWriter(0));
			cr.accept(detectTestClassAdapter, ClassReader.EXPAND_FRAMES);
			if (detectTestClassAdapter.isTestCase()) {
				testcases.add(detectTestClassAdapter.getClassName());
			}
		}
		XmlIo.toXML(testcases, new File(MutationProperties.TESTCASES_FILE));
	}
}
