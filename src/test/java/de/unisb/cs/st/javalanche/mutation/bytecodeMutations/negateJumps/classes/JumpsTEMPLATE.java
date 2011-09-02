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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.negateJumps.classes;

public class JumpsTEMPLATE {

	public int m1(int value) {
		if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	public int m2(int value) {
		if (value > 0) {
			return 1;
		} else if (value == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	public int m3(int value) {
		int r = 0;
		if (r >= 0) {
			return -1 * value;
		}
		return value;
	}

	public boolean m4(int a) {
		Integer i1 = new Integer(a);
		Integer i2 = new Integer(12);
		if (a > -1) {
			i2 = i1;
		}
		if (i1 == i2) {
			return true;
		}
		return false;
	}
}
