package de.unisb.cs.st.javalanche.mutation.examples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.javaagent.MutationPreMain;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class GetMutatedByteCode {

	private static final boolean VERBOSE = true;

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		if (args.length < 2) {
			System.out.println("Usage: srcfile destfile");
			return;
		}
		new GetMutatedByteCode().writeBytecode(new File(args[0]), new File(
				args[1]));
	}

	private void writeBytecode(File src, File dest)
			throws FileNotFoundException, IOException {

		MutationPossibilityCollector posibilities = getPosiibilities(src);
		QueryManager.saveMutations(posibilities.getPossibilities());

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassReader cr = new ClassReader(new FileInputStream(src));

		ClassVisitor cv = new CheckClassAdapter(cw);
		if (VERBOSE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(
					MutationPreMain.sysout));
		}
		cv = new MutationsClassAdapter(cv, new MutationManager(true));
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] byteArray = cw.toByteArray();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
		out.write(byteArray);
		out.flush();
		out.close();
	}

	private MutationPossibilityCollector getPosiibilities(File src)
			throws IOException, FileNotFoundException {
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassReader cr = new ClassReader(new FileInputStream(src));

		ClassVisitor cv = new CheckClassAdapter(cw);
		if (VERBOSE) {
			cv = new TraceClassVisitor(cv, new PrintWriter(System.out));
		}
		cv = new MutationsCollectorClassAdapter(cv, mpc);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		return mpc;
	}
}
