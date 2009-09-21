/*
* Copyright (C) 2009 Saarland University
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

/**
 * Interface provides a method that decides whether a class should be
 * transformed during runtime or not.
 *
 * @author David Schuler
 *
 */
public interface MutationDecision {

	/**
	 * Decides whether a class should be transformed.
	 * @param classNameWithDots
	 *            class name with dots as separator.
	 * @return True, if the class should be transformed
	 */
	public boolean shouldBeHandled(String classNameWithDots);
}
