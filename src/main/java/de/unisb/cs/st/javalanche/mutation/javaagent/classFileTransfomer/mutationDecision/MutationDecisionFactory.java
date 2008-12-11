package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

import java.util.Collection;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationDecisionFactory {

	private static String TEST_PACKAGE = ".testclasses";

	public static final MutationDecision SCAN_DECISION = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots == null) {
				return false;
			}
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")
					|| classNameWithDots.startsWith("org.aspectj.org.eclipse")
					|| classNameWithDots.startsWith("org.mozilla.javascript.gen.c")) {
				return false;
			}
			if (classNameWithDots.contains(TEST_PACKAGE)
					&& !classNameWithDots.endsWith("Test")) {

				return false;
			}
			if (classNameWithDots.contains(".test.")) {
				return false;
			}
			if (classNameWithDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
				if (QueryManager.hasMutationsforClass(classNameWithDots)) {
					return false;
				}
				return true;
			}
			return false;
		}
	};

	public static MutationDecision getStandardMutationDecision(
			final Collection<String> classesToMutate) {
		return new MutationDecision() {

			public boolean shouldBeHandled(String classNameWithDots) {
				if (classesToMutate.contains(classNameWithDots)) {
					return true;
				}
				if (classNameWithDots.contains(TEST_PACKAGE)
						&& !classNameWithDots.endsWith("Test")) {
					// Hack for unittesting //TODO ??? return false
					return true;
				}
				return false;
			}

		};

	}

}
