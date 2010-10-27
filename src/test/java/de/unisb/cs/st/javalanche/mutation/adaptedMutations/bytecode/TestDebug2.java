package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jaxen.expr.Step;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ASTParseResult;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.AdaptedMutationDescription;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.ParseResultAnalyzer;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.SourceScanner;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.BytecodeInfo;
import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.LastLineClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationTransformer;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsClassAdapter;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class TestDebug2 {

	private static String name = "PatternParser";
	private static String className = "xorg.jaxen.pattern." + name;
	// private static String className =
	// "de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode."
	// + name;
	static File f = new File("/Users/schuler/" + name + ".class");
	// static File f = new File(
	// "/Users/schuler/workspace/mutationTest/target/test-classes/org/jaxen/saxpath/base/XPathreaderTEMPLATE.class");
	static File analyzeFile = new File("/Users/schuler/" + name + ".java");
	// static File analyzeFile = new File(
	// "/Users/schuler/workspace/mutationTest/src/test/java/org/jaxen/saxpath/base/XPathreaderTEMPLATE.java");
	public static File transformedFile = new File("/Users/schuler/" + name
			+ "TRANSFORMED.class");;

	// private String className =
	// "de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.DebugTEMPLATE";

	@Test
	public void test() throws Exception {

		MutationProperties.PROJECT_PREFIX = "xorg.jaxen";
		MutationProperties.IGNORE_ADAPTED_REPLACE = false;
		MutationProperties.IGNORE_ADAPTED_JUMPS = false;
		// MutationProperties.PROJECT_PREFIX =
		// "de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.Pattern";
		// File f = new File(
		// "./target/test-classes/de/unisb/cs/st/javalanche/mutation/adaptedMutations/bytecode/DebugTEMPLATE.class");
		// File analyzeFile = new File(
		// "./src/test/java/de/unisb/cs/st/javalanche/mutation/adaptedMutations/bytecode/DebugTEMPLATE.java");

		byte[] bytes = FileUtils.readFileToByteArray(f);
		System.out.println("Got Bytes");
		String orig = AsmUtil.classToString(bytes);
		System.out.println(orig);
		deleteMutations(className);
		scan(bytes);
		analyze(analyzeFile);
		redefineMutations1(className);

		transform(bytes);
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		// System.out.println(Join.join("\n", mutationsForClass));
		MCL cl = new MCL();
		Class<?> loadClass = cl.loadClass(className);
		Object newInstance = loadClass.newInstance();
		Method[] methods = loadClass.getMethods();
		System.out.println(Arrays.toString(methods));
		// Method method = loadClass.getMethod("navigationStep", Step.class);
		// System.out.println(method);
		// Object invoke = method.invoke(newInstance, (Step) null);
		// System.out.println(invoke);
	}

	public static void redefineMutations1(String testClassName) {
		List<Long> ids = new ArrayList<Long>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		for (Mutation m : mList) {


// if ((m.getLineNumber() == 458 && m.getMutationForLine() == 1 && m
			// .getMutationType() == MutationType.ADAPTED_ALWAYS_ELSE)
			// || (m.getLineNumber() == 9462
			// && m.getMutationForLine() == 0 && m
			// .getMutationType() == MutationType.RIC_ZERO)
			// || (m.getLineNumber() == 9484
			// && m.getMutationForLine() == 0 && m
			// .getMutationType() == MutationType.REMOVE_CALL)) {

				ids.add(m.getId());
			// }
		}
		tx.commit();
		session.close();
		StringBuilder sb = new StringBuilder();
		for (Long l : ids) {
			sb.append(l + "\n");
		}
		File file = new File(DEFAULT_OUTPUT_FILE);
		Io.writeFile(sb.toString(), file);
		MutationProperties.MUTATION_FILE_NAME = file.getAbsolutePath();
	}

	static class MCL extends ClassLoader {

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.equals(className)) {
				try {
					// super.findClass(name);
					System.out.println("LOAD CLASS " + name + "  ");
					byte[] b = FileUtils.readFileToByteArray(transformedFile);
					Class<?> defineClass = defineClass(name, b, 0, b.length);
					return defineClass;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return super.loadClass(name);
		}
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
		//
		MutationTransformer mutationTransformer = new MutationTransformer();

		byte[] result = mutationTransformer.transformBytecode(bytes);

		ClassVisitor cv = new CheckClassAdapter(cw);
		// cv = new MutationsClassAdapter(cv, BytecodeInfo.read());

		// cv = new JumpsClassAdapter(cw);
		// cr.accept(cv, ClassReader.SKIP_FRAMES);
		// byte[] result = cw.toByteArray();
		try {
			FileUtils.writeByteArrayToFile(transformedFile, result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result.length + "  " + bytes.length);
		String errors = AsmUtil.checkClass(bytes);
		System.out.println("---------------------------------------");
		System.out.println(errors);
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