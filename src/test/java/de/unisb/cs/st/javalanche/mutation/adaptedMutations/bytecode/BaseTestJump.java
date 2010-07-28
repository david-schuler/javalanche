package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.google.common.base.Joiner;

import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ASTParseResult;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.AdaptedMutationDescription;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ParseResultAnalyzer;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.SourceScanner;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.JumpsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.JumpsCollectorTransformer;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.LastLineClassAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class BaseTestJump extends BaseTestAdapted {

	public BaseTestJump(Class<?> c) {
		super(c);
	}

	public Class<?> prepareTest() throws Exception {
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
		byte[] bytes = FileUtils.readFileToByteArray(classFile);
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		List<Mutation> pos = scan(classFile);
		analyze(outFile);
		redefineMutations(className);
		transform(classFile);
		Class<?> clazz = loadClass(outDir);
		return clazz;
	}

	protected void analyze(File outFile) {
		Map<String, ASTParseResult> analyzeJavaFile = SourceScanner
				.analyzeJavaFile(outFile);
		List<AdaptedMutationDescription> analyzeJump = ParseResultAnalyzer
				.analyzeJump(analyzeJavaFile);
		System.out.println(Joiner.on("\n").join(analyzeJump));
		ParseResultAnalyzer.writeJumpMutations(analyzeJump);
	}

	protected void transform(File classFile) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(classFile);
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		JumpsClassAdapter cv = new JumpsClassAdapter(cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		byte[] result = cw.toByteArray();
		// String transformed = AsmUtil.checkClass(result);
		String transformed = AsmUtil.classToString(result);
		FileUtils.writeByteArrayToFile(classFile, result);
		System.out.println("---------------------------------------");
		System.out.println(transformed);
	}

	protected List<Mutation> scan(File classFile) {
		MutationPossibilityCollector mpc = generateTestDataInDB(classFile
				.getAbsolutePath(), new JumpsCollectorTransformer(null));
		System.setProperty("mutation.run.mode", "mutation");
		System.setProperty("invariant.mode", "off");
		redefineMutations(className);

		prepareLastLineInfo(classFile);
		List<Mutation> pos = mpc.getPossibilities();
		return pos;
	}

	private void prepareLastLineInfo(File classFile) {
		BytecodeInfo lastLineInfo = new BytecodeInfo();
		byte[] ar;
		try {
			ar = FileUtils.readFileToByteArray(classFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ClassReader cr = new ClassReader(ar);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		LastLineClassAdapter cv = new LastLineClassAdapter(cw, lastLineInfo);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		lastLineInfo.write();
	}

}
