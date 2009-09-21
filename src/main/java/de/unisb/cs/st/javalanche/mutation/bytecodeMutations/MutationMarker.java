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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

/**
 * Class used to mark mutated statements in the bytecode. When mutated code is
 * inserted a marker is added at the beginning and the end of the inserted
 * method.
 * 
 * @author David Schuler
 * 
 */
public class MutationMarker {

	/**
	 * Flag that indicates whether this marker marks the start (true) or the end
	 * (false) of a inserted code block.
	 */
	private boolean isStart;

	public MutationMarker(boolean isStart) {
		this.isStart = isStart;
	}

	/**
	 * @return true, if the marker marks the start, else false.
	 */
	public boolean isStart() {
		return isStart;
	}

}
