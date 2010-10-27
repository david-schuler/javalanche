package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;


import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ASTParseResult;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.AdaptedMutationDescription;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ParseResultAnalyzer;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.SourceScanner;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.LastLineClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class TestDebug {

	// private String className = "org.jaxen.javabean.JavaBeanXPath";
	private String className = "de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.DebugTEMPLATE";

	@Test
	public void test() throws Exception {


		MutationProperties.PROJECT_PREFIX = "org.jaxen";
		// File f = new File("/Users/schuler/JavaBeanXPath.class");
		// File analyzeFile = new File("/Users/schuler/JavaBeanXPath.java");
		File f = new File(
				"./target/test-classes/de/unisb/cs/st/javalanche/mutation/adaptedMutations/bytecode/DebugTEMPLATE.class");
		File analyzeFile = new File(
				"./src/test/java/de/unisb/cs/st/javalanche/mutation/adaptedMutations/bytecode/DebugTEMPLATE.java");
		byte[] bytes = FileUtils.readFileToByteArray(f);
		System.out.println("Got Bytes");
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		deleteMutations(className);
		scan(bytes);
		analyze(analyzeFile);
		redefineMutations(className);
		transform(bytes);
	}

	private void analyze(File outFile) {
		Map<String, ASTParseResult> analyzeJavaFile = SourceScanner
				.analyzeJavaFile(outFile);
		List<AdaptedMutationDescription> analyzeReplace = ParseResultAnalyzer
				.analyzeReplace(analyzeJavaFile);
		List<AdaptedMutationDescription> analyzeJump = ParseResultAnalyzer
				.analyzeJump(analyzeJavaFile);
		ParseResultAnalyzer.writeJumpMutations(analyzeJump);
		ParseResultAnalyzer.writeMutations(analyzeReplace);
	}

	protected void transform(byte[] bytes) {
		String orig = AsmUtil.classToString(bytes);
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		// MutationTransformer m = new MutationTransformer();
		// m.transformBytecode(bytes);
		ClassVisitor cv = new CheckClassAdapter(cw);
		cv = new MutationsClassAdapter(cv, BytecodeInfo.read(),
				new MutationManager());
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] result = cw.toByteArray();
		System.out.println(result.length + "  " + bytes.length);
		// String errors = AsmUtil.checkClass(result);
		System.out.println("---------------------------------------");
		// System.out.println(errors);
		System.out.println("---------------------------------------");

		String resultString = AsmUtil.classToString(result);
		System.out.println(resultString);
	}

	protected List<Mutation> scan(byte[] classfileBuffer) {
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
				mpc);
		mutationScannerTransformer.transformBytecode(classfileBuffer);
		List<Mutation> mutations = mpc.getPossibilities();
		System.out.println(mutations.size() + "  mutations for class. ");
		System.out.println("Start Saving");
		mpc.updateDB();
		prepareLastLineInfo(classfileBuffer);
		// QueryManager.saveMutations(mutations);
		return mutations;

	}

	private void prepareLastLineInfo(byte[] classfileBuffer) {
		BytecodeInfo lastLineInfo = new BytecodeInfo();
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		LastLineClassAdapter cv = new LastLineClassAdapter(cw, lastLineInfo);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		lastLineInfo.write();
	}
	// protected List<Mutation> scan(File classFile) {
	//
	// MutationPossibilityCollector mpc = generateTestDataInDB(classFile
	// .getAbsolutePath(), new JumpsCollectorTransformer(null));
	// MutationPossibilityCollector mpc2 = generateTestDataInDB(classFile
	// .getAbsolutePath(), new ReplaceCollectorTransformer(null));
	// MutationPossibilityCollector mpc3 = generateTestDataInDB(classFile
	// .getAbsolutePath(), new RemoveMethodCallsCollectorTransformer(
	// null));
	// MutationPossibilityCollector mpc4 = generateTestDataInDB(classFile
	// .getAbsolutePath(), new ArithmeticReplaceCollectorTransformer(
	// null));
	// MutationPossibilityCollector mpc5 = generateTestDataInDB(classFile
	// .getAbsolutePath(), new NegateJumpsCollectorTransformer(null));
	// MutationPossibilityCollector mpc6 = generateTestDataInDB(classFile
	// .getAbsolutePath(), new RicCollectorTransformer(null));
	//
	// System.setProperty("mutation.run.mode", "mutation");
	// System.setProperty("invariant.mode", "off");
	// redefineMutations(className);
	// List<Mutation> result = new ArrayList<Mutation>();
	// result.addAll(mpc.getPossibilities());
	// result.addAll(mpc2.getPossibilities());
	// result.addAll(mpc3.getPossibilities());
	// result.addAll(mpc4.getPossibilities());
	// result.addAll(mpc5.getPossibilities());
	// result.addAll(mpc6.getPossibilities());
	// QueryManager.saveMutations(result);
	// System.out
	// .println(result.size() + "  mutations for class " + classFile);
	// return result;
	//
	// }
}
