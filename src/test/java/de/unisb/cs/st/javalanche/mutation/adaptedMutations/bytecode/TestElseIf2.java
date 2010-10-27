package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode;

import static de.unisb.cs.st.javalanche.mutation.bytecodeMutations.ByteCodeTestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class TestElseIf2 extends BaseTestJump {

	private static Class<?> classUnderTest = ElseIF2TEMPLATE.class;

	public TestElseIf2() {
		super(classUnderTest);
	}

	@Test
	public void test() throws Exception {
		Class<?> clazz = prepareTest();

		List<Mutation> mutationsForClass = QueryManager
				.getMutationsForClass(className);
		Method method = clazz.getMethod("m", int.class);
		assertNotNull(method);
		// checkUnmutated(10, 40, method, clazz);
		System.out.println(Joiner.on("\n").join("\n", mutationsForClass));
	}

	public Class<?> prepareTest() throws Exception {
		String filename = templateFileName;
		File outDir = new File(OUT_DIR, packageName.replace('.', '/'));
		File classFile = new File(outDir, simpleClassName + ".class");
		File outFile = createTmpJavaFile(filename, outDir);
		compileTest(outFile);
		deleteMutations(className);
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
			// if (m.getMutationType() == MutationType.ADAPTED_SKIP_ELSE)
				ids.add(m.getId());
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

}
