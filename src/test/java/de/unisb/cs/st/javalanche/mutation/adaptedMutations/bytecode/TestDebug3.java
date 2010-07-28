package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class TestDebug3 extends BaseTestJump {

	private static Class<?> classUnderTest = Debug3TEMPLATE.class;

	public TestDebug3() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();
		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		System.out.println("MUTATIONS: " + mutationsForClass);
		Method method = clazz.getMethod("m", int.class);
		assertNotNull(method);
		// checkUnmutated(10, 13, method, clazz);
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
	public static void redefineMutations(String testClassName) {
		List<Long> ids = new ArrayList<Long>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation as m where m.className=:clname");
		query.setString("clname", testClassName);
		List<Mutation> mList = query.list();
		for (Mutation m : mList) {
			if (m.getMutationType() == MutationType.ADAPTED_NEGATE_JUMP_IN_IF) {
				System.out.println();
				ids.add(m.getId());
			}
		}
		tx.commit();
		session.close();
		StringBuilder sb = new StringBuilder();
		for (Long l : ids) {
			sb.append(l + "\n");
		}
		File file = new File(ByteCodeTestUtils.DEFAULT_OUTPUT_FILE);
		Io.writeFile(sb.toString(), file);
		MutationProperties.MUTATION_FILE_NAME = file.getAbsolutePath();
	}
}
