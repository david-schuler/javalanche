/*
 * Copyright (C) 2011 Saarland University
 * 
 * This file is part of Javalanche.
 * 
 * Javalanche is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Javalanche is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision;

import java.util.Collection;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationDecisionFactory {

	// Hack for unit testing
	private static String TEST_PACKAGE = ".testclasses";

	public static final MutationDecision SCAN_DECISION = new MutationDecision() {

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots == null) {
				return false;
			}
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")
					|| classNameWithDots.startsWith("org.aspectj.org.eclipse")
					|| classNameWithDots
							.startsWith("org.mozilla.javascript.gen.c")
					|| (MutationProperties.IGNORE_PREFIX.length() > 0 && classNameWithDots
							.matches(MutationProperties.IGNORE_PREFIX))) {
				return false;
			}
			if (classNameWithDots.contains(TEST_PACKAGE)) {
				return false;
			}
			if (Excludes.getInstance().shouldExclude(classNameWithDots)) {
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
					// Hack for unittesting
					return true;
				}
				return false;
			}

		};

	}

}
