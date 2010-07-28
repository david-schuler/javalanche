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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.classes;

public class ArithmeticTEMPLATE {
	static {
		System.out.println("Arithmetic - test class loaded");
	}

	public int m1(int i) {
		return i + i;
	}

	public int m2(int i) {
		return i - i;
	}

	public int m3(int i) {
		i *= -1;
		return i;
	}

	public int m4(int i) {
		int m = i / 5;
		return m;
	}

	public boolean m5(int i) {
		return i % 10 == 0;
	}

}
