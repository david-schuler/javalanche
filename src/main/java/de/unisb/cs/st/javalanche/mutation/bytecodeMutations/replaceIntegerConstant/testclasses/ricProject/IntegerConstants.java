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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceIntegerConstant.testclasses.ricProject;

public class IntegerConstants {

	public int method1(int i) {
		int myconst = 5;
		return i * myconst;
	}

	public long method2() {
		Long l = 100l;
		return 5 * l;
	}

	public boolean method3(int arg) {
		boolean b = true;
		int magicValue = 5;
		return arg == magicValue && b;
	}
}
