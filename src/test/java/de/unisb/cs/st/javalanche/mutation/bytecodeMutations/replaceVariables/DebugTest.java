package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BaseBytecodeTest;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes.DebugTEMPLATE;

public class DebugTest extends BaseBytecodeTest {

	private Class<?> clazz;

	public DebugTest() throws Exception {
		super(DebugTEMPLATE.class);
		verbose = true;
		clazz = prepareTest();
	}

	@Test
	public void test() throws Exception {

		Method m1 = clazz.getMethod("putAll", Map.class);
		Map m = new HashMap<String, String>();
		m.put("A", "a");
		checkUnmutated(m, 1, m1, clazz);
	}

}
