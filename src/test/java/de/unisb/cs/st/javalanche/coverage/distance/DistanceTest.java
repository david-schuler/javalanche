package de.unisb.cs.st.javalanche.coverage.distance;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.unisb.cs.st.javalanche.coverage.distance.classes.A;
import de.unisb.cs.st.javalanche.coverage.distance.classes.B;
import de.unisb.cs.st.javalanche.coverage.distance.classes.B2;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.DistanceTransformer.ClassEntry;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import static org.hamcrest.Matchers.*;

public class DistanceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDistance() throws IOException, IllegalClassFormatException {
		DistanceTransformer dt = new DistanceTransformer();
		transformClasses(dt, A.class, B.class, B2.class);

		ConnectionData connectionData = dt.getConnectionData();
		Set<ClassEntry> classes = dt.getClasses();
		Hierarchy hierarchy = Hierarchy.fromSet(classes);
		DistanceGraph dg = new DistanceGraph(connectionData, hierarchy);

		String classNameA = A.class.getCanonicalName();
		Set<String> allSupersA = hierarchy.getAllSupers(classNameA);
		assertEquals(1, allSupersA.size());
		String desc = "()V";
		MethodDescription md1 = new MethodDescription(classNameA, "m1", desc);
		MethodDescription md2 = new MethodDescription(classNameA, "m2", desc);
		MethodDescription md3 = new MethodDescription(classNameA, "m3", desc);
		MethodDescription md4 = new MethodDescription(classNameA, "m4", desc);
		expectDistance(0, dg, md1, md1);
		expectDistance(1, dg, md1, md2);
		expectDistance(1, dg, md2, md1);
		expectDistance(2, dg, md3, md1);
		expectDistance(2, dg, md1, md3);
		expectDistance(1, dg, md2, md3);

		expectDistance(0, dg, md3, md3);

		String classNameB = B.class.getCanonicalName();
		Set<String> allSupersB = hierarchy.getAllSupers(classNameB);
		assertEquals(2, allSupersB.size());
		assertThat(classNameA, isIn(allSupersB));

		MethodDescription mdb1 = new MethodDescription(classNameB, "m1", desc);
		MethodDescription mdb3 = new MethodDescription(classNameB, "m3", desc);
		MethodDescription mdb4 = new MethodDescription(classNameB, "m4", desc);

		expectDistance(0, dg, mdb1, mdb1);
		expectDistance(1, dg, mdb1, mdb3);
		expectDistance(1, dg, mdb3, mdb1);
		expectDistance(4, dg, md4, mdb1);

		String classNameB2 = B2.class.getCanonicalName();
		Set<String> allSupersB2 = hierarchy.getAllSupers(classNameB2);
		assertEquals(2, allSupersB2.size());
		assertThat(classNameA, isIn(allSupersB2));

		MethodDescription mdb2_1 = new MethodDescription(classNameB, "m1", desc);
		MethodDescription mdb2_4 = new MethodDescription(classNameB, "m4", desc);

		expectDistance(-1, dg, mdb2_4, mdb2_1);

	}

	private void transformClasses(DistanceTransformer dt, Class<?>... classes)
			throws IOException, IllegalClassFormatException {
		for (Class<?> class1 : classes) {
			String path = class1.getCanonicalName().replace('.', '/')
					+ ".class";
			InputStream is = class1.getClassLoader().getResourceAsStream(path);
			byte[] byteArray = IOUtils.toByteArray(is);
			MutationProperties.PROJECT_PREFIX = "de.unisb.cs.st.javalanche.coverage.distance.classes";
			dt.transform(null, class1.getCanonicalName(), null, null, byteArray);
		}
	}

	private void expectDistance(int expectedDistance, DistanceGraph dg,
			MethodDescription start, MethodDescription end) {
		int distance = dg.getDistance(start, end);
		assertEquals("Different distance between nodes expected."
				+ "\n Start node: " + start + "\n End node:  " + end + "\n",
				expectedDistance, distance);
	}
}
