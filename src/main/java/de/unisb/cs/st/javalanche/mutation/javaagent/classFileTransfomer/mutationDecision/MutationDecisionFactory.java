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

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.properties.JavalancheConfiguration;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class MutationDecisionFactory {


	public static final MutationDecision SCAN_DECISION = new MutationDecision() {

		JavalancheConfiguration configuration = ConfigurationLocator
				.getJavalancheConfiguration();

		public boolean shouldBeHandled(String classNameWithDots) {
			if (classNameWithDots == null) {
				return false;
			}
			String ignorePattern = configuration.getIgnorePattern();
			if (classNameWithDots.startsWith("java")
					|| classNameWithDots.startsWith("sun")
					|| classNameWithDots.startsWith("org.aspectj.org.eclipse")
					|| classNameWithDots
							.startsWith("org.mozilla.javascript.gen.c")
					|| (ignorePattern.length() > 0 && classNameWithDots
							.matches(ignorePattern))) {
				return false;
			}
			if (Excludes.getInstance().shouldExclude(classNameWithDots)) {
				return false;
			}
			if (classNameWithDots.startsWith(configuration.getProjectPrefix())) {
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
				return false;
			}

		};

	}

}
