package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationsCollectorClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.RemoveSystemExitTransformer.RemoveSystemExitClassAdapter;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes.SystemExit2TEMPLATE;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes.SystemExitTEMPLATE;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties;
import de.unisb.cs.st.javalanche.mutation.properties.TestProperties.TestClass;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.testutil.TestUtil;
import de.unisb.cs.st.javalanche.mutation.util.AsmUtil;

public class RemoveSystemExit2BytecodeTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public static final String SYS_EXIT_CLASS_NAME = "org.jgap.gp.impl.GPGenotype2";

	public static final TestClass SYS_EXIT_CLASS = new TestClass(
			SYS_EXIT_CLASS_NAME);

	public RemoveSystemExit2BytecodeTest() throws Exception {
		super(SystemExit2TEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void testM1() throws Exception {
		Method m1 = clazz.getMethod("run");
		try {
			checkUnmutated(null, m1, clazz);
			fail("Expected exception");
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			assertThat(cause.getMessage(),
					Matchers.containsString("Replaced System.exit()"));
		}
	}

	@Test
	public void testPossibilities() throws IOException {
		byte[] classBytes = SYS_EXIT_CLASS.getClassBytes();
		String className = SYS_EXIT_CLASS.getClassName();
		QueryManager.deleteMutations(className);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		RemoveSystemExitClassAdapter rca = new RemoveSystemExitClassAdapter(cw);
		ClassReader cr = new ClassReader(classBytes);
		cr.accept(rca, ClassReader.EXPAND_FRAMES);
		AsmUtil.checkClass(cw.toByteArray());
		
	}
}
